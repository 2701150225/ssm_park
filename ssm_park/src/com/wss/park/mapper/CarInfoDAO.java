package com.wss.park.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.wss.park.pojo.CarInfo;


public interface CarInfoDAO {


    
	public CarInfo findCarbyCarId(@Param("carNo") String carNo);
	
}
