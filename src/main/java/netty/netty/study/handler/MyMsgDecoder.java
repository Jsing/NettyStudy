package netty.netty.study.handler;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import netty.netty.study.data.MyMessage;

import java.util.List;


public class MyMsgDecoder extends ByteToMessageDecoder {

    final private MyMessage myMessage = new MyMessage();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {

        myMessage.setOpcode(in.readInt());
        myMessage.setLength(in.readInt());
        myMessage.setContent((String)in.readCharSequence(myMessage.getLength(), CharsetUtil.UTF_8));

    }
}
