package com.soft.sys.websocket.registry;

import com.soft.sys.websocket.api.WebSocketConcreteHandler;
import com.soft.sys.websocket.api.WebSocketConcreteHolder;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
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
            WebSocketConcreteHolder.addConcreteHandler(c.getOrder().toString(), c);
            log.debug("{}处理器注入成功", c.getOrder());
        }
    }
}
