package com.soft.base.constants;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2024/11/15 21:40
 **/
public class RabbitmqConstant {

    public static class Direct {
        /**
         * 直接交换机队列
         */
        public static final String QUEUE_ONE = "radish-direct-queue";

        /**
         * 直接交换机
         */
        public static final String EXCHANGE = "radish-direct-exchange";

        /**
         * 直接交换机路由
         */
        public static final String ROUTE_KEY_ONE = "radish-direct-route-key";
    }

    public static class Topic {
        /**
         * 主题交换机队列
         */
        public static final String QUEUE_SEND_EMAIL = "radish-topic-email";

        /**
         * 主题交换机
         */
        public static final String EXCHANGE = "radish-topic-exchange";

        /**
         * 主题交换机路由
         */
        public static final String ROUTE_KEY_EMAIL = "radish.topic.email";
    }

    public static class DeadLetter {
        /**
         * 死信队列
         */
        public static final String DEAD_LETTER_QUEUE = "radish-queue-dead";

        /**
         * 死信交换机
         */
        public static final String DEAD_LETTER_EXCHANGE = "radish-exchange-dead";

        /**
         * 死信路由
         */
        public static final String DEAD_LETTER_ROUTING_KEY = "radish-dead-rout-key";
    }







}
