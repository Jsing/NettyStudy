package netty.netty.study.client;

import lombok.SneakyThrows;
import netty.netty.study.configure.ServerAddress;
import netty.netty.study.data.ConnectionTag;
import netty.netty.study.server.ServerService;
import netty.netty.study.server.TcpServer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@DisplayName("연결")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ConnectTest {
    private TcpServer server;
    private ClientService client;

    @BeforeEach
    @SneakyThrows
    void contextUp() {
        server = new TcpServer(ServerAddress.info().getPort());
        server.start();
        client = new ClientService();
        client.init();
    }

    @AfterEach
    @SneakyThrows
    void contextDown() {
        client.disconnect();
        server.shutdown();
    }

    @Test
    @DisplayName("연결 성공")
    @SneakyThrows
    void connectionSuccess() throws Exception {
        client.connectUntilSuccess(ServerAddress.info());

        Thread.sleep(1000);
        Assertions.assertEquals(true, client.isActive());
    }

    @Test
    @DisplayName("원격 서버 없음")
    @SneakyThrows
    void noServer() {
        final ConnectionTag connectionTag = new ConnectionTag(0, "172.30.12.1",
                ServerAddress.info().getPort() - 1);
        client.connectOnce(connectionTag);
        Assertions.assertEquals(false, client.isActive());
    }

    @Test
    @DisplayName("원격 서버 포트 없음")
    @SneakyThrows
    void noServerPort() {
        final ConnectionTag connectionTag = new ConnectionTag(0, ServerAddress.info().getIp(),
                ServerAddress.info().getPort() - 1);
        client.connectOnce(connectionTag);
        Assertions.assertEquals(false, client.isActive());
    }

    @Test
    @DisplayName("Client->Server")
    @SneakyThrows
    void clientToServerTransfer() {
        String testMessage = "I am Jsing";

        client.connectUntilSuccess(ServerAddress.info());
        Assertions.assertEquals(true, client.isActive());

        Thread.sleep(100);

        client.send(testMessage);

        Thread.sleep(100);

        ServerService serverService = server.getServerService(client.getLocalAddress().toString());
        String msgReceived = serverService.lastStatus().get();

        Assertions.assertEquals(testMessage, msgReceived);
    }

    @Test
    @DisplayName("Study Netty Thread Model")
    @SneakyThrows
    void studyNettyThreadModel() {
        System.out.println("connectUntilSuccess() --------------------------- ");
        client.connectUntilSuccess(ServerAddress.info());
        Thread.sleep(1000);

        System.out.println("scheduleAtFixedRate(1) --------------------------- ");
        client.beginUserTask(() -> {
            try {
                client.send("1");
                System.out.println("1 =" + Thread.currentThread().toString());
            } catch (CancellationException e) {
                e.printStackTrace();
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        Thread.sleep(1000);

        System.out.println("disconnect() --------------------------- ");
        client.disconnect();
        Thread.sleep(1000);

        System.out.println("connectUntilSuccess() --------------------------- ");
        client.connectUntilSuccess(ServerAddress.info());
        Thread.sleep(1000);

        System.out.println("scheduleAtFixedRate(2) --------------------------- ");
        client.beginUserTask(() -> {
            client.send("2");
            System.out.println("2 =" + Thread.currentThread().toString());
        }, 0, 1000, TimeUnit.MILLISECONDS);
        Thread.sleep(1000);

        System.out.println("disconnect() --------------------------- ");
        client.disconnect();
        Thread.sleep(1000);
    }

    @DisplayName("Server->Client")
    @SneakyThrows
    void serverToClientTransfer() {
        client.connectUntilSuccess(ServerAddress.info());
        Assertions.assertEquals(true, client.isActive());
    }


}
