package netty.netty.study.client;

public interface ChannelStatusListener {
    /**
     * 채널 Active 이벤트 발생
     */
    void channelActive();

    /**
     * 채널 Inactive 이벤트 발생
     */
    void channelInactive();

    /**
     * 채널 Exception 이벤트 발생
     */
    void exceptionCaught();
}
