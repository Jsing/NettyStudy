package netty.netty.study.client.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.AllArgsConstructor;
import netty.netty.study.client.handler.NettyClientBasicHandler;
import netty.netty.study.data.Updatable;


@AllArgsConstructor
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    final private Updatable<String> updateListener;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(
                // Up-stream
                new LineBasedFrameDecoder(1500, true, true),
                new StringDecoder(CharsetUtil.UTF_8),
                new NettyClientBasicHandler(this.updateListener),

                // Down-stream
                new StringEncoder(CharsetUtil.UTF_8));

    }

}
