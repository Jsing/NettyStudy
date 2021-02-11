package netty.netty.study.client;

import netty.netty.study.server.NettyServer;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@DisplayName("연결")
public class ConnectTest {

    final static Logger logger = Logger.getLogger(ConnectTest.class);
    final private int serverPort = 12345;
    final private String serverIp = "127.0.0.1";
    private NettyServer server;
    private NettyClient client;

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

        client = new NettyClient();
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
    @DisplayName("연결 실패-엉뚱한 포트")
    void connectionFail() {

        client = new NettyClient();
        client.init();

        try {
            Assertions.assertEquals(false, client.connect(serverIp, serverPort-1));
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.disconnect();
    }

    @Test
    @DisplayName("두개의 Client 연결")
    void twoClientConnection() {

        NettyClient client1 = new NettyClient();
        client1.init();

        NettyClient client2 = new NettyClient();
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


}
