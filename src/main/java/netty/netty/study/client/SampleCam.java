package netty.netty.study.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import netty.netty.study.data.Copyable;
import netty.netty.study.data.LastStatus;
import netty.netty.study.data.Updatable;

import java.net.InetSocketAddress;

/**
 * @TODO : Bootstrap 재연결 시에도 사용 가능한지 확인
 * @TODO : Channel 재연결 시에도 사용 가능한지 확인
 */
@Slf4j
public class SampleCam {

    private LastStatus<String> lastStatus;
    private Bootstrap bootstrap;
    private Channel channel;

    public SampleCam() {
        lastStatus = new LastStatus<String>();
    }

    public Copyable lastStatus() {

        return lastStatus; // @TODO 이렇게 참조를 반환하는 것이 옳은가?
    }

    public void init() {

        bootstrap = new Bootstrap();

        NioEventLoopGroup group = new NioEventLoopGroup();

        try {

            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChannelInitializer((Updatable<String>) lastStatus));

        } catch (Exception e) {
            e.printStackTrace();
            group.shutdownGracefully();
        }
    }

    public boolean connect(String ip, int port) {

        try {
            if (this.channel != null && this.channel.isActive()) {
                this.channel.close().sync();
            }

            ChannelFuture future = bootstrap.connect(ip, port).sync();

            if (future.isSuccess()) {
                this.channel = future.channel();
                return true;
            } else {
                bootstrap.config().group().shutdownGracefully();
                return false;
            }

        } catch (Exception e) {

            bootstrap.config().group().shutdownGracefully();
            e.printStackTrace();
            return false;

        }
    }

    public void disconnect() {

        try {
            bootstrap.config().group().shutdownGracefully().sync();

            if (channel != null && channel.isActive()) {
                channel.close().sync();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) channel.localAddress();
    }

}
