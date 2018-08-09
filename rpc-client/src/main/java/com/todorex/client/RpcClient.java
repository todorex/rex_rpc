package com.todorex.client;

import com.todorex.common.bean.RpcRequest;
import com.todorex.common.bean.RpcResponse;
import com.todorex.common.codec.RpcDecoder;
import com.todorex.common.codec.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC 客户端（用于发送RPC请求）
 * 同时接受请求
 * @Author rex
 * 2018/8/8
 */
@Slf4j
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse>{

    private final String host;

    private final int port;

    private RpcResponse rpcResponse;

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        this.rpcResponse = rpcResponse;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("caught exception", cause);
        ctx.close();
    }

    public RpcResponse send(RpcRequest request) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建Netty客户端
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    // 编码 RPC 请求
                    pipeline.addLast(new RpcEncoder(RpcRequest.class));
                    // 解码 RPC 响应
                    pipeline.addLast(new RpcDecoder(RpcResponse.class));
                    // 处理 RPC 响应
                    pipeline.addLast(RpcClient.this);
                }
            });
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            // 连接 RPC 服务器
            ChannelFuture future = bootstrap.connect(host, port).sync();
            // 写入 RPC 请求数据并关闭连接
            Channel channel = future.channel();
            channel.writeAndFlush(request).sync();
            channel.closeFuture().sync();
            // 返回 RPC 响应对象
            return rpcResponse;
        } finally {
            group.shutdownGracefully();
        }
    }
}
