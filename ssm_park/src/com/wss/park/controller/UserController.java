package com.wss.park.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wss.park.pojo.User;
import com.wss.park.service.UserService;


@Controller
@RequestMapping("/user")
public class UserController {
	@Resource
	UserService userService;
	//首页
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		return "login";
	}
	//登录
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(HttpServletRequest request, Model model) {
		String msg = "";
		HttpSession session = request.getSession();
		if (session.getAttribute("rand") == null) {
			return "login";
		}
		String loginName = request.getParameter("loginName");
		String password = request.getParameter("password");
		String randCode = request.getParameter("randCode");

		String rand = session.getAttribute("rand").toString();
		rand = rand.toUpperCase();
		randCode = randCode.toUpperCase();
		if (!rand.equals(randCode)) {
			msg = "验证码错误";
			model.addAttribute("msg", msg);
			return "login";
		}
		User user = new User();
		user.setUserId(loginName);
		user.setPassword(password);
		user = userService.login(user);
		if (user == null) {
			msg = "用户名或密码错误";
			model.addAttribute("msg", msg);
			return "login";
		}
		
		return "success";
		
	}

	//注册
	@RequestMapping(value="/reg",method=RequestMethod.GET)
	public String reg() throws Exception{
		return "regUser";
	}
	//注册
	@RequestMapping(value="/reg",method=RequestMethod.POST)
	@ResponseBody
	public Map reg(User user) throws Exception{
		user.setStatus(1);
		Map<String,String> map = new HashMap<String,String>();
		if(userService.findById(user)){
			userService.reg(user);
			map.put("code","0");
			map.put("msg", "恭喜您注册成功，登录名:"+user.getUserId()+",密码："+user.getPassword()+",请重新登录");
		}else{
			map.put("code","1");
			map.put("msg", "登录名称已经存在");
		}
		return map;
	}

	
}
