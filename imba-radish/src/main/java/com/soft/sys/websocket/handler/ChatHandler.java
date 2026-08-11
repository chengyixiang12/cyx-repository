package com.soft.sys.websocket.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.soft.module.thirdapi.gaode.ThirdInterface;
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
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
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

    private static final String SYSTEM_PROMPT = "使用中文回答。";

    @Value(value = "${spring.ai.max-context-num}")
    private Long maxContextNum;

    private final ChatClient.Builder chatClientBuilder;

    private final SysDialogueDetailsService sysDialogueDetailsService;

    private final ThirdInterface thirdInterface;

    @Override
    public void handle(WebSocketSession session, AbstractWebSocketMessage<String> message) throws IOException {
        ChatRequest chatRecParam = JSON.parseObject(message.getPayload(), ChatRequest.class);
        UserDTO user = (UserDTO) session.getAttributes().get(WebSocketConstant.WEBSOCKET_USER);

        // 问题入库
        SysDialogueDetails question = new SysDialogueDetails();
        question.setCreateBy(user.getId());
        question.setUpdateBy(user.getId());
        question.setContent(chatRecParam.getQuestion());
        question.setTag(BaseConstant.CHAT_TAG_USER);
        question.setParentId(chatRecParam.getDialogueId());
        sysDialogueDetailsService.save(question);

        ChatResponse chatSendParams = new ChatResponse();
        chatSendParams.setOrder(WebSocketOrderEnum.AI.toString());
        StringBuilder answerStr = new StringBuilder();

        chatClientBuilder.build()
                .prompt(SYSTEM_PROMPT)
                .tools(thirdInterface)
                .messages(buildMessages(chatRecParam.getDialogueId()))
                .stream()
                .content()
                .retry(10)
                .doOnNext(partialText -> {
                    if (partialText.isEmpty()) {
                        return;
                    }
                    answerStr.append(partialText);
                    chatSendParams.setAnswer(partialText);
                    if (session.isOpen()) {
                        try {
                            session.sendMessage(new TextMessage(chatSendParams.toJsonString()));
                        } catch (IOException e) {
                            log.error("发送 AI 回答失败: {}", e.getMessage(), e);
                        }
                    }
                })
                .doOnError(error -> {
                    log.error("AI 流式响应异常: {}", error.getMessage(), error);
                    if (session.isOpen()) {
                        try {
                            session.sendMessage(new TextMessage(ResultEnum.FAIL_NORMAL.getMessage()));
                        } catch (IOException e) {
                            log.error("发送错误消息失败: {}", e.getMessage(), e);
                        }
                    }
                })
                .doOnComplete(() -> saveAnswer(chatRecParam, user, answerStr.toString()))
                .subscribe();
    }

    /**
     * 组装历史上下文
     * @param dialogueId
     * @return
     */
    private List<Message> buildMessages(Long dialogueId) {
        List<GetRecentContentDTO> recentContext = sysDialogueDetailsService.getRecentContext(dialogueId, maxContextNum);

        List<Message> messages = new ArrayList<>();
//        messages.add(new SystemMessage(SYSTEM_PROMPT));

        if (CollectionUtil.isNotEmpty(recentContext)) {
            for (GetRecentContentDTO dto : recentContext) {
                if (BaseConstant.CHAT_TAG_USER.equals(dto.getTag())) {
                    messages.add(new UserMessage(dto.getContent()));
                } else if (BaseConstant.CHAT_TAG_AI.equals(dto.getTag())) {
                    messages.add(new AssistantMessage(dto.getContent()));
                }
            }
        }
        return messages;
    }

    /**
     * 流结束落库;出错时不落库
     * @param chatRecParam
     * @param user
     * @param answer
     */
    private void saveAnswer(ChatRequest chatRecParam, UserDTO user, String answer) {
        if (answer == null || answer.isEmpty()) {
            log.warn("AI 未生成回答内容,跳过保存, dialogueId={}", chatRecParam.getDialogueId());
            return;
        }
        SysDialogueDetails answerEntity = new SysDialogueDetails();
        answerEntity.setParentId(chatRecParam.getDialogueId());
        answerEntity.setContent(answer);
        answerEntity.setTag(BaseConstant.CHAT_TAG_AI);
        answerEntity.setCreateBy(user.getId());
        answerEntity.setUpdateBy(user.getId());
        sysDialogueDetailsService.save(answerEntity);
    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.AI;
    }
}
