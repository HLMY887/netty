package xyz.hlmy.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * @ProjectName: Netty-easy-chat
 * @ClassName: ChatServerHandler
 * @Author: lipenghui
 * @Description: 聊天服务处理
 * @Date: 2023/01/16 8:39
 */
@Slf4j
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    //存储所有连接的信道
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //在线
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channels.writeAndFlush("[客户端]" + channel.remoteAddress() + "上线了" + LocalDateTime.now());
        channels.add(channel);
        System.out.println("在线人数" + channels.size());
    }

    //离线
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channels.writeAndFlush("[客户端]" + channel.remoteAddress() + "下线了" + LocalDateTime.now());
        System.out.println("在线人数" + channels.size());
    }

    //读取数据
    @Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        final Channel channel = channelHandlerContext.channel();
        String split = channel.remoteAddress().toString().split(":")[1];
        channels.forEach(ca -> {
            if (channel != ca) {
                ca.writeAndFlush(split + "用户:" + s);
            }
        });
    }

    //异常处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) throws Exception {
        ctx.close();
    }
}
