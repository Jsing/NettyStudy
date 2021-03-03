package netty.netty.study.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TcpClient implements InactiveListener {
    private final Bootstrap bootstrap = new Bootstrap();
    private Channel channel;
    private String serverIp;
    private int serverPort;
    private Future<?> connectUntilSuccessFuture;

    public void init(ChannelInitializer<?> channelInitializer) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .handler(channelInitializer);
    }

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
     * 서버(ip,port)와 연결을 한 번 수행하고 완료 시 결과를 반환합니다.
     *
     * @param ip 서버 IP
     * @param port 서버 Port
     * @return 서버와 연결 결과
     */
    public boolean connect(String ip, int port) {
        this.serverIp = ip;
        this.serverPort = port;

        this.disconnect();

        return connectOnce();
    }

    /**
     * 서버(ip,port)와 연결이 성공할 때 까지 반복해서 연결을 시도합니다.
     * 본 함수는 호출 쓰레드와 비동기적으로 수행되며 EventLoop 쓰레드에 의해 반복 작업을 수행합니다.
     * 만약 EventLoop 쓰레드를 여러 채널과 공유해서 사용한다면 아래 함수를 사용할 수 없습니다.
     * connectUntilSuccessFuture 를 통해 반복되는 연결 동작을 취소할 수 있습니다.
     *
     * @param ip 서버 IP
     * @param port 서버 Port
     */
    public void connectUntilSuccess(String ip, int port) {
        this.serverIp = ip;
        this.serverPort = port;

        this.disconnect();

        connectUntilSuccessFuture = bootstrap.config().group().scheduleAtFixedRate(() -> {
            connectOnce();

            System.out.println("connectOnce() invoked!");

        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    private boolean connectOnce() {
        ChannelFuture channelFuture = null;
        try {
            channelFuture = bootstrap.connect(this.serverIp, this.serverPort).sync();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (channelFuture.isSuccess() == false) {
            return false;
        }

        channel = channelFuture.channel();
        return true;
    }

    public void disconnect() {
        try {
            if (connectUntilSuccessFuture != null && !connectUntilSuccessFuture.isDone()) {
                connectUntilSuccessFuture.cancel(true);
                connectUntilSuccessFuture = null;
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
     * 서버와 연결이 끊어진 경우 비동기적으로 연결복구를 시작합니다.
     *
     * @see connectUntilSuccess()
     */
    @Override
    public void channelInactiveOccurred() {
       connectUntilSuccess(this.serverIp, this.serverPort);
    }
}
