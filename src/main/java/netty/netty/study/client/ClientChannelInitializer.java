package netty.netty.study.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;
import netty.netty.study.client.handler.NettyClientBasicHandler;
import netty.netty.study.dto.Updatable;
import netty.netty.study.server.ClientActiveListener;
import netty.netty.study.server.handler.ClientServiceHandler;

public class ClientChannelInitializer  extends ChannelInitializer<SocketChannel> {

    final private Updatable<String> updateListener;

    public ClientChannelInitializer(Updatable<String> updateListener) {
        this.updateListener = updateListener;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(
                new LineBasedFrameDecoder(1500),
                new StringDecoder(CharsetUtil.UTF_8),
                new NettyClientBasicHandler(this.updateListener)
        );

    }

}
