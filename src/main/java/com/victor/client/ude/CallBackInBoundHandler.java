package com.victor.client.ude;

import com.victor.client.type.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 *  registered callback handler to be invoked
 * Created by likai on 2016/12/6.
 */
public class CallBackInBoundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object resolution) {
        if (resolution instanceof Response) { // do something}
            System.err.println(resolution);
        }
    }
}