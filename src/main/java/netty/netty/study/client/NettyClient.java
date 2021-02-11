package netty.netty.study.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.netty.study.client.handler.NettyClientBasicHandler;

import java.net.InetSocketAddress;

/**
 * @TODO : Bootstrap 재연결 시에도 사용 가능한지 확인
 * @TODO : Channel 재연결 시에도 사용 가능한지 확인
 */
public class NettyClient {

    private Bootstrap bootstrap;
    private Channel channel;

    public void init() {

        bootstrap = new Bootstrap();

        NioEventLoopGroup group = new NioEventLoopGroup();

        try {

            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new NettyClientBasicHandler());

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            group.shutdownGracefully();
        }
    }

    public boolean connect(String ip, int port) {

        assert this.channel != null : "channel is null";

        try {
            if (this.channel.isActive()) {
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

}
