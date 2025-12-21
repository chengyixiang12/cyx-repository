package com.soft.base.core.conf;

import com.soft.base.constants.RabbitmqConstant;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
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
        return factory;
    }

    @Bean(name = "directQueueOne")
    public Queue directQueueOne() {
        return new Queue(RabbitmqConstant.DIRECT_QUEUE_ONE, false, false, false);
    }

    @Bean(name = "directExchange")
    public DirectExchange directExchange() {
        return new DirectExchange(RabbitmqConstant.DIRECT_EXCHANGE, false, false);
    }

    @Bean(name = "directBindingOne")
    public Binding directBindingOne() {
        return BindingBuilder
                .bind(directQueueOne())
                .to(directExchange())
                .with(RabbitmqConstant.DIRECT_ROUTEKEY_ONE);
    }

    @Bean(name = "topicQueueEmail")
    public Queue topicQueueEmail() {
        return new Queue(RabbitmqConstant.TOPIC_QUEUE_SEND_EMAIL, false, false, false);
    }

    @Bean(name = "topicQueueDead")
    public Queue topicQueueDead() {
        return new Queue(RabbitmqConstant.TOPIC_QUEUE_SEND_DEAD, false, false, false);
    }

    @Bean(name = "topicExchange")
    public TopicExchange topicExchange() {
        return new TopicExchange(RabbitmqConstant.TOPIC_EXCHANGE, false, false);
    }

    @Bean(name = "topicBindingDead")
    public Binding topicBindingDead() {
        return BindingBuilder
                .bind(topicQueueDead())
                .to(topicExchange())
                .with(RabbitmqConstant.TOPIC_ROUTE_KEY_DEAD);
    }

    @Bean(name = "topicBindingRestPassword")
    public Binding topicBindingRestPassword() {
        return BindingBuilder
                .bind(topicQueueEmail())
                .to(topicExchange())
                .with(RabbitmqConstant.TOPIC_ROUTE_KEY_EMAIL);
    }
}
