package com.soft.sys.websocket.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.soft.sys.constants.BaseConstant;
import com.soft.sys.constants.WebSocketConstant;
import com.soft.sys.entity.SysDialogueDetails;
import com.soft.sys.enums.ResultEnum;
import com.soft.sys.enums.WebSocketOrderEnum;
import com.soft.sys.model.dto.GetRecentContentDTO;
import com.soft.sys.model.dto.UserDTO;
import com.soft.sys.service.SysDialogueDetailsService;
import com.soft.sys.websocket.api.WebSocketConcreteHandler;
import com.soft.sys.websocket.receive.ChatRequest;
import com.soft.sys.websocket.send.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.AbstractWebSocketMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        ChatRequest chatRecParam = JSON.parseObject(message.getPayload(), ChatRequest.class);
        UserDTO user = (UserDTO) session.getAttributes().get(WebSocketConstant.WEBSOCKET_USER);

        // 问题
        SysDialogueDetails question = new SysDialogueDetails();
        question.setCreateBy(user.getId());
        question.setUpdateBy(user.getId());
        question.setContent(chatRecParam.getQuestion());
        question.setTag(BaseConstant.CHAT_TAG_USER);
        question.setParentId(chatRecParam.getDialogueId());
        sysDialogueDetailsService.save(question);
        List<GetRecentContentDTO> recentContext = sysDialogueDetailsService.getRecentContext(chatRecParam.getDialogueId(), maxContextNum);

        List<Message> messages = new ArrayList<>();

        // 添加系统提示词
        messages.add(SystemMessage
                .builder()
                .text("使用中文回答。")
                .build());

        if (CollectionUtil.isNotEmpty(recentContext)) {
            for (GetRecentContentDTO getRecentContentDto : recentContext) {
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

        ChatResponse chatSendParams = new ChatResponse();
        chatSendParams.setOrder(WebSocketOrderEnum.AI.toString());
        StringBuilder answerStr = new StringBuilder();

        // 回答
        SysDialogueDetails answer = new SysDialogueDetails();

        chatModel.stream(prompt).subscribe(item -> {
            String partialText = Objects.requireNonNull(item.getResult()).getOutput().getText();
            if (partialText == null) return;
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
                session.sendMessage(new TextMessage(ResultEnum.FAIL_NORMAL.getMessage()));
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
