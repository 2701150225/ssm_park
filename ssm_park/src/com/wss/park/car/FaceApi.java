package com.wss.park.car;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import com.alibaba.fastjson.JSONObject;
import com.wss.park.utils.MyX509TrustManager;
import com.wss.park.utils.SignUtil;


public class FaceApi {
	//车牌识别系统
	public static final String API_OCR_PLATEOCR = "https://api.ai.qq.com/fcgi-bin/ocr/ocr_plateocr";
	public String httpsRequest(String requestUrl, Map<String, String> params) {
		StringBuilder strb = new StringBuilder();
		for (Entry<String, String> entry : params.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (strb.length() == 0) {
				strb.append(key).append("=").append(SignUtil.urlEncodeUTF8(value));
			} else {
				strb.append("&").append(key).append("=").append(SignUtil.urlEncodeUTF8(value));
			}
		}
		// logger.info(strb.toString());
		String result = null;
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			conn.setSSLSocketFactory(ssf);
			//输出(发送数据)
			conn.setDoOutput(true);
			//输入（接收数据）
			conn.setDoInput(true);
			//不使用缓存
			conn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			conn.setRequestMethod("POST");

			// 当outputStr不为null时向输出流写数据
			if (strb.length() > 0) {
				OutputStream outputStream = conn.getOutputStream();
				// 注意编码格式
				outputStream.write(strb.toString().getBytes());
				// outputStream.write(strb.toString().getBytes("UTF-8"));
				outputStream.close();
			}

			// 从输入流读取返回内容
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			StringBuffer buffer = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}

			// 释放资源
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			conn.disconnect();
			result = buffer.toString();
		} catch (ConnectException ce) {
			System.out.println("连接超时：{}"+ce);
		} catch (Exception e) {
			System.out.println("https请求异常：{}"+ e);
		}
		return result;
	}

	//车牌识别
	public RootResp getPlateOcr(String image) {
		int timeStamp = (int) Math.ceil(System.currentTimeMillis() / 1000);
		Map<String, String> params = new HashMap<String, String>();
		params.put("app_id", String.valueOf(WeChatConfig.APPID));
		params.put("time_stamp", String.valueOf(timeStamp));
		params.put("nonce_str", WeChatConfig.NONCESTR);
		params.put("image", image);
		String sign = SignUtil.getSignName(WeChatConfig.APPKEY, params);
		params.put("sign", sign);
		String result = httpsRequest(API_OCR_PLATEOCR, params);
		System.out.println(result);
		RootResp resp = JSONObject.parseObject(result, RootResp.class);
		return resp;
	}
}
