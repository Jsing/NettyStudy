package netty.netty.study.client;

import lombok.SneakyThrows;
import netty.netty.study.configure.ServerAddress;
import netty.netty.study.server.TcpServer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("연결")
public class ConnectTest {
    private TcpServer server;
    private ClientWorker client;

    @BeforeEach
    @SneakyThrows
    void contextUp() {
        server = new TcpServer(ServerAddress.getPort());
        server.start();
        client = new ClientWorker();
        client.init();
    }

    @AfterEach
    @SneakyThrows
    void contextDown() {
        client.disconnect();
        server.end();
    }

    @Test
    @DisplayName("연결 성공")
    @SneakyThrows
    void connectionSuccess() throws Exception {
        boolean connected = client.connect(ServerAddress.getIp(), ServerAddress.getPort());
        Assertions.assertEquals(true, connected);
    }

    @Test
    @DisplayName("원격 서버 없음")
    @SneakyThrows
    void noServer() {
        String noServerIp = "172.30.12.1";
        boolean connected = client.connect(noServerIp, ServerAddress.getPort());
        Assertions.assertEquals(false, connected);
    }

    @Test
    @DisplayName("원격 서버 포트 없음")
    @SneakyThrows
    void noServerPort() {
        final int noServerPort = ServerAddress.getPort()-1;

        boolean connected = client.connect(ServerAddress.getIp(), noServerPort);
        Assertions.assertEquals(false, connected);
    }

    // TODO : Today 테스트 필요
    @DisplayName("Reconnect->Transfer")
    @SneakyThrows
    void reconnectAndTransfer() {
        // TODO : @Sharable 어노테이션 영향 확인
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
