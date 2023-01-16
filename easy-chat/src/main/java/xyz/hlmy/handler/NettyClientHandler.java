package xyz.hlmy.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ProjectName: Netty-easy-chat
 * @ClassName: NettyClientHandler
 * @Author: lipenghui
 * @Description: 简易处理
 * @Date: 2023/01/16 9:04
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println(s.trim());
    }
}
