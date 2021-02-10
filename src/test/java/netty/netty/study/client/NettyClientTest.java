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

        client = new NettyClient("127.0.0.1", serverPort);
        client.init();

        try {
            Assertions.assertEquals(true, client.connect());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (client != null) {
            client.disconnect();
        }
    }

    @Test
    void 연결_실패_서버포트이상() {

        client = new NettyClient("127.0.0.1", serverPort - 1);
        client.init();

        try {
            Assertions.assertEquals(false, client.connect());
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.disconnect();
    }

}
