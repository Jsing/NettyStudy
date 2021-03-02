package netty.netty.study.client.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.AllArgsConstructor;
import netty.netty.study.client.InactiveListener;
import netty.netty.study.data.LastStatus;
import netty.netty.study.data.Updatable;

@AllArgsConstructor
public class LastStatusUpdateHandler extends SimpleChannelInboundHandler<String> {
    final private LastStatus updateListener;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        updateListener.set(msg);
        System.out.println("[Client] msg received : " + msg);
    }
}
