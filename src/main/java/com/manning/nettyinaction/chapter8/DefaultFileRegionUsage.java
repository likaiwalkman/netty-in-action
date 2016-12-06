package com.manning.nettyinaction.chapter8;

import io.netty.channel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author <a href="mailto:norman.maurer@googlemail.com">Norman Maurer</a>
 */
public class DefaultFileRegionUsage {
    public static void transfer(Channel channel, File file) throws FileNotFoundException {
        FileInputStream in     = new FileInputStream(file);
        FileRegion      region = new DefaultFileRegion(in.getChannel(), 0, file.length());

        channel.writeAndFlush(region).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    Throwable cause = future.cause();
                    // Do something
                }
            }
        });
    }
}
