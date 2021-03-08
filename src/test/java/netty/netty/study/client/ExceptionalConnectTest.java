package netty.netty.study.client;

import lombok.SneakyThrows;
import netty.netty.study.configure.ServerAddress;
import netty.netty.study.server.ServerService;
import netty.netty.study.server.TcpServer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class
ExceptionalConnectTest {

    @Test
    @DisplayName("N Reconnect->Transfer")
    @SneakyThrows
    void reconnectAndTransfer() {
        final int nRepeat = 100;
        final String testBaseMessage = "Hello I am Jsing";

        TcpServer server = new TcpServer(ServerAddress.info().getPort());
        server.start();
        ClientService client = new ClientService();
        client.init();

        for (int i = 0; i < nRepeat; i++) {
            boolean connected = client.connect(ServerAddress.info());
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
        TcpServer server = new TcpServer(ServerAddress.info().getPort());
        ClientService client = new ClientService();

        client.init();

        client.connectUntilSuccess(ServerAddress.info());

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
        TcpServer server = new TcpServer(ServerAddress.info().getPort());
        ClientService client = new ClientService();

        System.out.println("[Server] start");
        server.start();

        System.out.println("[Client] connect");
        client.init();
        client.connect(ServerAddress.info());

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

    @Test
    @DisplayName("반복 연결 취소")
    @SneakyThrows
    void cancelThatConnectUntilSuccess() {
        TcpServer server = new TcpServer(ServerAddress.info().getPort());
        ClientService client = new ClientService();

        client.init();

        System.out.println("[Client] start connectUntilSuccess()");
        client.connectUntilSuccess(ServerAddress.info());

        Thread.sleep(1000);

        System.out.println("[Client] cancel connectUntilSuccess()");
        client.disconnect();

        Thread.sleep(1200);

        System.out.println("[Server] start()");
        server.start();

        Thread.sleep(3000);

        System.out.println("[Client] Assertions.assertFalse(client.isActive()) = " + client.isActive());
        Assertions.assertFalse(client.isActive());

        System.out.println("[Client] start connectUntilSuccess()");
        client.connectUntilSuccess(ServerAddress.info());

        Thread.sleep(1000);

        System.out.println("[Client] Assertions.assertTrue(client.isActive()) = " + client.isActive());
        Assertions.assertTrue(client.isActive());

        System.out.println("[Server] shutdown");
        server.shutdown();

        System.out.println("[Client] disconnect");
        client.disconnect();
    }

    @Test
    @DisplayName("자동 복구 코드 동작 중 사용자 연결 시도")
    @SneakyThrows
    void userConnectInRecovery() {
        boolean connected = false;

        TcpServer server = new TcpServer(ServerAddress.info().getPort());
        ClientService client = new ClientService();

        client.init();

        System.out.println("[Client] connect");
        connected = client.connect(ServerAddress.info());

        System.out.println("[Client] sleep(1000)");
        Thread.sleep(1000);

        System.out.println("[Client] connectUntilSuccess()");
        client.connectUntilSuccess(ServerAddress.info());

        System.out.println("[Client] sleep(1000)");
        Thread.sleep(1000);

        System.out.println("[Server] server starts");
        server.start();

        System.out.println("[Client] sleep(1000)");
        Thread.sleep(1000);

        System.out.println("[Client] isActive() = " + client.isActive());
        Assertions.assertTrue(client.isActive());

        transfer(server, client);


        Thread.sleep(10000);

        server.shutdown();
        client.disconnect();
    }

    @Test
    @DisplayName("Not Channel Recovery When Explicit Disconnect ")
    @SneakyThrows
    void notRecoveryWhenExplicitDisconnect() {
        boolean connected = false;

        TcpServer server = new TcpServer(ServerAddress.info().getPort());
        ClientService client = new ClientService();

        server.start();
        client.init();

        System.out.println("[Client] connect");
        connected = client.connect(ServerAddress.info());

        System.out.println("[Client] sleep(1000)");
        Thread.sleep(1000);

        System.out.println("[Client] isActive() = " + client.isActive());
        Assertions.assertTrue(client.isActive());

        System.out.println("[Client] disconnect explicitly");
        client.disconnect();

        Thread.sleep(5000);

        System.out.println("[Client] isActive() = " + client.isActive());
        Assertions.assertFalse(client.isActive());

        server.shutdown();
        client.disconnect();
    }

    @SneakyThrows
    void transfer(TcpServer server, ClientService client) {
        final String testBaseMessage = "Hello I am Jsing";

        String testMessage = testBaseMessage;

        Thread.sleep(30);

        System.out.println("[Client] send() = " + testMessage);
        client.send(testMessage);

        Thread.sleep(30);

        ServerService serverService = server.getServerService(client.getLocalAddress().toString());
        String msgReceived = serverService.lastStatus().get();
        System.out.println("[Server] receive() = " + msgReceived);

        System.out.println("[Server] assertEquals() = " + testMessage + " : " + msgReceived);
        Assertions.assertEquals(testMessage, msgReceived);
    }


}
