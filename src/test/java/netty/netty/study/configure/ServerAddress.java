package netty.netty.study.configure;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class ServerAddress {

    @Getter
    private static final String ip = "127.0.0.1";

    @Getter
    @Setter
    private static final int port = 12345;
}
