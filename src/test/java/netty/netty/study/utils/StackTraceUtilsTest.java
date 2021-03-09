package netty.netty.study.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StackTraceUtilsTest {

    @Test
    void getCallerFuncTest() {
         Assertions.assertTrue(callerFunc().contentEquals("getCallerFuncTest"));
    }

    @Test
    void getCurrentFuncTest() {
        Assertions.assertTrue(currentFunc().contentEquals("currentFunc"));
    }

    String callerFunc() {
        return StackTraceUtils.getCallerFunc();
    }

    String currentFunc() {
        return StackTraceUtils.getCurrentFunc();
    }
}
