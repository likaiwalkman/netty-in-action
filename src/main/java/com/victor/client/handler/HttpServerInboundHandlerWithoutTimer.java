package com.victor.client.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpServerInboundHandlerWithoutTimer extends ChannelInboundHandlerAdapter {
    //static ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static Log  log = LogFactory.getLog(HttpServerInboundHandlerWithoutTimer.class);
    private HttpRequest request;

    static AtomicLong visitCnt = new AtomicLong();
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("visit: " + visitCnt.incrementAndGet());
        if (msg instanceof FullHttpMessage) {
            //System.out.println(new Date()+"receive a http request");
            ReferenceCountUtil.release(msg);

            System.out.println(new Date()+":channel promise complete");
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer("Ok".getBytes()));

            response.headers().add("Content-Type", "text/html");
            response.headers().add("Content-Length", 2);
            response.headers().add("Connection", "closed");
            ctx.channel().writeAndFlush(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage());
        ctx.close();
    }
}