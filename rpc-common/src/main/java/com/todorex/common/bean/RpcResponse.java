package com.todorex.common.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC响应
 *
 * @Author rex
 * 2018/8/8
 */
@Slf4j
@Data
@Builder
public class RpcResponse {
    /**
     * 请求ID
     */
    private String requestId;
    /**
     * 异常
     */
    private Exception exception;
    /**
     * 结果
     */
    private Object result;
}
