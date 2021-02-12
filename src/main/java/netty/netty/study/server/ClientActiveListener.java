package netty.netty.study.server;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;

public interface ClientActiveListener {

    void clientActivated(Channel channel, InetSocketAddress clientAddress);
}
