package netty.netty.study.data;

import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;

import java.util.Map;

@UtilityClass
public class Messaging {

    public void info(int camId, String code, @Nullable Map<String, Object> params) {
        System.out.println("camId = " + camId + ", code = " + code + ", params = " + params);
    }

    public void info(int camId, String code) {
        System.out.println("camId = " + camId + ", code = " + code);
    }

    public void warning(int camId, String code, @Nullable Map<String, Object> params) {
        System.out.println("camId = " + camId + ", code = " + code + ", params = " + params);
    }

    public void warning(int camId, String code) {
        System.out.println("camId = " + camId + ", code = " + code);
    }

    public void error(int camId, String code, @Nullable Map<String, Object> params) {
        System.out.println("camId = " + camId + ", code = " + code + ", params = " + params);
    }

    public void error(int camId, String code) {
        System.out.println("camId = " + camId + ", code = " + code);
    }

    public void connected(int camId) {
        System.out.println("camId = " + camId + " connected");
    }


    public void disconnected(int camId) {
        System.out.println("camId = " + camId + " disconnected");
    }
}
