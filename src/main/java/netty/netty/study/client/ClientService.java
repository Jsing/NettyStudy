package netty.netty.study.client;

import lombok.extern.slf4j.Slf4j;
import netty.netty.study.client.initializer.ClientChannelInitializer;
import netty.netty.study.data.LastStatus;

import java.net.InetSocketAddress;

@Slf4j
public class ClientService {

    private final LastStatus lastStatus = new LastStatus();
    private final TcpClient tcpClient = new TcpClient();

    public void init() {

        tcpClient.init(new ClientChannelInitializer(lastStatus, (InactiveListener)tcpClient));
    }

    public void end() {
        tcpClient.destroy();
    }

    public LastStatus lastStatus() {

        return lastStatus;
    }

    public void connectUntilSuccess(String ip, int port, int msecGap) throws Exception {
        tcpClient.connectUntilSuccessAsync(ip, port, msecGap);
    }

    public boolean connectOnce(String ip, int port) throws Exception {

         return tcpClient.connect(ip, port);
    }

    public void disconnect() throws InterruptedException {

        tcpClient.disconnect();
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
