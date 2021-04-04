package com.itcast.gmall.ums.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itcast.gmall.ums.entity.Member;
import com.itcast.gmall.ums.mapper.MemberMapper;
import com.itcast.gmall.ums.service.MemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Service
@Component
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

	@Autowired
	private MemberMapper memberMapper;

	/**
	 * 登录验证，并实现多端口单点登录
	 * @param username
	 * @param password
	 * @return
	 */
	@Override
	public Member login(String username, String password) {
		String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
		QueryWrapper<Member> queryWrapper = new QueryWrapper<Member>().eq("username", username).eq("password", md5Password);
		Member member = memberMapper.selectOne(queryWrapper);
		return member;
	}
}
