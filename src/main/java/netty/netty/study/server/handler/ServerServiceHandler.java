package netty.netty.study.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty.netty.study.data.LastStatus;
import netty.netty.study.data.Updatable;
import netty.netty.study.server.ClientActiveListener;

import java.net.InetSocketAddress;

public class ServerServiceHandler extends SimpleChannelInboundHandler {

    final private ClientActiveListener activeListener;
    private LastStatus updateListener;

    public ServerServiceHandler(ClientActiveListener activeListener) {
        this.activeListener = activeListener;
    }

    public void setUpdateListener(LastStatus updateListener) {
        this.updateListener = updateListener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        activeListener.clientActivated(ctx.channel(), (InetSocketAddress) ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object in) throws Exception {
        String msg = (String) in;
        updateListener.set(msg);
        //System.out.println("[Server] msg received : \"" + msg + "\"");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        cause.printStackTrace();
        ctx.close();

        //super.exceptionCaught(ctx, cause);
    }
}
