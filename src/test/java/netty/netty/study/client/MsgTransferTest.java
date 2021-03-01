package netty.netty.study.client;

import netty.netty.study.configure.ServerAddress;
import netty.netty.study.server.TcpServer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("메시지 송수신")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MsgTransferTest {

    private TcpServer server;
    private ClientService clientServcie;

    @BeforeAll
    @Test
    void contextLoad() throws Exception {
        // 서버 생성
        server = new TcpServer(ServerAddress.getPort());
        server.start();

        // 클라이언트 생성
        clientServcie = new ClientService();
        clientServcie.init();

        // 연결
        boolean isConnected = clientServcie.connectOnce(ServerAddress.getIp(), ServerAddress.getPort());
        Assertions.assertEquals( true, isConnected );

    }

    @AfterAll
    void contextEnd() {

        // 클라이언트 연결 끊기
        clientServcie.end();

        // 서버 종료
        server.shutdown();

    }

    @Test
    @DisplayName("Send a message")
    void sendMessage() {

        boolean isExpected = true;


        Assertions.assertEquals( true, isExpected );

    }

    @Test
    @DisplayName("Read a message")
    void readMessage() {

        boolean isExpected = true;


        Assertions.assertEquals(true, isExpected);
    }

    @Test
    @DisplayName("Periodic sending messages")
    void periodicSendMessages() {

        boolean isExpected = true;


        Assertions.assertEquals( true, isExpected );

    }
}