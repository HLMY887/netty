package xyz.hlmy.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import xyz.hlmy.filter.HeartNettyServerFilter;

/**
 * @ProjectName: netty
 * @ClassName: HearNettyServer
 * @Author: lipenghui
 * @Description: 心跳服务端
 * @Date: 2023/01/16 10:13
 */
public class HearNettyServer {
    public static void main(String[] args) throws InterruptedException {
        //启动引导类
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        //父线程组
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        //子线程组
        EventLoopGroup childGroup = new NioEventLoopGroup();
        serverBootstrap
                //parentGroup负责连接，childGroup负责处理
                .group(parentGroup, childGroup)
                //设置线程多少
                .option(ChannelOption.SO_BACKLOG, 128)
                //绑定服务通道
                .channel(NioServerSocketChannel.class)
                //绑定handler，处理读写事件，ChannelInitializer是给通道初始化
                .childHandler(new HeartNettyServerFilter());
        //绑定端口
        ChannelFuture future = serverBootstrap.bind("192.168.125.70",8888).sync();
        //当通道关闭 继续执行
        future.channel().closeFuture().sync();
    }
}
