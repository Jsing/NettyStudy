package netty.netty.study.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Array;
import java.util.Arrays;

@Getter @Setter
public class MyMessage {


    @Getter @Setter
    public static class Header {
        private int opcode;
        private int length;

        Arrays.stream("TEST", "HAN");
    }

    @Getter @Setter
    public static class TypeA extends Header {

        private String stringField;
    }

    @Getter @Setter
    public static class TypeB extends Header {

        private int detailIntField;
        private double detailFloatField;
    }


}
