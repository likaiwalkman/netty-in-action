package com.victor.client.api.multiport;

import com.victor.client.handler.HttpServerInboundHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class HttpServerBoundMultiPort {

    public static void main(String[] args) {
        EventLoopGroup  bossGroup = new NioEventLoopGroup(5);
        EventLoopGroup  workerGroup = new NioEventLoopGroup(10);
        ServerBootstrap b           = new ServerBootstrap();
        ServerBootstrap serverBootstrap = b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("decoder", new HttpRequestDecoder());
                        ch.pipeline().addLast("aggregator", new HttpObjectAggregator(10*1000));
                        ch.pipeline().addLast("biz",new HttpServerInboundHandler());
                        ch.pipeline().addLast("encoder", new HttpResponseEncoder());
                    }
                });
        ChannelFuture bind1 = serverBootstrap.bind(8081);
        ChannelFuture bind2 = serverBootstrap.bind(8082);
        ChannelFuture bind3 = serverBootstrap.bind(8083);
        ChannelFuture bind4 = serverBootstrap.bind(8084);
        ChannelFuture[] futures = {bind1, bind2, bind3, bind4};
        for (ChannelFuture future : futures) {
            future.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    System.out.println(future.isSuccess());
                }
            });
        }

    }
}
