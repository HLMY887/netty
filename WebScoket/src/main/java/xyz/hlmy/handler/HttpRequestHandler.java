package xyz.hlmy.handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;
import lombok.extern.slf4j.Slf4j;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @ProjectName: netty
 * @ClassName: HttpRequestHandler
 * @Author: lipenghui
 * @Description: 请求处理
 * @Date: 2023/01/16 11:31
 * FullHttpRequest：完整的HTTP请求
 */
@Slf4j
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String Uri;

    private static final File INDEX;

    static {
        //TODO getProtectionDomain:获取保护域;getCodeSource:获取代码源; getLocation:获取位置
        try {
            URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();
            String path = location.toURI() + "index.html";
            path = !path.contains("file:") ? path : path.substring(5);
            INDEX = new File(path);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpRequestHandler(String uri) {
        Uri = uri;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        //equalsIgnoreCase:忽略大小写
        if (Uri.equalsIgnoreCase(request.uri())) {
            ctx.fireChannelRead(request.retain());
        } else {
            if (HttpHeaders.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }
            //RandomAccessFile：随机访问文件
            RandomAccessFile file = new RandomAccessFile(INDEX, "r");//4
            HttpResponse response = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
            boolean keepAlive = HttpHeaders.isKeepAlive(request);
            if (keepAlive) {                                        //5
                response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
                response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }
            ctx.write(response);                    //6
            if (ctx.pipeline().get(SslHandler.class) == null) {     //7
                ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
            } else {
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }
            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);           //8
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);        //9
            }
            file.close();
        }
    }

    //请求字节大于1024调用
    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    //异常处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getLocalizedMessage());
    }
}
