package com.itcast.gmall.sso.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class LoginController {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@GetMapping("/login")
	public String login(@RequestParam(value = "redirect_url",required = false)
			            String redirect_url,
	                    @CookieValue(value = "sso_user",required = false)
			            String ssoUserCookie,
                        HttpServletResponse response,
                        Model model
	                    ) throws IOException {
		//判断是否登录过
		if (StringUtils.isEmpty(ssoUserCookie)) {
			//没登录过
			model.addAttribute("redirect_url", redirect_url);
			return "login";
		}else {
			//登陆过,返回原来登录页面
			String url = redirect_url + "?sso_user=" + ssoUserCookie;
			response.sendRedirect(url);
			return null;
		}
	}

	@PostMapping("/doLogin")
	public void doLogin(String username, String password,HttpServletResponse response,Model model) throws IOException {
		//模拟用户登录
		Map<String, Object> map = new HashMap<>();
		map.put("username", username);
		map.put("email", username + "@qq.com");

		//redis存储相关数据
		String token = UUID.randomUUID().toString().replace("-", "");
		stringRedisTemplate.opsForValue().set(token, JSON.toJSONString(map));

		//登陆成功，第一实现把用户信息写入token，第二实现调回原来链接的地址页面
		Cookie sso_user = new Cookie("sso_user", token);
		response.addCookie(sso_user);
		String redirect_url = (String) model.getAttribute("redirect_url");
		response.sendRedirect(redirect_url+"?sso_user="+sso_user);

	}
}
