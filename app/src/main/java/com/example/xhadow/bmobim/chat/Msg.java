package com.example.xhadow.bmobim.chat;


/**
 * @description:
 * @author: Xeldow
 * @date: 2019/4/25
 */
public class Msg {
    /**
     * 内容
     */
    private String content;

    /**
     * 类型
     */
    private TYPE type;


    public enum TYPE {
        /**
         * 接收
         */
        RECEIVED,
        /**
         * 发送
         */
        SENT,
        /**
         * 发送图片
         */
        SENT_IMG,
        /**
         * 接收图片
         */
        RECEIVED_IMG
    }

    public Msg(String content, TYPE type) {
        this.content = content;
        this.type = type;
    }

    public TYPE getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}