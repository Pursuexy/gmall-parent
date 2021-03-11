package com.itcast.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itcast.gmall.pms.entity.Brand;
import com.itcast.gmall.pms.mapper.BrandMapper;
import com.itcast.gmall.pms.service.BrandService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcast.gmall.utils.PageInfoVo;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 品牌表 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Service
@Component
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {

	@Override
	public PageInfoVo brandPageInfo(String keyword, Integer pageNum, Integer pageSize) {
		return null;
	}
}
