package netty.netty.study.dto;

public interface Updatable<T> {

    void update(T status);
}
