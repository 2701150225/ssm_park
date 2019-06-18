package com.wss.park.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wss.park.car.FaceApi;
import com.wss.park.car.RootResp;
import com.wss.park.pojo.User;
import com.wss.park.utils.Base64Util;

@Controller
@RequestMapping("/car")
public class CarController {

	
	@RequestMapping("/paizhao")
	@ResponseBody
	public Map<String,Object> paizhao(HttpServletRequest request,String imageData,String extName ) throws Exception{
		User user = (User)request.getSession().getAttribute("userinfo");
		FaceApi faceApi = new FaceApi();
		RootResp resp = faceApi.getPlateOcr(imageData);
		Map<String,Object> json = new HashMap<String, Object>();

		
		if(resp!=null){
			//ret=0表示识别成功
			if(resp.getRet()==0) {
				JSONObject data = (JSONObject )resp.getData();
				json.put("errorCode", 0);
				JSONArray arr = data.getJSONArray("item_list");
				System.out.println(arr);
				JSONObject item = (JSONObject)arr.get(0);
				String carNo = item.getString("itemstring");
				System.out.println("carNo="+carNo);
				String savePath = request.getSession().getServletContext().getRealPath("/attached/face/")+"/";
				String inPic = UUID.randomUUID()+"."+extName;
				savePath += inPic;
				System.out.println("savePath="+savePath);
				Base64Util.decoderBase64File(imageData, savePath);
			    json.put("msg", carNo); 
			}
				else{
					
					json.put("msg", "车牌识别失败");
				
			}
		}
			

		return json;
}
}
