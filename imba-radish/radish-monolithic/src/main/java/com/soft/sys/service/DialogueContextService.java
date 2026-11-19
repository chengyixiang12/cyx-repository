package com.soft.sys.service;

import org.springframework.ai.chat.messages.Message;

import java.util.List;

/**
 * 对话上下文构建与压缩：保留最近若干轮原文，把更早历史压成摘要后注入 system prompt
 */
public interface DialogueContextService {

    /**
     * 构建包含历史摘要的 system prompt
     */
    String buildSystemPrompt(Long dialogueId);

    /**
     * 构建最近若干轮原文消息（user/assistant 交替，时间正序）
     */
    List<Message> buildMessages(Long dialogueId);

    /**
     * 异步检查并压缩已挤出保留窗口的旧消息
     */
    void compressIfNeeded(Long dialogueId);
}
