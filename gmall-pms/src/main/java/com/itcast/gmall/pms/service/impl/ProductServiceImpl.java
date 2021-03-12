package com.itcast.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import org.springframework.util.StringUtils;

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
		QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
		if (productQueryParam.getBrandId() != null) {
			queryWrapper.eq("brand_id", productQueryParam.getBrandId());
		}
		if (!StringUtils.isEmpty(productQueryParam.getKeyword())) {
			queryWrapper.like("name", productQueryParam.getKeyword());
		}
		if (productQueryParam.getProductCategoryId() != null) {
			queryWrapper.eq("product_category_id", productQueryParam.getProductCategoryId());
		}
		if (!StringUtils.isEmpty(productQueryParam.getProductSn())) {
			queryWrapper.like("product_sn", productQueryParam.getProductSn());
		}
		if (productQueryParam.getPublishStatus() != null) {
			queryWrapper.eq("product_status", productQueryParam.getPublishStatus());
		}
		if (productQueryParam.getVerifyStatus() != null) {
			queryWrapper.eq("verify_status", productQueryParam.getVerifyStatus());
		}
		IPage<Product> page = productMapper.selectPage(new Page<Product>(productQueryParam.getPageNum(), productQueryParam.getPageSize()), queryWrapper);
		PageInfoVo pageInfoVo = new PageInfoVo(page.getTotal(),page.getPages(),productQueryParam.getPageSize(),page.getRecords(),page.getCurrent());
		return pageInfoVo;
	}
}
