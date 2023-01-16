package xyz.hlmy.filter;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import xyz.hlmy.handler.HeartNettyServerHandler;

import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: netty
 * @ClassName: HeartNettyServerFilter
 * @Author: lipenghui
 * @Description: 过滤：服务端过滤器，如编解码和心跳的设置
 * @Date: 2023/01/16 10:18
 */
public class HeartNettyServerFilter extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline cp = socketChannel.pipeline();
        cp.addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));

        //解码和编码
        cp.addLast(new StringDecoder());
        cp.addLast(new StringEncoder());

        //处理服务端业务
        cp.addLast(new HeartNettyServerHandler());
    }
}
