package netty.netty.study.data;

public class LastStatus {

    private String status = new String();

    synchronized public void set(String status) {
        this.status = new String(status);
    }

    synchronized public String get() {
        return new String(this.status);
    }
}
