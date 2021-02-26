package netty.netty.study.client.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.AllArgsConstructor;
import netty.netty.study.client.InactiveListener;
import netty.netty.study.client.handler.ClientWorkerHandler;
import netty.netty.study.data.LastStatus;
import netty.netty.study.data.Updatable;


@AllArgsConstructor
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    final private LastStatus updateListener;
    final private InactiveListener inactiveListener;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(
                // 수신
                new StringDecoder(CharsetUtil.UTF_8),
                new ClientWorkerHandler(this.updateListener, inactiveListener),

                // 전송
                new StringEncoder(CharsetUtil.UTF_8));
    }

}
