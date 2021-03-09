package netty.netty.study.utils;

public class StackTraceUtils {

    static public String getCallerFunc() {
        final int callerIndex = 2; // 유틸리티 함수 : 0, 실제 Callee : 1, Caller : 2
        StackTraceElement[] stack = new Throwable().getStackTrace();
        StackTraceElement caller = stack[callerIndex];
        return caller.getMethodName();
    }

    static public String getCurrentFunc() {
        final int callerIndex = 1; // 유틸리티 함수 : 0, 실제 Callee : 1, Caller : 2
        StackTraceElement[] stack = new Throwable().getStackTrace();
        StackTraceElement caller = stack[callerIndex];
        return caller.getMethodName();
    }

    static public String getCallerClass() {
        final int callerIndex = 2; // 유틸리티 함수 : 0, 실제 Callee : 1, Caller : 2
        StackTraceElement[] stack = new Throwable().getStackTrace();
        StackTraceElement caller = stack[callerIndex];
        return caller.getClassName();
    }

    static public String getCurrentClass() {
        final int callerIndex = 1; // 유틸리티 함수 : 0, 실제 Callee : 1, Caller : 2
        StackTraceElement[] stack = new Throwable().getStackTrace();
        StackTraceElement caller = stack[callerIndex];
        return caller.getClassName();
    }
}
