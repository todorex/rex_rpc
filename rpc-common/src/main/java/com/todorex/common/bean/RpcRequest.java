package com.todorex.common.bean;

import lombok.Builder;
import lombok.Data;

/**
 * RPC 请求
 * @Author rex
 * 2018/8/8
 */
@Data
@Builder
public class RpcRequest {
    /**
     * 请求ID
     */
    private String requestId;
    /**
     * 接口名称
     */
    private String interfaceName;
    /**
     * 接口版本号
     */
    private String serviceVersion;
    /**
     * 方法名称
     */
    private String methodName;
    /**
     * 参数类型
     */
    private Class<?>[] parameterTypes;
    /**
     * 参数
     */
    private Object[] parameters;
}
