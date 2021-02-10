package netty.netty.study.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
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
    private final String host;
    private final int port;
    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private Channel channel;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void init() {

        group = new NioEventLoopGroup();

        try {
            bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
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

    public boolean connect() throws Exception {

        ChannelFuture future = bootstrap.connect().sync();

        try {
            if (future.isSuccess()) {
                this.channel = future.channel();
                return true;
            } else {
                group.shutdownGracefully();
                return false;
            }
        }catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void disconnect() {

        group.shutdownGracefully();
        try {
            if (channel != null && channel.isActive()) {
                channel.close().sync();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
