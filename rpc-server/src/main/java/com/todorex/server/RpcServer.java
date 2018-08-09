package com.todorex.server;

import com.todorex.common.bean.RpcRequest;
import com.todorex.common.bean.RpcResponse;
import com.todorex.common.codec.RpcDecoder;
import com.todorex.common.codec.RpcEncoder;
import com.todorex.common.handler.RpcServerHandler;
import com.todorex.common.util.StringUtil;
import com.todorex.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于Netty的RPC服务器（用于发布RPC服务）
 * @Author rex
 * 2018/8/8
 */
@Slf4j
public class RpcServer implements ApplicationContextAware, InitializingBean {

    private String serviceAddress;

    private ServiceRegistry serviceRegistry;

    /**
     * 存放服务名与服务对象之家的映射关系
     */
    private Map<String, Object> handlerMap = new HashMap<>();

    public RpcServer(String serviceAddress, ServiceRegistry serviceRegistry) {
        this.serviceAddress = serviceAddress;
        this.serviceRegistry = serviceRegistry;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 设置 handler对象
        // 扫描带有RpcService注解的类并初始化 handlerMap对象
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
                String serviceName = rpcService.value().getName();
                String serviceVersion = rpcService.version();
                if (StringUtils.isNotEmpty(serviceVersion)) {
                    serviceName += "-" + serviceVersion;
                }
                handlerMap.put(serviceName, serviceBean);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建Netty服务端
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    // 解码 RPC 请求
                    pipeline.addLast(new RpcDecoder(RpcRequest.class));
                    // 编码 RPC 响应
                    pipeline.addLast(new RpcEncoder(RpcResponse.class));
                    // 处理 RPC 请求
                    pipeline.addLast(new RpcServerHandler(handlerMap));
                }
            });
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            // 获取RPC服务器的IP地址与端口号
            String[] addressArray = StringUtil.split(serviceAddress, ":");
            String ip = addressArray[0];
            int port = Integer.valueOf(addressArray[1]);
            // 启动RPC服务器
            ChannelFuture future = bootstrap.bind(ip, port).sync();
            // 注册RPC服务地址
            if (serviceRegistry != null) {
                for (String interfaceName : handlerMap.keySet()) {
                    serviceRegistry.register(interfaceName, serviceAddress);
                    log.debug("register service: {} => {}", interfaceName, serviceAddress);
                }
            }
            log.debug("server started on port {}", port);
            // 关闭RPC服务器
            future.channel().closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
