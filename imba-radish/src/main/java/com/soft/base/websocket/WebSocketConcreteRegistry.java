package com.soft.base.websocket;

import com.soft.base.websocket.handle.message.WebSocketConcreteHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: cyx
 * @Description: 用于将配置的具体逻辑处理器注册到缓存中
 * @DateTime: 2024/11/22 11:42
 **/

@Component
@Slf4j
public class WebSocketConcreteRegistry {

    private final List<WebSocketConcreteHandler<?>> webSocketConcreteHandlers;

    @Autowired
    public WebSocketConcreteRegistry(List<WebSocketConcreteHandler<?>> webSocketConcreteHandlers) {
        this.webSocketConcreteHandlers = webSocketConcreteHandlers;
    }

    /**
     * 将具体逻辑加入到WebSocketConcreteHolder中统一管理
     */
    @PostConstruct
    public void init() {
        log.info("开始注入websocket消息处理器");
        for (WebSocketConcreteHandler<?> c : webSocketConcreteHandlers) {
            String order = c.getOrder().toString();
            WebSocketConcreteHolder.addConcreteHandler(order, c);
            log.debug("{}处理器注入成功", order);
        }
        log.info("websocket处理器注入完成");
    }
}
