package netty.netty.study.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class NettyServerBasicHandler extends ChannelInboundHandlerAdapter {

    private Channel channel;

    public void config() {

    }

    public void writeMessage(byte[] msg) {

        channel.writeAndFlush(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        channel = ctx.channel();

        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf in = (ByteBuf) msg;
        System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));
        //ctx.write(in); // this is asyncronous.

        // @TODO : super.channelRead()가 호출되면 레퍼런스 관련 오류가 발생하는데 원인 분석  필요함
        // @TODO : 그리고 msg 레퍼런스 카운트를 감소시키는 것으로 보인다.
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        //ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);

        //super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        cause.printStackTrace();
        ctx.close();

        //super.exceptionCaught(ctx, cause);
    }
}
