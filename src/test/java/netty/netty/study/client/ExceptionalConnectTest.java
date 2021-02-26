package netty.netty.study.client;

import lombok.SneakyThrows;
import netty.netty.study.configure.ServerAddress;
import netty.netty.study.server.ServerService;
import netty.netty.study.server.TcpServer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ExceptionalConnectTest {
    private TcpServer server;
    private ClientService client;

    @BeforeEach
    @SneakyThrows
    void contextUp() {

    }

    @AfterEach
    @SneakyThrows
    void contextDown() {

    }

    @Test
    @DisplayName("N Reconnect->Transfer")
    @SneakyThrows
    void reconnectAndTransfer() {
        final int nRepeat = 100000;
        final String testBaseMessage = "Hello I am Jsing";

        server = new TcpServer(ServerAddress.getPort());
        server.start();
        client = new ClientService();
        client.init();

        for (int i = 0; i < nRepeat; i++) {
            boolean connected = client.connect(ServerAddress.getIp(), ServerAddress.getPort());
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
    @DisplayName("클라이언트 연결 시도 중 서버 시작")
    @SneakyThrows
    void serverStartAfterConnect() {
        client = new ClientService();
        client.init();

        client.connectUntilSuccess(ServerAddress.getIp(), ServerAddress.getPort());

        Thread.sleep(10000);

        Assertions.assertEquals(false, client.isActive());

        server = new TcpServer(ServerAddress.getPort());
        server.start();

        Thread.sleep(500);

        ServerService serverService = server.getServerService(client.getLocalAddress().toString());

        Assertions.assertEquals(true, serverService!=null);
        Assertions.assertEquals(true, client.isActive());

        serverService.send("Hello Netty");

        Thread.sleep(1000);

        Assertions.assertEquals("Hello Netty", client.lastStatus().get());

        serverService.disconnect();

        server.end();

        Thread.sleep(1000);

        Assertions.assertEquals(false, client.isActive());

        server.start();

        Thread.sleep(2000);

        Assertions.assertEquals(true, client.isActive());


    }


}
