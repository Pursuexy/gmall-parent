package com.itcast.gmall.ums.service;

import com.itcast.gmall.ums.entity.Member;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
public interface MemberService extends IService<Member> {

	/**
	 * 登录验证，并实现多端口单点登录
	 * @param username
	 * @param password
	 * @return
	 */
	Member login(String username, String password);
}
