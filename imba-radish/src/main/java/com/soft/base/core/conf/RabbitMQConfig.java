package com.soft.base.core.conf;

import com.soft.base.constants.RabbitmqConstant;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: cyx
 * @Description: rabbitmq配置
 * @DateTime: 2024/11/15 11:19
 **/

@Configuration
public class RabbitMQConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // 设置JSON转换器，支持任意POJO对象序列化/反序列化
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // 消费端也设置Jackson2JsonMessageConverter，用于反序列化
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        // 消息手动确认
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        // 每次从队列取出一条消息消费
        factory.setPrefetchCount(1);
        // 拒绝重试
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    @Bean(name = "directQueueOne")
    public Queue directQueueOne() {
        return QueueBuilder
                .nonDurable(RabbitmqConstant.Direct.QUEUE_ONE)
                .deadLetterExchange(RabbitmqConstant.DeadLetter.DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(RabbitmqConstant.DeadLetter.DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean(name = "directExchange")
    public DirectExchange directExchange() {
        return ExchangeBuilder
                .directExchange(RabbitmqConstant.Direct.EXCHANGE)
                .durable(false)
                .build();
    }

    @Bean(name = "directBindingOne")
    public Binding directBindingOne(@Qualifier(value = "directQueueOne") Queue directQueueOne,
                                    @Qualifier(value = "directExchange") DirectExchange directExchange) {
        return BindingBuilder
                .bind(directQueueOne)
                .to(directExchange)
                .with(RabbitmqConstant.Direct.ROUTE_KEY_ONE);
    }

    @Bean(name = "topicQueueEmail")
    public Queue topicQueueEmail() {
        return QueueBuilder
                .nonDurable(RabbitmqConstant.Topic.QUEUE_SEND_EMAIL)
                .deadLetterExchange(RabbitmqConstant.DeadLetter.DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(RabbitmqConstant.DeadLetter.DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean(name = "topicExchange")
    public TopicExchange topicExchange() {
        return ExchangeBuilder
                .topicExchange(RabbitmqConstant.Topic.EXCHANGE)
                .durable(false)
                .build();
    }

    @Bean(name = "topicBindingEmail")
    public Binding topicBindingEmail(@Qualifier(value = "topicQueueEmail") Queue topicQueueEmail,
                                            @Qualifier(value = "topicExchange") TopicExchange topicExchange) {
        return BindingBuilder
                .bind(topicQueueEmail)
                .to(topicExchange)
                .with(RabbitmqConstant.Topic.ROUTE_KEY_EMAIL);
    }

    @Bean(name = "deadLetterQueue")
    public Queue deadLetterQueue() {
        return QueueBuilder
                .nonDurable(RabbitmqConstant.DeadLetter.DEAD_LETTER_QUEUE)
                .maxLength(100)
                .ttl(1000)
                .build();
    }

    @Bean(name = "deadLetterExchange")
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder
                .directExchange(RabbitmqConstant.DeadLetter.DEAD_LETTER_EXCHANGE)
                .durable(false)
                .build();
    }

    @Bean(name = "deadLetterBinding")
    public Binding deadLetterBinding(@Qualifier("deadLetterQueue") Queue deadLetterQueue,
                                     @Qualifier("deadLetterExchange") DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(RabbitmqConstant.DeadLetter.DEAD_LETTER_ROUTING_KEY);
    }
}
