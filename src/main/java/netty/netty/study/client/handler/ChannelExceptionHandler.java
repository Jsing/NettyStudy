package netty.netty.study.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.AllArgsConstructor;
import netty.netty.study.client.ChannelStatusListener;

@AllArgsConstructor
public class ChannelExceptionHandler extends ChannelInboundHandlerAdapter {
    final private ChannelStatusListener channelStatusListener;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channelStatusListener.channelInactive();
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        super.exceptionCaught(ctx, cause);
    }
}
