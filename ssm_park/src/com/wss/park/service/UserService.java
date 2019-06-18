package com.wss.park.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wss.park.mapper.UserDAO;
import com.wss.park.pojo.User;


//加入服务注解，表示这个类是一个服务层，操作数据库的类
@Service
public class UserService {
	//注入MyBatis的数据库操作类
	@Resource 
	UserDAO userDAO;


	//注册
	@Transactional
	public void reg(User user){
		userDAO.reg(user);
	}
	

	//登录
	@Transactional
	public User login(User user){
		return userDAO.login(user);
		
	}
	
	//根据id查找
	@Transactional
	public boolean findById(User user){
		User u = userDAO.findById(user);
		if(u==null){
			return true;
		}else{
			return false;
		}
		
	}
	
}
