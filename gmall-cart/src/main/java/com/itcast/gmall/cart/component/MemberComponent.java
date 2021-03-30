package com.itcast.gmall.cart.component;

import com.alibaba.fastjson.JSON;
import com.itcast.gmall.constant.SystemCacheConstant;
import com.itcast.gmall.ums.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class MemberComponent {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * 根据AccessToken获取
	 * @param accessToken
	 * @return
	 */
	public Member getMemberByAccessToken(String accessToken) {
		String userJson = stringRedisTemplate.opsForValue().get(SystemCacheConstant.LOGIN_MEMBER + accessToken);
		Member member = JSON.parseObject(userJson, Member.class);
		return member;
	}

}
