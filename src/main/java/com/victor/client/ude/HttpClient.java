package com.victor.client.ude;


import com.victor.client.handler.HttpClientInboundHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpClient {
    private Bootstrap b;

    public HttpClient() {
        EventLoopGroup workerGroup = new NioEventLoopGroup(5);
        Bootstrap      b           = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
                ch.pipeline().addLast(new HttpResponseDecoder());
                // 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
                ch.pipeline().addLast(new HttpRequestEncoder());
                ch.pipeline().addLast(new HttpClientInboundHandler());
                ch.pipeline().addLast(new CallBackInBoundHandler());
            }
        });
        this.b = b;
    }

    public static void main(String[] args) throws Exception {
        String os = System.getProperty("os.name");
        if (os.startsWith("Windows")){
            System.setOut(new PrintStream(new FileOutputStream("D:/PubGit/netty-in-action/out.o")));
        }else {
            System.setOut(new PrintStream(new FileOutputStream("/Users/victor/git/netty-in-action/out.o")));
        }
        final HttpClient client = new HttpClient();

        int concurrent  = 1000;
        final CyclicBarrier barrier = new CyclicBarrier(concurrent+1);
        ExecutorService executorService = Executors.newFixedThreadPool(concurrent);
        for (int i = 0; i < concurrent; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        barrier.await();
                        client.connect("cn.bing.com", 80);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        System.out.println("ABCDEFG"+System.currentTimeMillis());
        barrier.await();
    }

    public void connect(final String host, int port) throws Exception {
        //EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // Start the client.
            final ChannelFuture f = this.b.connect(new InetSocketAddress(host, port));
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println(future.isDone());
                    URI    uri = new URI("http://cn.bing.com/rms/rms%20answers%20Identity%20Blue$BlueIdentityDropdownBootStrap/jc,nj/c0fac2c5/89faaefc.js");
                    String msg = "";//Constant.LONG_STRING;
                    DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
                            uri.toASCIIString(), Unpooled.wrappedBuffer(msg.getBytes("UTF-8")));

                    // 构建http请求
                    request.headers().set(HttpHeaders.Names.HOST, host);
                    request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                    request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());
                    // 发送http请求
                    f.channel().write(request);
                    f.channel().flush().closeFuture().addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            System.out.println(future.isDone());
                            Void aVoid = future.get();
                            System.out.println(aVoid);
                        }
                    });
                }
            });

            Thread.sleep(1000);

        } finally {
            //workerGroup.shutdownGracefully();
        }

    }
}
