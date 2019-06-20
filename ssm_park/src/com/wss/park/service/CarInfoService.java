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

	@Transactional
	public List<CarInfo> findCar(String carNo) {
		return carInfoDAO.findAll(carNo);
	}

	@Transactional
	public CarInfo findCarbyCarId(String carNo) {
		return carInfoDAO.findCarbyCarId(carNo);
	}
	
	@Transactional
	public void carAdd(CarInfo carInfo){
		carInfoDAO.addCarInfo(carInfo);
	}

	@Transactional
	public void carUpdate(CarInfo carInfo) {
        carInfoDAO.UpdateCarInfo(carInfo);		
	}
}
