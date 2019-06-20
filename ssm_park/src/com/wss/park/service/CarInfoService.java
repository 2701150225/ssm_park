package com.wss.park.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wss.park.mapper.CarInfoDAO;
import com.wss.park.pojo.CarInfo;

@Service
public class CarInfoService {
   
	@Resource
	private CarInfoDAO carInfoDAO;

	

	public CarInfo findCarbyCarId(String carNo){
	return carInfoDAO.findCarbyCarId(carNo);
	}
}

