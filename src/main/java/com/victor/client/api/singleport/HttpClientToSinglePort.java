package com.victor.client.api.singleport;


import com.victor.client.Constant;
import com.victor.client.handler.HttpClientInboundHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HttpClientToSinglePort {
    private Bootstrap b;

    public HttpClientToSinglePort() {
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);
        Bootstrap      b           = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
                ch.pipeline().addLast(new HttpResponseDecoder());
                ch.pipeline().addLast(new HttpObjectAggregator(2*1000));
                //ch.pipeline().addLast(new IdleStateHandler(5,5,5, TimeUnit.SECONDS));
                ch.pipeline().addLast(new HttpClientInboundHandler());
                // 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
                ch.pipeline().addLast(new HttpRequestEncoder());
            }
        });
        //b.option(ChannelOption.SO_TIMEOUT, 5);
        b.option(ChannelOption.SO_KEEPALIVE, false);
        this.b = b;
    }

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(new FileOutputStream("/Users/victor/git/netty-in-action/out.o")));
        final HttpClientToSinglePort client = new HttpClientToSinglePort();

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        final int port = 8080;
        System.err.println(new Date());
        for (int i = 0; i < 10000; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        client.connect("localhost", port, "http://localhost:"+port);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void connect(final String host, int port, final String url) throws Exception {
        //EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // Start the client.
            final ChannelFuture f = this.b.connect(new InetSocketAddress(host, port));
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()){
                        System.out.println("conns");
                        send(future, url, f);
                    } else {
                        System.out.println("connf");
                    }
                }
            });

        } catch (Throwable e) {
            System.out.println("unknown:"+e);
        }finally {
            //workerGroup.shutdownGracefully();
        }

    }

    private void send(ChannelFuture future, String url, ChannelFuture f) throws URISyntaxException, UnsupportedEncodingException {
        System.out.println(future.isDone());
        URI    uri = new URI(url);
        String msg = Constant.LONG_STRING;
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
                uri.toASCIIString(), Unpooled.wrappedBuffer("".getBytes("UTF-8")));

        // 构建http请求
        request.headers().set(HttpHeaders.Names.HOST, "localhost");
        //request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());
        // 发送http请求
        f.channel().write(request);
        f.channel().flush().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
//                System.out.println(future.isDone());
//                Void aVoid = future.get();
//                System.out.println(aVoid);
            }
        });
    }
}
