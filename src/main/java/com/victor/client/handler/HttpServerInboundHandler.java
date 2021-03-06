package com.victor.client.handler;

import com.victor.client.api.singleport.WrapperListener;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelPromise;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
public class HttpServerInboundHandler extends ChannelInboundHandlerAdapter {
    static ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static Log  log = LogFactory.getLog(HttpServerInboundHandler.class);
    private HttpRequest request;

    static AtomicLong visitCnt = new AtomicLong();
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("visit: " + visitCnt.incrementAndGet());
        if (msg instanceof FullHttpMessage) {
            //System.out.println(new Date()+"receive a http request");
            ReferenceCountUtil.release(msg);

            final ChannelPromise channelPromise = ctx.newPromise();

            final WrapperListener listener = new WrapperListener();
            listener.setCtx(ctx);
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                                HttpResponseStatus.OK,
                                Unpooled.wrappedBuffer("Ok".getBytes()));

                        response.headers().add("Content-Type", "text/html");
                        response.headers().add("Content-Length", 2);
                        response.headers().add("Connection", "closed");
                        listener.setRESULT(response);
                        //Thread.sleep(5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    channelPromise.setSuccess();
                }
            });
            channelPromise.addListener(listener);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage());
        ctx.close();
    }
}