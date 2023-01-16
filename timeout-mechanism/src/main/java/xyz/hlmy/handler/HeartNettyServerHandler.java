package xyz.hlmy.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @ProjectName: netty
 * @ClassName: HeartNettyServerHandler
 * @Author: lipenghui
 * @Description: 服务端处理类
 * @Date: 2023/01/16 10:28
 */
@Slf4j
public class HeartNettyServerHandler extends ChannelInboundHandlerAdapter {

    private int idle_count = 1;

    private int send_count = 1;

    //超时处理
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //IdleStateEvent :空闲状态事件
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent stateEvent = (IdleStateEvent) evt;
            if (IdleState.READER_IDLE.equals(stateEvent.state())) {
                if (idle_count > 2) {
                    System.out.println("客户端请求两次超时");
                    ctx.channel().closeFuture();
                }
                System.out.println("5秒没收到客户端请求");
                idle_count++;
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    //业务处理
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("第" + send_count + "次" + "，服务端收到的消息:" + msg);
        String message = (String) msg;
        // 如果是心跳命令，服务端收到命令后回复一个相同的命令给客户端
        if ("hf_request".equals(message)) {
            ctx.write("服务端成功收到心跳信息");
            ctx.flush();
        }
        send_count++;
    }

    //异常处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getLocalizedMessage());
        ctx.close();
    }
}
