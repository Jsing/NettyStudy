package netty.netty.study.server;

import io.netty.channel.Channel;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ClientService {

    final private Channel channel;

    public ClientService(Channel channel) {

        assert channel.isActive() == true : "channel is not active";

        this.channel = channel;
    }

    public void init(int initialDelay, int period, TimeUnit unit) {
        ScheduledFuture<?> future = channel.eventLoop().scheduleAtFixedRate(
                new Runnable() {

                    @Override
                    public void run() {
                        System.out.println("periodic call by eventloop");
                    }
                }, initialDelay, period, unit);

    }

    public void end() throws InterruptedException {

        channel.close().sync();
    }

    public void writeMessage(byte[] msg) {

        channel.writeAndFlush(msg);
    }


}
