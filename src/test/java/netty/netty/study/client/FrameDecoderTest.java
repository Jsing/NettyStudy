package netty.netty.study.client;

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

        boolean connectionResult = client.connect(serverIp, serverPort);

        Assertions.assertEquals(true, connectionResult);

        System.out.println("client address s= " + client.getLocalAddress().toString());

        ClientService service = server.getClientService(client.getLocalAddress().toString());

        service.writeMessage("han joo seung".getBytes(StandardCharsets.UTF_8));

        Thread.sleep(3000);
    }

}
