package netty.netty.study.client;

import io.netty.buffer.Unpooled;
import netty.netty.study.server.ClientService;
import netty.netty.study.server.NettyServer;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;


@SpringBootTest
@DisplayName("Frame Decoder")
public class FrameDecoderTest {
    final private int serverPort = 12345;
    final private String serverIp = "127.0.0.1";
    private NettyServer server;

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
    @DisplayName("Message Boundary")
    void messageBoundary() throws InterruptedException {
        NettyClient client = new NettyClient();

        client.init();

        boolean connected = client.connect(serverIp, serverPort);

        Assertions.assertEquals(true, connected);

        server.waitForClient(client.getLocalAddress());

        System.out.println("[Client] my address = " + client.getLocalAddress().toString());

        ClientService service = server.getClientService(client.getLocalAddress().toString());

        service.writeMessage("1.Incomplete half message");

        Thread.sleep(5000);

        service.writeMessage(" 2.complete half message\n");

        Thread.sleep(3000);

        Assertions.assertEquals( "1.Incomplete half message 2.complete half message", client.lastStatus().copy() );

    }

    @Test
    @DisplayName("transferContent")
    void transferContent() {

    }

}
