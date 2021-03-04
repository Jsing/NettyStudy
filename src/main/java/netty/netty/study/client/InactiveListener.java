package netty.netty.study.client;

public interface InactiveListener {
    /**
     * 채널 Inactive 이벤트 발생 시 리스너 호출
     */
    void channelInactiveOccurred();
}
