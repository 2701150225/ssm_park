package com.wss.park.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wss.park.car.FaceApi;
import com.wss.park.car.RootResp;
import com.wss.park.pojo.Car;
import com.wss.park.pojo.CarInfo;
import com.wss.park.pojo.Finance;
import com.wss.park.pojo.SysSet;
import com.wss.park.pojo.User;
import com.wss.park.service.CarInfoService;
import com.wss.park.service.CarService;
import com.wss.park.utils.Base64Util;

@Controller
@RequestMapping("/car")
public class CarController {

	@Resource
	private CarInfoService carInfoService;

	@Resource
	private CarService carService;
	
	@RequestMapping("/paizhao")
	@ResponseBody
	public Map<String, Object> paizhao(HttpServletRequest request,String imageData,String extName) throws Exception{
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
				
				//车辆信息
				CarInfo carInfo = carInfoService.findCarbyCarId(carNo);

				Car car = new Car();
				car.setCardNo(carNo);
				car.setStatus(0);
				car.setUserName(user.getUserName());
				//查未缴费的停车信息
				Car c = carService.findByStatus(car);
				String remark= "临时停车";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if(carInfo==null){//临时停车
					car.setFinanceType(2);//2临时
					if(c==null){//入库
						car.setCarFee(0);
						car.setInPic(inPic);
						car.setInTime(sdf.format(new Date()));
						car.setRemark(remark);
						car.setStatus(0);//0未收费
						carService.carIn(car);
						json.put("msg","临时车【"+carNo+"】：入场" );
					}else{//出库
						float carFee = 0;
						SysSet sysSet = (SysSet)request.getServletContext().getAttribute("sysSet");
						carFee = c.getMinutes()-sysSet.getMianfeiTime();
						if(carFee<=0){
							car.setCarFee(0);
							carFee=0;
						}else{
							carFee = (float)Math.ceil(carFee/sysSet.getShoufeiTime())*sysSet.getShoufeiMoney();
							car.setCarFee(carFee);
						}
						car.setStatus(1);//1已收费
						car.setOutPic(inPic);
						car.setOutTime(sdf.format(new Date()));
						car.setRemark(remark);
						car.setCarId(c.getCarId());
						carService.carOut(car);
						if(carFee>0){
							//收费
							Finance finance = new Finance();
							finance.setCarNo(carNo);
							finance.setFinanceType(2);//临时车收费
							finance.setOprTime(new Date());
							finance.setUserName(user.getUserName());
							finance.setRemark("临时车【"+carNo+"】：出场，请收费:"+carFee+"元" );
							finance.setTotalMoney(carFee);
					
						}
						json.put("msg","临时车【"+carNo+"】：出场，请收费:"+carFee+"元" );
					}
				}else{//包月停车
					remark="包月停车";
					car.setFinanceType(1);//1包月
					if(c==null){//入库
						car.setCarFee(0);
						car.setInPic(inPic);
						car.setInTime(sdf.format(new Date()));
						car.setRemark(remark);
						car.setStatus(0);//0未收费
						carService.carIn(car);
						json.put("msg","包月车【"+carNo+"】：入场" );
					}else{//出库
						car.setCarFee(0);
						car.setStatus(2);//2包月
						car.setOutPic(inPic);
						car.setOutTime(sdf.format(new Date()));
						car.setRemark(remark);
						car.setCarId(c.getCarId());
						carService.carOut(car);
						if(carInfo.getDiffDate()>0){
							json.put("msg","包月车【"+carNo+"】：出场，还剩"+carInfo.getDiffDate()+"天" );
						}else{
							json.put("msg","包月车【"+carNo+"】：出场，请及时缴费" );
						}
					}
				}
				
			}else{
				json.put("errorCode", resp.getRet());
				json.put("msg", "车牌识别失败");
			}
		}
		return json;
	}
	
	@RequestMapping("/carInfo")
	public String carInfo(String carNo,ModelMap model) throws Exception{
		if(carNo==null){
			carNo="";
		}
		List<CarInfo> carInfoList = carInfoService.findCar(carNo);
		model.addAttribute("carInfoList", carInfoList);
		return "car";
	}
	@RequestMapping(value="/carAdd",method=RequestMethod.GET)
	public String carAdd(ModelMap model) throws Exception{
		CarInfo carInfo = new CarInfo();
		model.addAttribute("carInfo",carInfo);
		return "carInfo";
	}
	//保存车辆信息
		@RequestMapping(value="/carAdd",method=RequestMethod.POST)
		public String carAdd(CarInfo carInfo,ModelMap model) {
			try{
				if(carInfo.getCarInfoId()==0){
					carInfoService.carAdd(carInfo);
				}else{
					carInfoService.carUpdate(carInfo);
				}
			}catch(Exception e){
				model.addAttribute("carInfo", carInfo);
				model.addAttribute("msg", "该车牌对应的车辆信息已经存在");
				return "carInfo";
			}
			//车辆信息
			List<CarInfo> carInfoList = carInfoService.findCar("");
			model.addAttribute("carInfoList", carInfoList);
			return "car";
		}
	
		@RequestMapping("/stopCar")

		public String stopCar(Car car,Model model)
		{
			if(car==null) {
				car=new Car();
			}
			List<Car> list = carService.findByCarNo(car);
			model.addAttribute("carlist", list);
			return "stop";
		}
	
}
