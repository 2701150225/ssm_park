package com.wss.park.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;


import com.wss.park.pojo.CarInfo;


public interface CarInfoDAO {
	
    public void addCarInfo(CarInfo carInfo);

	public List<CarInfo> findAll(@Param("carNo") String carNo);

	public CarInfo findCarbyCarId(@Param("carNo") String carNo);

	public void UpdateCarInfo(CarInfo carInfo);
	
}
