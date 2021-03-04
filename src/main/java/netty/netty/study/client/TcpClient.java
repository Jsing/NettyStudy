package netty.netty.study.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import netty.netty.study.data.ConnectionTag;
import org.springframework.lang.Nullable;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Netty 기반의 TCP Client 서비스를 제공합니다. TcpClient 는 Netty 종속적인 대부분의 구현을 캡슐화합니다.
 * 1. 초기화
 * - TcpClient 는 응용 계층 프로토콜에 따라서 적절한 채널 파이프라인 설정이 필요합니다.
 * - 이를 위해 반드시 적절한 ChannelInitializer 와 함께 init()함수를 통해 초기화 되어야 합니다.
 * 2. 연결
 * - 동기화 메쏘드 connect() 함수를 통해서 한 번의 연결 시도를 수행할 수 있습니다.
 * - 비동기 메쏘드 connectUntilSuccess() 함수를 통해서 연결이 성공할 때 까지 연결을 시도하는 태스크를 실행할 수 있습니다.
 * 3. 종료
 * - TcpClient 사용 이후 반드시 destroy() 함수를 호출하여 안전하게 자원을 해제해야 합니다.
 * 4. 연결 자동 복구
 * - TcpClient 는 InactiveListener 인터페이스를 구현합니다.
 * - 채널 파이프라인에 포함된 특정 ChannelInboundHandler 에서 inactive 이벤트를 발생 시, TcpClient에서 구현한 channelInactiveOccurred
 *   Listener 함수를 호출해 주어야 합니다.
 * TODO : 예외 발생 시, 이벤트 로그를 Service Layer 에 전달하는 로직 추가
 * @author Jsing
 * @see InactiveListener
 */
public class TcpClient implements InactiveListener {
    private final Bootstrap bootstrap = new Bootstrap();
    private Channel channel;
    private ConnectionTag connectionTag;
    /**
     * 비동기적으로 EventLoop 쓰레드에 의해 수행되는 connectUntilSuccess() 태스크 수행을 취소할 수 있습니다.
     */
    private CountDownLatch cancelConnectUntilSuccess; // TODO : is this thread-safe???

    /**
     * TcpClient 를 초기화 합니다. TcpClient 는 사용전 반드시 init() 함수를 통해 초기화되어야 합니다.
     * init() 이 호출되면 EventLoopGroup 이 생성되어 EventLoop 쓰레드가 생성되고 이후 연결을 위한 bootstrap 설정을 수행합니다.
     *
     * @param channelInitializer Channel Pipeline 설정
     */
    public void init(ChannelInitializer<?> channelInitializer) {
        final int connectTimeoutMillis = 3000;
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMillis)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(channelInitializer);
    }

    /**
     * TcpClient 에 할당된 자원을 해제합니다.
     */
    public void destroy() {
        try {
            if (channel != null && channel.eventLoop() != null) {
                channel.eventLoop().shutdownGracefully().sync();
            }
        } catch (Exception e) {
            // TODO :: 예외처리
            e.printStackTrace();
        }
    }

    /**
     * 서버와 연결을 (한번) 시도하고 결과를 반환합니다. 기존에 연결된 채널이 있으면 연결을 종료합니다.
     * connect() 메쏘드는 호출 쓰레드와 동기화되어 수행됩니다.
     *
     * @param connectionTag 연결 정보
     * @return true : connection success
     *         false : fail
     */
    public boolean connect(ConnectionTag connectionTag) {
        this.connectionTag = connectionTag;

        this.disconnect();

        return _connectOnce(connectionTag);
    }

    /**
     * 서버와 연결에 성공할 때 까지 반복해서 연결을 시도합니다. 기존에 연결된 채널이 있으면 연결을 종료합니다.
     * connectUntilSuccess() 메쏘드의 연결 수행 동작은 EventLoop 쓰레드에서 비동기적으로 수행됩니다.
     * connectUntilSuccess() 메쏘드는 EventLoop 쓰레드에 태스크를 할당하고 즉시 반환됩니다.
     *
     * @param connectionTag 연결 정보
     */
    public void connectUntilSuccess(ConnectionTag connectionTag) {
        this.connectionTag = this.connectionTag;

        this.disconnect();

        cancelConnectUntilSuccess = new CountDownLatch(1);
        bootstrap.config().group().submit(() -> {
            boolean connected = false;
            do {
                if (cancelConnectUntilSuccess.await(100, TimeUnit.MILLISECONDS)) {
                    break;
                }
                connected = _connectOnce(connectionTag);
            } while (!connected);
            cancelConnectUntilSuccess.countDown();
            cancelConnectUntilSuccess = null;
            return null;
        });
    }

    /**
     * connect()와 connectUntilSuccess() 메쏘드에서 서버와 연결을 (한번) 시도하는 공용 코드를 구현합니다.
     *
     * @param connectionTag 연결 정보
     * @return true : connection success
     *         false : fail
     */
    private synchronized boolean _connectOnce(ConnectionTag connectionTag) {
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

        channel = channelFuture.channel();
        return true;
    }

    /**
     * 서버와 연결을 끊습니다.
     * connectUntilSuccess()에 의해 비동기적으로 수행중인 연결 태스크가 살아 있으면 태스크를 종료합니다.
     */
    public void disconnect() {
        try {
            if (cancelConnectUntilSuccess != null) {
                cancelConnectUntilSuccess.countDown();
                cancelConnectUntilSuccess = null;
            }

            if (channel != null) {
                channel.close().sync();
                channel = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ChannelFuture sendSync(Object message) {
        ChannelFuture channelFuture = channel.writeAndFlush(message);
        try {
            channelFuture.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channel.writeAndFlush(message);
    }

    public ChannelFuture send(Object message) {
        return channel.writeAndFlush(message);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return channel.eventLoop().scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    public Future<?> submit(Runnable task) {
        return channel.eventLoop().submit(task);
    }

    public boolean isActive() {
        if (channel == null) {
            return false;
        }
        return channel.isActive();
    }

    @Nullable
    public InetSocketAddress getLocalAddress() {
        if (channel == null) {
            return null;
        }
        return (InetSocketAddress) channel.localAddress();
    }

    /**
     * 채널의 Inactive 이벤트 리스너를 구현합니다.
     * 채널의 연결이 끊어지면 connectUntilSuccess() 메쏘드를 호출하여 연결 복구 태스크를 실행합니다.
     */
    @Override
    public void channelInactiveOccurred() {
        connectUntilSuccess(this.connectionTag);
    }
}
