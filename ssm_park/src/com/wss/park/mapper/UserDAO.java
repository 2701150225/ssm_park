package com.wss.park.mapper;

import com.wss.park.pojo.User;

public interface UserDAO {

	//注册
	public void reg(User user);
	//登录
	public User login(User user);
	//按用户id查找
	public User findById(User user);

}
