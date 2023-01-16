package xyz.hlmy.main;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import xyz.hlmy.handler.ChatServerHandler;

/**
 * @ProjectName: Netty-easy-chat
 * @ClassName: ChatServer
 * @Author: lipenghui
 * @Description: 聊的服务端
 * @Date: 2023/01/16 8:27
 */
@Slf4j
public class ChatServer {

    public static void main(String[] args) {

        //处理请求线程组
        EventLoopGroup processAsk = new NioEventLoopGroup();
        //处理请求内容
        EventLoopGroup toolMan = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(processAsk, toolMan)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel sc) throws Exception {
                    sc.pipeline().addLast("decoder", new StringDecoder());
                    sc.pipeline().addLast("encoder", new StringEncoder());
                    sc.pipeline().addLast(new ChatServerHandler());
                }
            });
            log.info("简易聊天室已启动----");
            ChannelFuture cf = serverBootstrap.bind(9000).sync();
            cf.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.toString());
        } finally {
            processAsk.shutdownGracefully();
            toolMan.shutdownGracefully();
        }
    }
}
