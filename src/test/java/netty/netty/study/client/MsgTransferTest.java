package netty.netty.study.client;

import netty.netty.study.server.NettyServer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("메시지 송수신")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MsgTransferTest {

    private NettyServer server;
    private SampleCam cam;

    @BeforeAll
    @Test
    void contextLoad() throws Exception {
        // 서버 생성
        server = new NettyServer(ServerAddress.getPort());
        server.start();

        // 클라이언트 생성
        cam = new SampleCam();
        cam.init();

        // 연결
        boolean isConnected = cam.connect(ServerAddress.getIp(), ServerAddress.getPort());
        Assertions.assertEquals( true, isConnected );

    }

    @AfterAll
    void contextEnd() {

        // 클라이언트 연결 끊기
        cam.disconnect();

        // 서버 종료
        server.end();

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