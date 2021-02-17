package netty.netty.study.data;

import lombok.Synchronized;

public class LastStatus<T> implements Updatable<T>, Copyable<T>{

    private T value;

    @Synchronized
    public void update(T value) {
        this.value = value;
    }

    @Synchronized
    public T copy() {
        return value;
    }
}
