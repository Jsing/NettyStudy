package netty.netty.study.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ResourceLeakDetector;

import java.util.List;

public class ByteToMessageDecoderTest extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ByteBuf readableBuf = in.readBytes(in.readableBytes());
        out.add(readableBuf);

        //readableBuf.release();
    }
}
