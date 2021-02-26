package netty.netty.study.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import netty.netty.study.server.handler.ServerServiceHandler;

public class ServerServiceChannelInitializer extends ChannelInitializer<SocketChannel> {
    final private ClientActiveListener clientActiveListener;

    public ServerServiceChannelInitializer(ClientActiveListener clientActiveListener) {
        this.clientActiveListener = clientActiveListener;
    }

    /**
     * 서버에 접속하는 클라리언트 서비스 채널을 초기화한다.
     * @param socketChannel
     * @throws Exception
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        // 수신
        socketChannel.pipeline().addLast(new StringDecoder());
        socketChannel.pipeline().addLast(new ServerServiceHandler(clientActiveListener));

        // 송신
        socketChannel.pipeline().addLast(new StringEncoder());
    }

}
