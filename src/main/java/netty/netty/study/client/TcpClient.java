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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TcpClient implements InactiveListener {
    private final Bootstrap bootstrap = new Bootstrap();
    private Channel channel;
    private String serverIp;
    private int serverPort;
    private CountDownLatch cancelConnectUntilSuccess;

    public void init(ChannelInitializer<?> channelInitializer) {
        final int connectTimeoutMillis = 3000;
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

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
            // TODO :: 예외처리
            e.printStackTrace();
        }
    }

    public boolean connect(String ip, int port) {
        this.serverIp = ip;
        this.serverPort = port;

        this.disconnect();

        return _connectOnce(ip, port);
    }

    public void connectUntilSuccess(String ip, int port) {
        this.serverIp = ip;
        this.serverPort = port;

        this.disconnect();

        cancelConnectUntilSuccess = new CountDownLatch(1);
        bootstrap.config().group().submit( () -> {
            boolean connected = false;
            do {
                if (cancelConnectUntilSuccess.await(100, TimeUnit.MILLISECONDS)) {
                    break;
                }
                connected = _connectOnce(ip, port);
            } while (!connected);
            cancelConnectUntilSuccess.countDown();
            cancelConnectUntilSuccess = null;
            return null;
        });
    }

    private synchronized boolean _connectOnce(String ip, int port) {
        ChannelFuture channelFuture = null;
        try {
            channelFuture = bootstrap.connect(ip, port).sync();
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

    @Override
    public void channelInactiveOccurred() {
        final int eachConnectTimeoutMillis = 3000;
        final int retryGapMillis = 1000;

        connectUntilSuccess(this.serverIp, this.serverPort);
    }
}
