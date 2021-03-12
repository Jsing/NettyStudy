package netty.netty.study.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.AllArgsConstructor;
import netty.netty.study.client.ChannelStatusListener;

@AllArgsConstructor
public class ChannelStatusMonitor extends ChannelInboundHandlerAdapter {
    final private ChannelStatusListener channelStatusListener;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channelStatusListener.channelActive();
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channelStatusListener.channelInactive();
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        channelStatusListener.exceptionCaught(cause);
        ctx.close(); // TODO 여기서 하는 것이 옳은지 검토해 보세요.
        super.exceptionCaught(ctx, cause);
    }
}
