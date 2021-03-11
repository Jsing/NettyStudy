package netty.netty.study.client;

import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import netty.netty.study.client.initializer.ClientChannelInitializer;
import netty.netty.study.data.ConnectionTag;
import netty.netty.study.data.LastStatus;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ClientService {

    private final LastStatus lastStatus = new LastStatus();
    private final TcpClient tcpClient = new TcpClient();

    public void init() {

        tcpClient.init(new ClientChannelInitializer(lastStatus, (ChannelStatusListener)tcpClient));
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

    public boolean connect(ConnectionTag connectionTag) {
         return tcpClient.connect(connectionTag);
    }

    public void disconnect() {

        tcpClient.disconnect();
    }


    public void scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
        tcpClient.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    public void send(Object msg) {
        tcpClient.send(msg);
    }

    public boolean isActive() {
        return tcpClient.isActive();
    }

    public InetSocketAddress getLocalAddress() {

        return tcpClient.getLocalAddress();
    }
}
