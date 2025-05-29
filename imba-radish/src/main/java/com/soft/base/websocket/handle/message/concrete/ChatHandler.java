package com.soft.base.websocket.handle.message.concrete;

import com.alibaba.fastjson2.JSON;
import com.soft.base.enums.WebSocketOrderEnum;
import com.soft.base.websocket.handle.message.WebSocketConcreteHandler;
import com.soft.base.websocket.receive.ChatRecParams;
import com.soft.base.websocket.send.ChatSendParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.AbstractWebSocketMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/5/28 17:37
 **/
@Component
@Slf4j
public class ChatHandler implements WebSocketConcreteHandler<String> {

    private final DeepSeekChatModel chatModel;

    @Autowired
    public ChatHandler(DeepSeekChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public void handle(WebSocketSession session, AbstractWebSocketMessage<String> message) throws IOException {
        ChatRecParams chatRecParams = JSON.parseObject(message.getPayload(), ChatRecParams.class);
        var prompt = new Prompt(new UserMessage(chatRecParams.getQuestion()));
        ChatSendParams chatSendParams = new ChatSendParams();
        chatSendParams.setOrder(WebSocketOrderEnum.AI.toString());
        chatModel.stream(prompt).subscribe(item -> {
            String partialText = item.getResult().getOutput().getText();
            chatSendParams.setAnswer(partialText);
            try {
                session.sendMessage(new TextMessage(chatSendParams.toJsonString()));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }, error -> {
            log.error(error.getMessage());
            try {
                session.sendMessage(new TextMessage("服务异常，请稍后再试"));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.AI;
    }
}
