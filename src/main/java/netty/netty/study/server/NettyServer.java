package netty.netty.study.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import netty.netty.study.server.handler.NettyServerBasicHandler;

import java.net.InetSocketAddress;

import static io.netty.channel.ChannelOption.SO_REUSEADDR;


public class NettyServer {
    private final int port;
    private ServerBootstrap serverBootstrap;
    private EventLoopGroup group;
    private Channel channel;

    public NettyServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {

        new NettyServer(1024).start();

    }

    public void start() throws Exception {

        group = new NioEventLoopGroup();

        try {
            serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .option(SO_REUSEADDR, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new NettyServerBasicHandler());
                        }
                    });

            ChannelFuture future = serverBootstrap.bind().sync();

            if (future.isSuccess()) {
                this.channel = future.channel();
            } else {
                group.shutdownGracefully().sync();
            }

        } catch (Exception e){

            e.printStackTrace();

            group.shutdownGracefully().sync();
        }
    }

    public void end() {

        try {
            group.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
