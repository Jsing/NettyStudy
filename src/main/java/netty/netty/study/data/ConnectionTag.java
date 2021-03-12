package netty.netty.study.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ConnectionTag {
    private final Integer equipmentId;
    private final String ip;
    private final Integer port;

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getEquipmentId() {
        return equipmentId;
    }
}
