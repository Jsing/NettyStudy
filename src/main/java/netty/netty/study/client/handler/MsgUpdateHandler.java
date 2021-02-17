package netty.netty.study.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty.netty.study.data.MyMessage;

public class MsgUpdateHandler extends SimpleChannelInboundHandler<MyMessage> {

    private Updatable<MyMessage>

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MyMessage myMessage) throws Exception {

    }
}
