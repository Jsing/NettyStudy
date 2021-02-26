package netty.netty.study.client;

import lombok.SneakyThrows;
import netty.netty.study.configure.ServerAddress;
import netty.netty.study.data.Updatable;
import netty.netty.study.server.ServerService;
import netty.netty.study.server.TcpServer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RepeatConnectTest {
    private TcpServer server;
    private ClientService client;

    @BeforeEach
    @SneakyThrows
    void contextUp() {

    }

    @AfterEach
    @SneakyThrows
    void contextDown() {

    }

    @Test
    @DisplayName("N Reconnect->Transfer")
    @SneakyThrows
    void reconnectAndTransfer() {
        final int nRepeat = 100000;
        final String testBaseMessage = "Hello I am Jsing";

        server = new TcpServer(ServerAddress.getPort());
        server.start();
        client = new ClientService();
        client.init();

        for(int i=0; i<nRepeat; i++) {
            boolean connected = client.connect(ServerAddress.getIp(), ServerAddress.getPort());
            Assertions.assertEquals(true, connected);

            String testMessage = testBaseMessage + String.valueOf(i);

            Thread.sleep(30);

            client.send(testMessage);

            Thread.sleep(30);

            ServerService serverService = server.getServerService(client.getLocalAddress().toString());
            String msgReceived = serverService.lastStatus().get();

            Assertions.assertEquals(testMessage, msgReceived);

            client.disconnect();
        }

        server.end();
    }

}
