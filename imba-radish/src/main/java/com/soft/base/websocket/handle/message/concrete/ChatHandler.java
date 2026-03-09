package com.soft.base.websocket.handle.message.concrete;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.WebSocketConstant;
import com.soft.base.entity.SysDialogueDetails;
import com.soft.base.enums.WebSocketOrderEnum;
import com.soft.base.model.dto.GetRecentContentDto;
import com.soft.base.model.dto.UserDto;
import com.soft.base.service.SysDialogueDetailsService;
import com.soft.base.websocket.handle.message.WebSocketConcreteHandler;
import com.soft.base.websocket.receive.ChatRecParam;
import com.soft.base.websocket.send.ChatSendParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.AbstractWebSocketMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2025/5/28 17:37
 **/
@Component
@Slf4j
@RequiredArgsConstructor
public class ChatHandler implements WebSocketConcreteHandler<String> {

    @Value(value = "${spring.ai.max-context-num}")
    private Long maxContextNum;

    private final DeepSeekChatModel chatModel;

    private final SysDialogueDetailsService sysDialogueDetailsService;

    @Override
    public void handle(WebSocketSession session, AbstractWebSocketMessage<String> message) throws IOException {
        ChatRecParam chatRecParam = JSON.parseObject(message.getPayload(), ChatRecParam.class);
        UserDto user = (UserDto) session.getAttributes().get(WebSocketConstant.WEBSOCKET_USER);

        // 问题
        SysDialogueDetails question = new SysDialogueDetails();
        question.setCreateBy(user.getId());
        question.setUpdateBy(user.getId());
        question.setContent(chatRecParam.getQuestion());
        question.setTag(BaseConstant.CHAT_TAG_USER);
        question.setParentId(chatRecParam.getDialogueId());
        sysDialogueDetailsService.save(question);
        List<GetRecentContentDto> recentContext = sysDialogueDetailsService.getRecentContext(chatRecParam.getDialogueId(), maxContextNum);

        List<Message> messages = new ArrayList<>();

        // 添加系统提示词
        messages.add(SystemMessage.builder().text("你是一名程序员助手，专注于解决java方面的问题，请使用中文回答。").build());

        if (CollectionUtil.isNotEmpty(recentContext)) {
            for (GetRecentContentDto getRecentContentDto : recentContext) {
                Integer tag = getRecentContentDto.getTag();
                String content = getRecentContentDto.getContent();
                if (BaseConstant.CHAT_TAG_USER.equals(tag)) {
                    messages.add(UserMessage.builder().text(content).build());
                } else if (BaseConstant.CHAT_TAG_AI.equals(tag)) {
                    messages.add(AssistantMessage.builder().content(content).build());
                }
            }
        }

        var prompt = new Prompt(messages);

        ChatSendParams chatSendParams = new ChatSendParams();
        chatSendParams.setOrder(WebSocketOrderEnum.AI.toString());
        StringBuilder answerStr = new StringBuilder();

        // 回答
        SysDialogueDetails answer = new SysDialogueDetails();

        chatModel.stream(prompt).subscribe(item -> {
            String partialText = item.getResult().getOutput().getText();
            chatSendParams.setAnswer(partialText);
            try {
                session.sendMessage(new TextMessage(chatSendParams.toJsonString()));
                answerStr.append(partialText);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }, error -> {
            log.error(error.getMessage());
            try {
                session.sendMessage(new TextMessage("服务异常，请联系管理员"));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }, () -> {
            answer.setParentId(chatRecParam.getDialogueId());
            answer.setContent(answerStr.toString());
            answer.setTag(BaseConstant.CHAT_TAG_AI);
            answer.setCreateBy(user.getId());
            answer.setUpdateBy(user.getId());
            sysDialogueDetailsService.save(answer);
        });
    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.AI;
    }
}
