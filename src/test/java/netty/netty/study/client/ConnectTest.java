package netty.netty.study.client;

import lombok.SneakyThrows;
import netty.netty.study.configure.ServerAddress;
import netty.netty.study.data.ConnectionTag;
import netty.netty.study.server.ServerService;
import netty.netty.study.server.TcpServer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("연결")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ConnectTest {
    private TcpServer server;
    private ClientService client;

    @BeforeEach
    @SneakyThrows
    void contextUp() {
        server = new TcpServer(ServerAddress.info().getPort());
        server.start();
        client = new ClientService();
        client.init();
    }

    @AfterEach
    @SneakyThrows
    void contextDown() {
        client.disconnect();
        server.shutdown();
    }

    @Test
    @DisplayName("연결 성공")
    @SneakyThrows
    void connectionSuccess() throws Exception {
        boolean connected = client.connect(ServerAddress.info());
        Assertions.assertEquals(true, connected);
    }

    @Test
    @DisplayName("원격 서버 없음")
    @SneakyThrows
    void noServer() {
        final ConnectionTag connectionTag =  new ConnectionTag("172.30.12.1",
                ServerAddress.info().getPort()-1);

        boolean connected = client.connect(connectionTag);
        Assertions.assertEquals(false, connected);
    }

    @Test
    @DisplayName("원격 서버 포트 없음")
    @SneakyThrows
    void noServerPort() {
        final ConnectionTag connectionTag =  new ConnectionTag(ServerAddress.info().getIp(),
                        ServerAddress.info().getPort()-1);

        boolean connected = client.connect(connectionTag);
        Assertions.assertEquals(false, connected);
    }

    @Test
    @DisplayName("Client->Server")
    @SneakyThrows
    void clientToServerTransfer() {
        String testMessage = "I am Jsing";

        boolean connected = client.connect(ServerAddress.info());
        Assertions.assertEquals(true, connected);

        Thread.sleep(100);

        client.send(testMessage);

        Thread.sleep(100);

        ServerService serverService = server.getServerService(client.getLocalAddress().toString());
        String msgReceived = serverService.lastStatus().get();

        Assertions.assertEquals(testMessage, msgReceived);
    }

    @Test
    @DisplayName("notAllowedCaller-postConstruct()")
    @SneakyThrows
    void postConstruct() {
        boolean connected = client.connect(ServerAddress.info());
    }

    @Test
    @DisplayName("notAllowedCaller-connect()")
    @SneakyThrows
    void connect() {
        boolean connected = client.connect(ServerAddress.info());
    }

    @DisplayName("Server->Client")
    @SneakyThrows
    void serverToClientTransfer() {
        boolean connected = client.connect(ServerAddress.info());
        Assertions.assertEquals(true, connected);
    }


}
