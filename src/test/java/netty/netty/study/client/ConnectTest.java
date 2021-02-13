package netty.netty.study.client;

import netty.netty.study.server.NettyServer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@DisplayName("연결")
public class ConnectTest {
    final private int serverPort = 12345;
    final private String serverIp = "127.0.0.1";
    private NettyServer server;
    private SampleCam client;

    @BeforeEach
    void contextLoad() {

        try {
            server = new NettyServer(serverPort);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void contextEnd() {
        server.end();
    }

    @Test
    @DisplayName("연결 성공")
    void connectionSuccess() {

        client = new SampleCam();
        client.init();

        try {
            Assertions.assertEquals(true, client.connect(serverIp, serverPort));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (client != null) {
            client.disconnect();
        }
    }


    @Test
    @DisplayName("두개의 Client 연결")
    void twoClientConnection() {

        SampleCam client1 = new SampleCam();
        client1.init();

        SampleCam client2 = new SampleCam();
        client2.init();

        try {
            Assertions.assertEquals(true, client1.connect(serverIp, serverPort));
            Assertions.assertEquals(true, client2.connect(serverIp, serverPort));
        } catch (Exception e) {
            e.printStackTrace();
        }

        client1.disconnect();
        client2.disconnect();

    }


    @Test
    @DisplayName("원격 서버 없음")
    void noServer() {
        client = new SampleCam();
        client.init();

        try {
            Assertions.assertEquals(false, client.connect("192.168.21.12", serverPort));
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.disconnect();
    }

    @Test
    @DisplayName("원격 서버 포트 없음")
    void noServerPort() {
        client = new SampleCam();
        client.init();

        try {
            Assertions.assertEquals(false, client.connect(serverIp, serverPort-1));
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.disconnect();
    }


    @Test
    @DisplayName("연결 실패-엉뚱한 포트")
    void reconnect() {

        client = new SampleCam();
        client.init();

        try {
            boolean connected = client.connect(serverIp, serverPort-1);

            Assertions.assertEquals(false, connected);

            connected = client.connect(serverIp, serverPort);

            Assertions.assertEquals(true, connected);

        } catch (Exception e) {
            e.printStackTrace();
        }

        client.disconnect();
    }


}
