package netty.netty.study.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.AllArgsConstructor;
import netty.netty.study.client.ChannelExceptionListener;

@AllArgsConstructor
public class ChannelExceptionMonitor extends ChannelInboundHandlerAdapter {
    final private ChannelExceptionListener channelExceptionListener;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channelExceptionListener.channelInactive();
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        channelExceptionListener.exceptionCaught(cause);
        ctx.close(); // TODO 여기서 하는 것이 옳은지 검토해 보세요.
        super.exceptionCaught(ctx, cause);
    }
}
