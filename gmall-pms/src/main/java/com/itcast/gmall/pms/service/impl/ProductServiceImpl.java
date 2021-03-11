package com.itcast.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itcast.gmall.pms.entity.Product;
import com.itcast.gmall.pms.mapper.ProductMapper;
import com.itcast.gmall.pms.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcast.gmall.utils.PageInfoVo;
import com.itcast.gmall.vo.product.PmsProductQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Component
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

	@Autowired
	private ProductMapper productMapper;
	/**
	 * 根据复杂查询条件返回分页数据
	 * @param productQueryParam
	 * @return
	 */
	@Override
	public PageInfoVo productPageInfo(PmsProductQueryParam productQueryParam) {
		IPage<Product> page = productMapper.selectPage(new Page<Product>(productQueryParam.getPageNum(), productQueryParam.getPageSize()), null);
		PageInfoVo pageInfoVo = new PageInfoVo(page.getTotal(),page.getPages(),productQueryParam.getPageSize(),page.getRecords(),page.getCurrent());
		return pageInfoVo;
	}
}
