package com.soft.base.conf;

import com.soft.base.constants.RabbitmqConstant;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: cyx
 * @Description: rabbitmq配置
 * @DateTime: 2024/11/15 11:19
 **/

@Configuration
public class RabbitMQConfig {

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

    @Bean(name = "topicQueueRegist")
    public Queue topicQueueRegist() {
        return new Queue(RabbitmqConstant.TOPIC_QUEUE_SEND_REGIST_CAPTCHA, false, false, false);
    }

    @Bean(name = "topicQueueLogin")
    public Queue topicQueueLogin() {
        return new Queue(RabbitmqConstant.TOPIC_QUEUE_SEND_LOGIN_CAPTCHA, false, false, false);
    }

    @Bean(name = "topicQueueDead")
    public Queue topicQueueDead() {
        return new Queue(RabbitmqConstant.TOPIC_QUEUE_SEND_DEAD, false, false, false);
    }

    @Bean(name = "topicExchange")
    public TopicExchange topicExchange() {
        return new TopicExchange(RabbitmqConstant.TOPIC_EXCHANGE, false, false);
    }

    @Bean(name = "topicBindingLogin")
    public Binding topicBindingLogin() {
        return BindingBuilder
                .bind(topicQueueLogin())
                .to(topicExchange())
                .with(RabbitmqConstant.TOPIC_ROUTE_KEY_LOGIN);
    }

    @Bean(name = "topicBindingRegist")
    public Binding topicBindingRegist() {
        return BindingBuilder
                .bind(topicQueueRegist())
                .to(topicExchange())
                .with(RabbitmqConstant.TOPIC_ROUTE_KEY_REGIST);
    }

    @Bean(name = "topicBindingDead")
    public Binding topicBindingDead() {
        return BindingBuilder
                .bind(topicQueueDead())
                .to(topicExchange())
                .with(RabbitmqConstant.TOPIC_ROUTE_KEY_DEAD);
    }
}
