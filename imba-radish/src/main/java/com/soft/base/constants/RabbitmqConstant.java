package com.soft.base.constants;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2024/11/15 21:40
 **/
public class RabbitmqConstant {

    /****************************************直接交换机******************************************/
    /**
     * 直接交换机队列
     */
    public static final String DIRECT_QUEUE_ONE = "radish-direct-queue";

    /**
     * 直接交换机
     */
    public static final String DIRECT_EXCHANGE = "radish-direct-exchange";

    /**
     * 直接交换机路由
     */
    public static final String DIRECT_ROUTEKEY_ONE = "radish-direct-route-key";

    /*****************************************主题交换机*****************************************/
    /**
     * 主题交换机队列
     */
    public static final String TOPIC_QUEUE_SEND_DEAD = "radish-topic-queue-dead";
    public static final String TOPIC_QUEUE_SEND_EMAIL = "radish-topic-email";

    /**
     * 主题交换机
     */
    public static final String TOPIC_EXCHANGE = "radish-topic-exchange";

    /**
     * 主题交换机路由
     */
    public static final String TOPIC_ROUTE_KEY_DEAD = "radish.topic.dead";
    public static final String TOPIC_ROUTE_KEY_EMAIL = "radish.topic.email";


}
