package netty.netty.study.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;
import netty.netty.study.client.handler.NettyClientBasicHandler;
import netty.netty.study.dto.Copyable;
import netty.netty.study.dto.LastStatus;

import java.net.InetSocketAddress;

/**
 * @TODO : Bootstrap 재연결 시에도 사용 가능한지 확인
 * @TODO : Channel 재연결 시에도 사용 가능한지 확인
 */
public class NettyClient {


    private LastStatus<String> lastStatus;
    private Bootstrap bootstrap;
    private Channel channel;

    public NettyClient() {
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
                    .handler(new ClientChannelInitializer(lastStatus));

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
