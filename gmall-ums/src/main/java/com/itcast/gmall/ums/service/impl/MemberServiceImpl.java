package com.itcast.gmall.ums.service.impl;

import com.itcast.gmall.ums.entity.Member;
import com.itcast.gmall.ums.mapper.MemberMapper;
import com.itcast.gmall.ums.service.MemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

}
