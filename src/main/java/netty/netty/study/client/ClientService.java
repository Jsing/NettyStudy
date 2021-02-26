package netty.netty.study.client;

import lombok.extern.slf4j.Slf4j;
import netty.netty.study.client.initializer.ClientChannelInitializer;
import netty.netty.study.data.LastStatus;
import netty.netty.study.data.Updatable;

import java.net.InetSocketAddress;

@Slf4j
public class ClientService {

    private final LastStatus lastStatus = new LastStatus();
    private final TcpClient tcpClient = new TcpClient();

    public void init() {

        tcpClient.init(new ClientChannelInitializer(lastStatus));
    }

    public void end() {
        tcpClient.destroy();
    }

    public LastStatus lastStatus() {

        return lastStatus;
    }

    public boolean connect(String ip, int port) throws Exception {

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
