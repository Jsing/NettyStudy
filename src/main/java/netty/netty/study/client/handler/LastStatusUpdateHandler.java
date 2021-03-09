package netty.netty.study.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import netty.netty.study.data.LastStatus;

@AllArgsConstructor
public class LastStatusUpdateHandler extends SimpleChannelInboundHandler<String> {
    final private LastStatus updateListener;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        updateListener.set(msg);
        System.out.println("[Client] msg received : " + msg);
    }
}
