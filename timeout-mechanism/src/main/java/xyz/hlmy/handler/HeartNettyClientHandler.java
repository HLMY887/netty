package xyz.hlmy.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

import java.time.LocalDateTime;

/**
 * @ProjectName: netty
 * @ClassName: HeartNettyClientHandler
 * @Author: lipenghui
 * @Description:
 * @Date: 2023/01/16 10:48
 */
public class HeartNettyClientHandler extends ChannelInboundHandlerAdapter {
    //复苏指令
    private static final ByteBuf HEARTBEAT_SEQUENCE =
            Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("hf_request", CharsetUtil.UTF_8));

    /**
     * 空闲次数
     */
    private int idle_count = 1;

    /**
     * 发送次数
     */
    private int send_count = 1;

    /**
     * 循环次数
     */
    private int for_count = 1;

    /**
     * 建立连接时
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("建立连接时：" + LocalDateTime.now());
        ctx.fireChannelActive();
    }

    /**
     * 关闭连接时
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("关闭连接时：" + LocalDateTime.now());
    }

    /**
     * 心跳请求处理，每4秒发送一次心跳请求;
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
        System.out.println("\r\n循环请求的时间：" + LocalDateTime.now() + "，次数" + for_count);
        if (obj instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) obj;
            if (IdleState.WRITER_IDLE.equals(event.state())) { // 如果写通道处于空闲状态就发送心跳命令
                // 设置发送次数，允许发送3次心跳包
                if (idle_count <= 3) {
                    idle_count++;
                    ctx.channel().writeAndFlush(HEARTBEAT_SEQUENCE.duplicate());
                } else {
                    System.out.println("心跳包发送结束，不再发送心跳请求！！！");
                }
            }
        }

        for_count++;
    }

    /**
     * 业务逻辑处理
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("第" + send_count + "次" + "，客户端收到的消息:" + msg);
        send_count++;
    }

}
