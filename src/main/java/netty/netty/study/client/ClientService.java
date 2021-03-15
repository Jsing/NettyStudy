package netty.netty.study.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import netty.netty.study.client.handler.ByteToMessageDecoderTest;
import netty.netty.study.client.handler.HelloStarterHandler;
import netty.netty.study.client.handler.LastStatusUpdateHandler;
import netty.netty.study.data.ConnectionTag;
import netty.netty.study.data.LastStatus;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ClientService {

    private final LastStatus lastStatus = new LastStatus();
    private final TcpClient tcpClient = new TcpClient();

    public void init() {

        tcpClient.init(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(
                        // 수신
                        new ByteToMessageDecoderTest(),
                        new StringDecoder(CharsetUtil.UTF_8),
                        new HelloStarterHandler(),
                        new LastStatusUpdateHandler(lastStatus),

                        // 전송
                        new StringEncoder(CharsetUtil.UTF_8));
            }
        });
    }

    public void end() {
        tcpClient.destroy();
    }

    public LastStatus lastStatus() {

        return lastStatus;
    }

    public void connectUntilSuccess(ConnectionTag connectionTag) {
        tcpClient.connectUntilSuccess(connectionTag);
    }

    public Future beginConnectUntilSuccess(ConnectionTag connectionTag) {
        return tcpClient.beginConnectUntilSuccess(connectionTag);
    }

    public boolean connectOnce(ConnectionTag connectionTag) {
         return tcpClient.connect(connectionTag);
    }

    public void disconnect() {
        tcpClient.disconnect();
    }

    public boolean beginUserTask(Runnable task, long initialDelay, long period, TimeUnit unit) {
        return tcpClient.beginUserTask(task, initialDelay, period, unit);
    }

    public boolean send(Object msg) {
        return tcpClient.send(msg, true);
    }

    public boolean isActive() {
        return tcpClient.isActive();
    }

    public InetSocketAddress getLocalAddress() {

        return tcpClient.getLocalAddress();
    }
}
