package com.wss.park.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.wss.park.pojo.Car;





public interface CarDAO {

	//入库
	public void carIn(Car car);
	//出库
	public void carOut(Car car);
    //根据状态码查询
	public Car findByStatus(Car car);
	//根据编号查询返回集合
	public List<Car> findByCarNo(@Param("car") Car car);
		
}
