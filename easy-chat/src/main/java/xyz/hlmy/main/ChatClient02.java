package xyz.hlmy.main;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import xyz.hlmy.handler.NettyClientHandler;

import java.util.Scanner;

/**
 * @ProjectName: Netty-easy-chat
 * @ClassName: ChatClient
 * @Author: lipenghui
 * @Description: 简易聊天客户端
 * @Date: 2023/01/16 9:00
 */
public class ChatClient02 {
    public static void main(String[] args) {
        EventLoopGroup group02 = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group02).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new StringDecoder());
                    socketChannel.pipeline().addLast(new StringEncoder());
                    //加入处理器
                    socketChannel.pipeline().addLast(new NettyClientHandler());

                }
            });
            ChannelFuture channelFuture = bootstrap.connect("192.168.125.70", 9000).sync();
            Channel channel = channelFuture.channel();
            System.out.println("客户端启动成功:" + channel.localAddress());
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                channel.writeAndFlush(msg);
            }
            //对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group02.shutdownGracefully();
        }
    }
}
