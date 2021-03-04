package netty.netty.study.client;

import lombok.SneakyThrows;
import netty.netty.study.configure.ServerAddress;
import netty.netty.study.server.ServerService;
import netty.netty.study.server.TcpServer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

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
        boolean connected = client.connect(ServerAddress.info());
        Assertions.assertEquals(true, connected);
    }

    @Test
    @DisplayName("원격 서버 없음")
    @SneakyThrows
    void noServer() {
        String noServerIp = "172.30.12.1";
        boolean connected = client.connect(ServerAddress.info());
        Assertions.assertEquals(false, connected);
    }

    @Test
    @DisplayName("원격 서버 포트 없음")
    @SneakyThrows
    void noServerPort() {
        final int noServerPort = ServerAddress.info().getPort()-1;

        boolean connected = client.connect(ServerAddress.info());
        Assertions.assertEquals(false, connected);
    }

    @Test
    @DisplayName("Client->Server")
    @SneakyThrows
    void clientToServerTransfer() {
        String testMessage = "I am Jsing";

        boolean connected = client.connect(ServerAddress.info());
        Assertions.assertEquals(true, connected);

        Thread.sleep(100);

        client.send(testMessage);

        Thread.sleep(100);

        ServerService serverService = server.getServerService(client.getLocalAddress().toString());
        String msgReceived = serverService.lastStatus().get();

        Assertions.assertEquals(testMessage, msgReceived);
    }

    @DisplayName("Server->Client")
    @SneakyThrows
    void serverToClientTransfer() {
        boolean connected = client.connect(ServerAddress.info());
        Assertions.assertEquals(true, connected);
    }

    //@Test
    // TODO : Today 테스트 필요
    @DisplayName("1개 Bootstrap,EventLoop / N개의 Channel")
    @SneakyThrows
    void twoClientConnection() {

    }



//
//
//    @Test
//    @DisplayName("연결 후 연결 끊기")
//    @SneakyThrows
//    void disconnectInActive() {
//
//        // Ready
//        ClientWorker clientWorker = new ClientWorker();
//        clientWorker.init();
//
//        boolean connected = clientWorker.connect(ServerAddress.getIp(), ServerAddress.getPort());
//
//        connected = clientWorker.isActive();
//
//        Assertions.assertEquals(true, connected);
//
//        // Do
//        server.end();
//
//        // Then
//        Thread.sleep(1000);
//
//        connected = clientWorker.isActive();
//
//        Assertions.assertEquals(false, connected);
//
//        // End
//        clientWorker.disconnect();
//    }


}
