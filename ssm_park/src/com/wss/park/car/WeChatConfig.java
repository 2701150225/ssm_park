package com.wss.park.car;

public class WeChatConfig {
	
    public static final String APPID = WeChatProperties.get("appID");
    public static final String APPKEY = WeChatProperties.get("appKey");
    public static final String NONCESTR = WeChatProperties.get("nonceStr");
    public static final String USED = WeChatProperties.get("used");
}
