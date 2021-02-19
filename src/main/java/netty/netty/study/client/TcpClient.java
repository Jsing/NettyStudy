package netty.netty.study.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TcpClient {

    private Bootstrap bootstrap = new Bootstrap();
    private Channel channel;

    public void init(ChannelInitializer channelInitializer) {

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        // TODO : 연결 전 이벤트 설정이 가능한지 확인!
//        eventLoopGroup.scheduleAtFixedRate()

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(channelInitializer);
    }

    /**
     * @throws InterruptedException
     */
    public void end() throws InterruptedException {

        if (channel.eventLoop() != null) {
            channel.eventLoop().shutdownGracefully().sync();
        }
    }

    public boolean connect(String ip, int port) throws Exception {

        this.disconnect();

        ChannelFuture channelFuture = bootstrap.connect(ip, port).syncUninterruptibly();

        if (channelFuture.isSuccess() == false) {
            channel = null;
            return false;
        }

        channel = channelFuture.channel();
        return false;
    }

    /**
     * @throws InterruptedException
     */
    public void disconnect() throws InterruptedException {

        if (channel != null) {
            channel.close().sync();
        }
    }

    public boolean isActive() {

        return channel.isActive();
    }

    public ChannelFuture send(Object message) {

        return channel.writeAndFlush(message);
    }

    /**
     * @param command
     * @param initialDelay
     * @param period
     * @param unit
     * @return
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {

        return channel.eventLoop().scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    /**
     * @param task
     * @return
     */
    public Future<?> submit(Runnable task) {

        return channel.eventLoop().submit(task);
    }

    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) channel.localAddress();
    }

}
