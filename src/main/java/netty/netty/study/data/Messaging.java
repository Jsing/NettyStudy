package netty.netty.study.data;

import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;

import java.util.Map;

@UtilityClass
public class Messaging {

    public void info(int equipmentId, String code, @Nullable Map<String, Object> params) {
        System.out.println("Client Id = " + equipmentId + ", code = " + code + ", params = " + params);
    }

    public void info(int equipmentId, String code) {
        System.out.println("camId = " + equipmentId + ", code = " + code);
    }

    public void warning(int equipmentId, String code, @Nullable Map<String, Object> params) {
        System.out.println("Client Id = " + equipmentId + ", code = " + code + ", params = " + params);
    }

    public void warning(int equipmentId, String code) {
        System.out.println("camId = " + equipmentId + ", code = " + code);
    }

    public void error(int equipmentId, String code, @Nullable Map<String, Object> params) {
        System.out.println("Client Id = " + equipmentId + ", code = " + code + ", params = " + params);
    }

    public void error(int camId, String code) {
        System.out.println("camId = " + camId + ", code = " + code);
    }

    public void connected(int equipmentId) {
        System.out.println("Client Id = " + equipmentId + " connected");
    }

    public void disconnected(int equipmentId) {
        System.out.println("Client Id = " + equipmentId + " disconnected");
    }
}
