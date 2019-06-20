package com.wss.park.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wss.park.mapper.CarDAO;
import com.wss.park.pojo.Car;

@Service
public class CarService {

	@Resource
	private CarDAO carDAO;
	
	//入库
	@Transactional
	public void carIn(Car car){
		carDAO.carIn(car);
	}
	//出库
	@Transactional
	public void carOut(Car car){
		carDAO.carOut(car);
	}
	
	//按状态查找
	public Car findByStatus(Car car){
		return carDAO.findByStatus(car);
	}
}
