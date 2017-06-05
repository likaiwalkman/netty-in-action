package com.victor.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpContent;
//import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;

import java.util.concurrent.atomic.AtomicLong;

public class HttpClientInboundHandler extends ChannelInboundHandlerAdapter {

    static AtomicLong visitCnt = new AtomicLong();
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpResponse) {
            //HttpResponse response = (HttpResponse) msg;
            //System.out.println("CONTENT_TYPE:" + response.headers().get(HttpHeaders.Names.CONTENT_TYPE));
        }
        if (msg instanceof HttpContent) {
            if (msg instanceof FullHttpMessage) {
                HttpContent content = (HttpContent) msg;
                ByteBuf     buf     = content.content();
                System.out.println(buf.toString(io.netty.util.CharsetUtil.UTF_8));
                String s = buf.toString(CharsetUtil.UTF_8);
                System.out.println(s+"@" + System.currentTimeMillis());
                buf.release();
                ctx.channel().close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("error");
        System.out.println(cause);
        ctx.channel().close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("User Triggered" + evt);
    }
}

