package netty.netty.study.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;


public class TcpServer implements ClientActiveListener {
    private final int port;
    private ServerBootstrap serverBootstrap;
    private EventLoopGroup clientAcceptGroup;
    private EventLoopGroup clientServiceGroup;
    private ConcurrentHashMap<String, ServerService> clientServiceMap;
    private String tmp;

    public TcpServer(int port) {
        this.port = port;
        clientServiceMap = new ConcurrentHashMap<>();
    }

    public void start() throws Exception {

        clientAcceptGroup = new NioEventLoopGroup();
        clientServiceGroup = new NioEventLoopGroup();

        try {
            serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(clientAcceptGroup, clientServiceGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ServerServiceChannelInitializer((ClientActiveListener) this));

            ChannelFuture future = serverBootstrap.bind().sync();

            if (!future.isSuccess()) {

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
            for (ServerService serverService : clientServiceMap.values()) {
                serverService.end();
            }
            clientAcceptGroup.shutdownGracefully().sync();
            clientServiceGroup.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ServerService getServerService(String address) {
        assert tmp.equals(address) : "tmp is not the same with address";
        return clientServiceMap.get(address);
    }

    @Override
    public void clientActivated(Channel channel, InetSocketAddress clientAddress) {
        tmp = clientAddress.toString();
        clientServiceMap.put(clientAddress.toString(), new ServerService(channel));
        System.out.println("[Server] Activated client address = " + clientAddress.toString());
    }

    public void waitForClient(InetSocketAddress clientAddress) throws InterruptedException {
        Thread.sleep(1000); //@TODO : 개선할 수 있는 더 좋은 방법을 생각해 보자.
        return;
    }
}
