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

        server.shutdown();
    }

    @Test
    @DisplayName("연결 성공할 때 까지 시도")
    @SneakyThrows
    void clientConnectUntilSuccess() {
        TcpServer server = new TcpServer(ServerAddress.getPort());
        ClientService client = new ClientService();

        client.init();

        client.connectUntilSuccess(ServerAddress.getIp(), ServerAddress.getPort(), 1000);

        Thread.sleep(10000);

        Assertions.assertFalse(client.isActive());

        server.start();

        Thread.sleep(1000);

        Assertions.assertTrue(client.isActive());

        server.shutdown();
        client.disconnect();
    }

    @Test
    @DisplayName("연결 복구")
    @SneakyThrows
    void connectionRecovery() {
        TcpServer server = new TcpServer(ServerAddress.getPort());
        ClientService client = new ClientService();

        System.out.println("[Server] start");
        server.start();

        System.out.println("[Client] connect");
        client.init();
        client.connectOnce(ServerAddress.getIp(), ServerAddress.getPort());

        System.out.println("[Client] sleep during 1sec");
        Thread.sleep(1000);

        System.out.println("[Client] client.isActive() = " + client.isActive());
        Assertions.assertTrue(client.isActive());

        System.out.println("[Client] data transfer");
        transfer(server, client);
        Thread.sleep(1000);

        System.out.println("[Server] shutdown");
        server.shutdown();

        System.out.println("[Client] sleep during 1sec");
        Thread.sleep(1000);

        System.out.println("[Client] client.isActive() = " + client.isActive());
        Assertions.assertFalse(client.isActive());

        System.out.println("[Server] restart");
        server.start();

        System.out.println("[Client] sleep during 5sec");
        Thread.sleep(5000);

        System.out.println("[Client] client.isActive() = " + client.isActive());
        Assertions.assertTrue(client.isActive());

        System.out.println("[Client] data transfer");
        transfer(server, client);
        Thread.sleep(1000);

        System.out.println("[Server] shutdown");
        server.shutdown();

        System.out.println("[Client] disconnect");
        client.disconnect();
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
