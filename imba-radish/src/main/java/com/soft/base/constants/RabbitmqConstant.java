package com.soft.base.constants;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/15 21:40
 **/
public class RabbitmqConstant {

    /****************************************直接交换机******************************************/
    /**
     * 直接交换机队列
     */
    public final static String DIRECT_QUEUE_ONE = "cyx-direct-queue";

    /**
     * 直接交换机
     */
    public final static String DIRECT_EXCHANGE = "cyx-direct-exchange";

    /**
     * 直接交换机路由
     */
    public final static String DIRECT_ROUTEKEY_ONE = "cyx-direct-route-key";

    /*****************************************主题交换机*****************************************/
    /**
     * 主题交换机队列
     */
    public final static String TOPIC_QUEUE_SEND_REGIST_CAPTCHA = "cyx-topic-queue-regist";
    public final static String TOPIC_QUEUE_SEND_LOGIN_CAPTCHA = "cyx-topic-queue-login";
    public final static String TOPIC_QUEUE_SEND_DEAD = "cyx-topic-queue-dead";

    /**
     * 主题交换机
     */
    public final static String TOPIC_EXCHANGE = "cyx-topic-exchange";

    /**
     * 主题交换机路由
     */
    public final static String TOPIC_ROUTE_KEY_REGIST = "cyx.topic.regist";
    public final static String TOPIC_ROUTE_KEY_LOGIN = "cyx.topic.login";
    public final static String TOPIC_ROUTE_KEY_DEAD = "cyx.topic.dead";


}
