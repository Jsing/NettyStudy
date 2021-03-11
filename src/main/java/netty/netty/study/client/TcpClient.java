package netty.netty.study.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import netty.netty.study.client.handler.ChannelStatusMonitor;
import netty.netty.study.data.ConnectionTag;
import netty.netty.study.utils.StackTraceUtils;
import org.springframework.util.Assert;

import java.net.InetSocketAddress;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Netty 기반의 TCP Client 서비스를 제공합니다. TcpClient 는 Netty 종속적인 대부분의 구현을 캡슐화합니다.
 * 1. 초기화
 * - TcpClient 는 응용 계층 프로토콜에 따라서 적절한 채널 파이프라인 설정이 필요합니다.
 * - 이를 위해 반드시 적절한 ChannelInitializer 와 함께 init()함수를 호출하여 TcpClient 초기화를 수행해야 합니다.
 * 2. 연결
 * - 동기화 메쏘드 connect() 함수를 통해서 한 번의 연결 시도를 수행할 수 있습니다.
 * - 비동기 메쏘드 connectUntilSuccess() 함수를 통해서 연결이 성공할 때 까지 연결을 시도하는 태스크를 실행할 수 있습니다.
 * 3. 종료
 * - TcpClient 사용 이후 반드시 destroy() 함수를 호출하여 안전하게 자원을 해제해야 합니다.
 * 4. 연결 자동 복구
 * - TcpClient 는 InactiveListener 인터페이스를 구현합니다.
 * - 채널 파이프라인에 포함된 특정 ChannelInboundHandler 에서 inactive 이벤트를 발생 시, TcpClient에서 구현한 channelInactiveOccurred
 * Listener 함수를 호출해 주어야 합니다.
 * TODO : 예외 발생 시, 이벤트 로그를 Service Layer 에 전달하는 로직 추가
 *
 * @author Jsing
 * @see ChannelStatusListener
 */
@Slf4j
public class TcpClient implements ChannelStatusListener {
    private final Bootstrap bootstrap = new Bootstrap();
    private final CopyOnWriteArrayList<ScheduledFuture> taskFutures = new CopyOnWriteArrayList<>();
    private Channel channel;
    private ConnectionTag connectionTag;
    private CountDownLatch cancelConnectUntilSuccess; // TODO : is this thread-safe???
    private boolean shouldRecoveryChannel = true;

    public void init(ChannelInitializer<?> channelInitializer) {
        Assert.isNull(bootstrap.config().group(), "you have to call this function in postConstruct()");

        final int connectTimeoutMillis = 3000;
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMillis)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(channelInitializer);
    }

    public void destroy() {
        try {
            if (channel != null && channel.eventLoop() != null) {
                channel.eventLoop().shutdownGracefully().sync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean connect(ConnectionTag connectionTag) {
        Assert.state(!StackTraceUtils.getCallerFunc().contentEquals("postConstruct"), "you have to call connectUntilSuccess()");
        Assert.state(!StackTraceUtils.getCallerFunc().contentEquals("connect"), "you have to call connectUntilSuccess()");

        this.connectionTag = connectionTag;
        this.disconnect();
        return connectOnce(connectionTag);
    }

    public void connectUntilSuccess(ConnectionTag connectionTag) {
        this.connectionTag = connectionTag;
        this.disconnect();

        cancelConnectUntilSuccess = new CountDownLatch(1);
        bootstrap.config().group().schedule(() -> {
            boolean connected = false;
            do {
                if (cancelConnectUntilSuccess.await(100, TimeUnit.MILLISECONDS)) {
                    break;
                }
                connected = connectOnce(connectionTag);
            } while (!connected);
            cancelConnectUntilSuccess.countDown();
            return null;
        }, 0, TimeUnit.MILLISECONDS);

        try {
            cancelConnectUntilSuccess.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized boolean connectOnce(ConnectionTag connectionTag) {
        ChannelFuture channelFuture = null;
        try {
            channelFuture = bootstrap.connect(connectionTag.getIp(), connectionTag.getPort()).sync();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (!channelFuture.isSuccess()) {
            return false;
        }

        shouldRecoveryChannel = true;
        channel = channelFuture.channel();

        channel.pipeline().addLast(new ChannelStatusMonitor(this));
        return true;
    }

    public void disconnect() {
        shouldRecoveryChannel = false; // 명시적으로 연결을 끊는 경우 연결 복구 로직 OFF
        try {
            if (cancelConnectUntilSuccess != null && cancelConnectUntilSuccess.getCount() != 0) {
                cancelConnectUntilSuccess.countDown();
            }

            if (!taskFutures.isEmpty()) {
                taskFutures.forEach((future) -> future.cancel(true));
                taskFutures.clear();
            }

            if (channel != null) {
                channel.close().sync();
                channel = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ChannelFuture send(Object message) {
        Assert.notNull(channel, "channel must be not null");
        return channel.writeAndFlush(message);
    }

    public ChannelFuture sendSync(Object message) {
        Assert.notNull(channel, "channel must be not null");
        ChannelFuture channelFuture = channel.writeAndFlush(message);
        try {
            channelFuture.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channel.writeAndFlush(message);
    }

    public void scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
        ScheduledFuture future = channel.eventLoop().scheduleAtFixedRate(task, initialDelay, period, unit);
        taskFutures.add(future);
    }

    public boolean isActive() {
        if (channel == null) {
            return false;
        }
        return channel.isActive();
    }

    public InetSocketAddress getLocalAddress() {
        if (channel == null) {
            return null;
        }
        return (InetSocketAddress) channel.localAddress();
    }

    @Override
    public void channelActive() {
        // TODO : 연결에 대한 이벤트 메시지 처리하기
    }

    @Override
    public void channelInactive() {
        if (shouldRecoveryChannel) {
            connectUntilSuccess(this.connectionTag);
        }
        // TODO : 연결에 대한 이벤트 메시지 처리하기
    }

    @Override
    public void exceptionCaught() {
        // TODO : 채널 예외에 대한 이벤트 메시지 처리하기
    }
}
