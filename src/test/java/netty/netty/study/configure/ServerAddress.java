package netty.netty.study.configure;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import netty.netty.study.data.ConnectionTag;

@AllArgsConstructor
public class ServerAddress {
    private final static ConnectionTag info = new ConnectionTag(0,"127.0.0.1", 12345);

    public static ConnectionTag info() {
        return info;
    }
}
