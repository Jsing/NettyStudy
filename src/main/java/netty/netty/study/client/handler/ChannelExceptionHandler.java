package netty.netty.study.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import netty.netty.study.client.InactiveListener;

public class ChannelExceptionHandler extends ChannelInboundHandlerAdapter {
    final private InactiveListener inactiveListener;
    private boolean exceptionOccurred = false;

    public ChannelExceptionHandler(InactiveListener inactiveListener) {
        this.inactiveListener = inactiveListener;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (exceptionOccurred) {
            // 채널에 문제가 발생하여 연결이 끊어진 경우에만 복구 동작 수행
            // 사용자의 명시적은 disconnect 에 의해 연결이 끊어진 경우 복구 동작 수행하지 않음
            exceptionOccurred = false;
            inactiveListener.channelInactiveOccurred();
        }
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        System.out.println(cause);

        exceptionOccurred = true;
        ctx.close();
        super.exceptionCaught(ctx, cause);
    }
}
