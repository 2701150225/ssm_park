package com.wss.park.mapper;

import java.util.List;

import com.wss.park.pojo.Car;





public interface CarDAO {

	//入库
	public void carIn(Car car);
	//出库
	public void carOut(Car car);

	public Car findByStatus(Car car);
		
}
