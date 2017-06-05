package com.victor.client.api.singleport;

//import com.victor.client.handler.HttpServerInboundHandler;
import com.victor.client.handler.HttpServerInboundHandlerWithoutTimer;
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

public class HttpServerBoundSinglePortWithoutTimer {

    public static void main(String[] args) {
        EventLoopGroup  bossGroup = new NioEventLoopGroup(2);
        EventLoopGroup  workerGroup = new NioEventLoopGroup(10);
        ServerBootstrap b           = new ServerBootstrap();
        ServerBootstrap serverBootstrap = b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("decoder", new HttpRequestDecoder());
                        ch.pipeline().addLast("aggregator", new HttpObjectAggregator(10*1000));
                        ch.pipeline().addLast("biz",new HttpServerInboundHandlerWithoutTimer());
                        ch.pipeline().addLast("encoder", new HttpResponseEncoder());
                    }
                });
        ChannelFuture future = serverBootstrap.bind(8080);
        future.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                System.out.println(future.isSuccess());
            }
        });
    }
}
