package xyz.hlmy.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import xyz.hlmy.filter.HeartNettyClientFilter;

import java.io.IOException;

/**
 * @ProjectName: netty
 * @ClassName: HeartNettyClient
 * @Author: lipenghui
 * @Description: 客户端
 * @Date: 2023/01/16 10:42
 */
public class HeartNettyClient {
    public static void main(String[] args) throws InterruptedException, IOException {
        Bootstrap client = new Bootstrap();

        EventLoopGroup loopGroup = new NioEventLoopGroup();
        //配置
        client.group(loopGroup)
                .channel(NioSocketChannel.class)
                .handler(new HeartNettyClientFilter());

        //连接服务器
        Channel channel = client.connect("192.168.125.70", 8888).sync().channel();

        //发送信息
        String str = "Hello Netty";
        channel.writeAndFlush(str);
        System.out.println("客户端发送数据:" + str);
    }
}
