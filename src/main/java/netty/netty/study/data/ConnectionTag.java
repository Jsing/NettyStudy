package netty.netty.study.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ConnectionTag {
    private final String ip;
    private final Integer port;

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
