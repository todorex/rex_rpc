package com.todorex.common.codec;

import com.todorex.common.bean.RpcResponse;
import com.todorex.common.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * RPC 编码器
 *
 * @Author rex
 * 2018/8/8
 */
public class RpcEncoder extends MessageToByteEncoder{
    /**
     * 用泛型好一些
     */
    private Class<?> genericClass;

    public RpcEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }
    /**
     * 编码
     * 先存长度
     * 再存数据
     * @param channelHandlerContext
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)) {
            byte[] data = SerializationUtil.serialize(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }

    }
}
