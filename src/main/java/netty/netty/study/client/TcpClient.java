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
     * EventLoopGroup 쓰레드를 통해 비동기적으로 연결에 성공 할 때 까지 연결 시도를 수행합니다.
     * EventLoopGroup 쓰레드가 여러 채널에 의해 공유되고 있는 경우 사용해서는 안됩니다.
     *
     * @param ip
     * @param port
     */
    // TODO 테스트 및 리팩토링, 안정화가 필요합니다.
    public void connectUntilSuccess(String ip, int port) {
        this.serverIp = ip;
        this.serverPort = port;

        // TODO 반드시 동시에 여러번 호출되지 않음을 확인하는 Assert 문이라든지 방어 코드가 들어가야할 것으로 보임

        connectUntilSuccessFuture = bootstrap.config().group().submit(() -> {
            boolean connected = false;
            do {
                connected = connectOnce();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (!connected);
        });

    }

    public boolean connectOnce(String ip, int port) {
        this.serverIp = ip;
        this.serverPort = port;

        return connectOnce();
    }

    private boolean connectOnce() {
        this.disconnect();

        ChannelFuture channelFuture = null;
        try {
            channelFuture = bootstrap.connect(this.serverIp, this.serverPort).syncUninterruptibly();
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
            // TODO 테스트 및 리팩토링, 안정화가 필요합니다.
            if (connectUntilSuccessFuture!=null && !connectUntilSuccessFuture.isDone()) {
                connectUntilSuccessFuture.cancel(true);
            }
            // TODO ----

            if (channel != null) {
                channel.close().syncUninterruptibly();
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

    @Override
    public void channelInactiveOccurred() {
        connectUntilSuccess(serverIp, serverPort);
    }
}
