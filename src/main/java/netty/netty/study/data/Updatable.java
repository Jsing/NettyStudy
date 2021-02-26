package netty.netty.study.data;

public interface Updatable<T> {

    void update(T newStatus);
}
