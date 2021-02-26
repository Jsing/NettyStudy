package netty.netty.study.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import lombok.AllArgsConstructor;

import java.util.List;


@AllArgsConstructor
enum OpcodeLengthMap {
    RESPONSE ("STA", 10),
    COMMAND ("CMD", 20);

    private final String indicator;
    private final int length;
}


public class OpcodeBasedFrameDecoder extends ReplayingDecoder {


    ByteProcessor FIND_END = new ByteProcessor() {
        @Override
        public boolean process(byte value) {
            return value != (byte)'}';
        }
    };

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        final char responseIndicator = '?';
        final

        CharSequence header = in.readCharSequence(6, CharsetUtil.UTF_8);

        if (header.charAt(2) == responseIndicator) {
            //in.readCharSequence(
        }


    }
}
