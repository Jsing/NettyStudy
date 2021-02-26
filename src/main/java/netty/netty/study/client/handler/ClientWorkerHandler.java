package netty.netty.study.client.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import netty.netty.study.client.InactiveListener;
import netty.netty.study.data.LastStatus;
import netty.netty.study.data.Updatable;


public class ClientWorkerHandler extends SimpleChannelInboundHandler<String> {

    final private LastStatus updateListener;
    final private InactiveListener inactiveListener;

    public ClientWorkerHandler(LastStatus updateListener, InactiveListener inactiveListener) {
        this.updateListener = updateListener;
        this.inactiveListener = inactiveListener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello Server", CharsetUtil.UTF_8));
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("[Client] channelInactive");
        inactiveListener.channelInactiveOccurred();
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        updateListener.set(msg);
        System.out.println("[Client] msg received : " + msg);
    }
}
