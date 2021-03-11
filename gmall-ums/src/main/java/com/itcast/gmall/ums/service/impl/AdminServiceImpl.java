package com.itcast.gmall.ums.service.impl;

import com.itcast.gmall.ums.entity.Admin;
import com.itcast.gmall.ums.mapper.AdminMapper;
import com.itcast.gmall.ums.service.AdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 后台用户表 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

}
