package netty.netty.study.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import netty.netty.study.server.handler.NettyServerBasicHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;


public class NettyServer {
    private final int port;
    private ServerBootstrap serverBootstrap;
    private EventLoopGroup clientAcceptGroup;
    private EventLoopGroup clientServiceGroup;
    private ConcurrentHashMap<String, ClientService> clientServiceMap;

    public NettyServer(int port) {
        this.port = port;
        clientServiceMap = new ConcurrentHashMap<>();
    }

    public static void main(String[] args) throws Exception {

        new NettyServer(1024).start();

    }

    public void start() throws Exception {

        clientAcceptGroup = new NioEventLoopGroup();
        clientServiceGroup = new NioEventLoopGroup();

        try {
            serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(clientAcceptGroup, clientServiceGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
//                    .option(SO_REUSEADDR, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                            Channel channel = socketChannel;
                            InetSocketAddress address = (InetSocketAddress)channel.remoteAddress();
                            clientServiceMap.put( address.toString(), new ClientService(channel));

                            System.out.println("registed client address s= " + address.toString());
                            socketChannel.pipeline().addLast(new NettyServerBasicHandler());
                            socketChannel.pipeline().addLast(new ChannelOutboundHandlerAdapter());
                        }
                    });

            ChannelFuture future = serverBootstrap.bind().sync();

            if (future.isSuccess()) {

            } else {
                clientAcceptGroup.shutdownGracefully().sync();
                clientServiceGroup.shutdownGracefully().sync();
            }

        } catch (Exception e) {

            e.printStackTrace();

            clientAcceptGroup.shutdownGracefully().sync();
            clientServiceGroup.shutdownGracefully().sync();
        }
    }

    public void end() {

        try {

            for (ClientService clientService : clientServiceMap.values()) {
                clientService.end();
            }

            clientAcceptGroup.shutdownGracefully().sync();
            clientServiceGroup.shutdownGracefully().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public ClientService getClientService(String address) {
        return clientServiceMap.get(address);
    }
}
