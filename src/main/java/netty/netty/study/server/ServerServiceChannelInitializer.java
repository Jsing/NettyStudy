package netty.netty.study.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import netty.netty.study.server.handler.ClientServiceHandler;


public class ServerServiceChannelInitializer extends ChannelInitializer<SocketChannel> {

    final private ClientActiveListener activeListener;

    public ServerServiceChannelInitializer(ClientActiveListener activeListener) {
        this.activeListener = activeListener;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        socketChannel.pipeline().addLast(new ClientServiceHandler(activeListener));
        socketChannel.pipeline().addLast(new StringEncoder());
    }

}
