package netty.netty.study.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.AllArgsConstructor;
import netty.netty.study.client.InactiveListener;

@AllArgsConstructor
public class ChannelExceptionHandler extends ChannelInboundHandlerAdapter {
    final private InactiveListener inactiveListener;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        inactiveListener.channelInactiveOccurred();
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        super.exceptionCaught(ctx, cause);
    }
}
