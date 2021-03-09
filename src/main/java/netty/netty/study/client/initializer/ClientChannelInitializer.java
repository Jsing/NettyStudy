package netty.netty.study.client.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.AllArgsConstructor;
import netty.netty.study.client.ChannelStatusListener;
import netty.netty.study.client.handler.HelloStarterHandler;
import netty.netty.study.client.handler.LastStatusUpdateHandler;
import netty.netty.study.data.LastStatus;


@AllArgsConstructor
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    final private LastStatus updateListener;
    final private ChannelStatusListener channelStatusListener;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(
                // 수신
                new StringDecoder(CharsetUtil.UTF_8),
                new HelloStarterHandler(),
                new LastStatusUpdateHandler(this.updateListener),

                // 전송
                new StringEncoder(CharsetUtil.UTF_8));
    }

}
