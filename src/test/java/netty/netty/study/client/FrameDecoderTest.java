package netty.netty.study.client;

import netty.netty.study.server.ClientService;
import netty.netty.study.server.NettyServer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;


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
        SampleCam client = new SampleCam();

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
