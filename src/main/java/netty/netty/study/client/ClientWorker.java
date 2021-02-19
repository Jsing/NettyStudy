package netty.netty.study.client;

import lombok.extern.slf4j.Slf4j;
import netty.netty.study.client.initializer.ClientChannelInitializer;
import netty.netty.study.data.LastStatus;

import java.net.InetSocketAddress;

@Slf4j
public class ClientWorker {

    private final LastStatus<String> lastStatus = new LastStatus<>();
    private final TcpClient tcpClient = new TcpClient();

    public void init() {

        tcpClient.init(new ClientChannelInitializer(lastStatus));
    }

    public void end() {

        try {
            tcpClient.end();
        } catch (InterruptedException e) {
            log.debug(e.toString());
        }
    }

    public LastStatus<String> lastStatus() {

        return lastStatus;
    }

    public boolean connect(String ip, int port) throws Exception {

         return tcpClient.connect(ip, port);
    }

    public void disconnect() throws InterruptedException {

        tcpClient.disconnect();
    }

    public boolean isActive() {
        return tcpClient.isActive();
    }

    public InetSocketAddress getLocalAddress() {

        return tcpClient.getLocalAddress();
    }
}
