package netty.netty.study.server;

import io.netty.channel.Channel;
import netty.netty.study.data.LastStatus;
import netty.netty.study.data.Updatable;
import netty.netty.study.server.handler.ServerServiceHandler;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ServerService {
    private final Channel channel;
    private LastStatus lastStatus = new LastStatus();

    public ServerService(Channel channel) {

        assert channel.isActive() == true : "channel is not active";

        channel.pipeline().get(ServerServiceHandler.class).setUpdateListener(lastStatus);
        this.channel = channel;
    }

    public LastStatus lastStatus() {
        return this.lastStatus;
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

    public void disconnect() throws InterruptedException {

        channel.close().sync();
    }

    public void send(Object msg) {

        channel.writeAndFlush(msg);
    }


}
