package com.victor.client.api.singleport;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * wrapper of result mixed in future listener
 * Created by victor on 6/7/17.
 */
public class WrapperListener implements GenericFutureListener<Future<? super Void>>{

    static String STATUS = "status";
    static String RESULT = "result";
    private ChannelHandlerContext ctx;

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    private Map<String, Object> map = new ConcurrentHashMap<>();
    {
        map.put(STATUS, false);
        map.put(RESULT, null );
    }
    public void setRESULT( DefaultFullHttpResponse buf) {
        this.map.put(RESULT, buf);
    }


    @Override
    public void operationComplete(Future<? super Void> future) throws Exception {
        //System.out.println(new Date()+":channel promise complete");
        if (future.isSuccess()) {
            ctx.channel().writeAndFlush(map.get(RESULT));
        }else {
            ctx.close();
        }
        //ctx.channel().writeAndFlush(response);
    }

}
