package netty.netty.study.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import netty.netty.study.server.handler.ClientServiceHandler;


public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    final private ClientActiveListener listener;

    public ClientChannelInitializer(ClientActiveListener listener) {
        this.listener = listener;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        socketChannel.pipeline().addLast(new ClientServiceHandler(listener));
    }

}
