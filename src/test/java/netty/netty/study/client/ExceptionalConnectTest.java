package netty.netty.study.client;

import lombok.SneakyThrows;
import netty.netty.study.configure.ServerAddress;
import netty.netty.study.server.ServerService;
import netty.netty.study.server.TcpServer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ExceptionalConnectTest {

    @Test
    @DisplayName("N Reconnect->Transfer")
    @SneakyThrows
    void reconnectAndTransfer() {
        final int nRepeat = 100;
        final String testBaseMessage = "Hello I am Jsing";

        TcpServer server = new TcpServer(ServerAddress.getPort());
        server.start();
        ClientService client = new ClientService();
        client.init();

        for (int i = 0; i < nRepeat; i++) {
            boolean connected = client.connectOnce(ServerAddress.getIp(), ServerAddress.getPort());
            Assertions.assertEquals(true, connected);

            String testMessage = testBaseMessage + String.valueOf(i);

            Thread.sleep(30);

            client.send(testMessage);

            Thread.sleep(30);

            ServerService serverService = server.getServerService(client.getLocalAddress().toString());
            String msgReceived = serverService.lastStatus().get();

            Assertions.assertEquals(testMessage, msgReceived);

            client.disconnect();
        }

        server.end();
    }

    @Test
    @DisplayName("연결 성공할 때 까지 시도")
    @SneakyThrows
    void clientConnectUntilSuccess() {
        TcpServer server = new TcpServer(ServerAddress.getPort());
        ClientService client = new ClientService();

        client.init();

        client.startConnectUntilSuccess(ServerAddress.getIp(), ServerAddress.getPort());

        Thread.sleep(10000);

        Assertions.assertFalse(client.isActive());

        server.start();

        Thread.sleep(1000);

        Assertions.assertTrue(client.isActive());

        server.end();
        client.disconnect();
    }

    @Test
    @DisplayName("연결 복구")
    @SneakyThrows
    void connectionRecovery() {
        TcpServer server = new TcpServer(ServerAddress.getPort());
        server.start();

        ClientService client = new ClientService();
        client.init();
        client.connectOnce(ServerAddress.getIp(), ServerAddress.getPort());

        Thread.sleep(1000);
        Assertions.assertTrue(client.isActive());

        transfer(server, client);
        Thread.sleep(1000);

        server.end();
        Thread.sleep(1000);
        Assertions.assertFalse(client.isActive());

        server.start();
        Thread.sleep(5000);
        Assertions.assertTrue(client.isActive());

        transfer(server, client);
        Thread.sleep(1000);

//        server.end();
        client.disconnect();
        server.end();
    }

    @SneakyThrows
    void transfer(TcpServer server, ClientService client) {
        final String testBaseMessage = "Hello I am Jsing";

        String testMessage = testBaseMessage;

        Thread.sleep(30);

        client.send(testMessage);

        Thread.sleep(30);

        ServerService serverService = server.getServerService(client.getLocalAddress().toString());
        String msgReceived = serverService.lastStatus().get();

        Assertions.assertEquals(testMessage, msgReceived);
    }


}
