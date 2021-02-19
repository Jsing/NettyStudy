package netty.netty.study.client;

import lombok.SneakyThrows;
import netty.netty.study.configure.ServerAddress;
import netty.netty.study.server.TcpServer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("연결")
public class ConnectTest {

    private TcpServer server;

    @BeforeEach
    void contextLoad() {

        try {

            server = new TcpServer(ServerAddress.getPort());
            server.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    void contextEnd() {
        server.end();
    }

    @Test
    @DisplayName("연결 성공")
    @SneakyThrows
    void connectionSuccess() {

        ClientWorker clientWorker = new ClientWorker();
        clientWorker.init();

        boolean connected = clientWorker.connect(ServerAddress.getIp(), ServerAddress.getPort());

        Assertions.assertEquals(true, connected);

        clientWorker.disconnect();
    }


    @Test
    @DisplayName("두개의 Client 연결")
    @SneakyThrows
    void twoClientConnection() {

        ClientWorker clientWorker1 = new ClientWorker();
        ClientWorker clientWorker2 = new ClientWorker();

        clientWorker1.init();
        clientWorker2.init();

        boolean connected1 = clientWorker1.connect(ServerAddress.getIp(), ServerAddress.getPort());
        boolean connected2 = clientWorker2.connect(ServerAddress.getIp(), ServerAddress.getPort());

        Assertions.assertEquals(true, connected1);
        Assertions.assertEquals(true, connected2);

        clientWorker1.disconnect();
        clientWorker2.disconnect();
    }


    @Test
    @DisplayName("원격 서버 없음")
    @SneakyThrows
    void noServer() {

        final String noServerIp = "172.30.12.1";

        ClientWorker clientWorker = new ClientWorker();
        clientWorker.init();

        boolean connected = clientWorker.connect(noServerIp, ServerAddress.getPort());

        Assertions.assertEquals(false, connected);

        clientWorker.disconnect();
    }

    @Test
    @DisplayName("원격 서버 포트 없음")
    @SneakyThrows
    void noServerPort() {

        final int noServerPort = ServerAddress.getPort()-1;

        ClientWorker clientWorker = new ClientWorker();
        clientWorker.init();

        boolean connected = clientWorker.connect(ServerAddress.getIp(), noServerPort);

        Assertions.assertEquals(false, connected);

        clientWorker.disconnect();
    }


    @Test
    @DisplayName("연결 후 연결 끊기")
    @SneakyThrows
    void disconnectInActive() {

        // Ready
        ClientWorker clientWorker = new ClientWorker();
        clientWorker.init();

        boolean connected = clientWorker.connect(ServerAddress.getIp(), ServerAddress.getPort());

        connected = clientWorker.isActive();

        Assertions.assertEquals(true, connected);

        // Do
        server.end();

        // Then
        Thread.sleep(1000);

        connected = clientWorker.isActive();

        Assertions.assertEquals(false, connected);

        // End
        clientWorker.disconnect();
    }


}
