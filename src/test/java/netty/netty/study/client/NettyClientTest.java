package netty.netty.study.client;

import netty.netty.study.server.NettyServer;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@DisplayName("Netty Client Test")
public class NettyClientTest {

    final static Logger logger = Logger.getLogger(NettyClientTest.class);
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
    void 연결_성공() {

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
    void 연결_실패_서버포트이상() {

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
    void 두개_연결_성공() {

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
