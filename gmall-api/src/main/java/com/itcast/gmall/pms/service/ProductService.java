package com.itcast.gmall.pms.service;

import com.itcast.gmall.pms.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itcast.gmall.utils.PageInfoVo;
import com.itcast.gmall.vo.product.PmsProductParam;
import com.itcast.gmall.vo.product.PmsProductQueryParam;

/**
 * <p>
 * 商品信息 服务类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
public interface ProductService extends IService<Product> {

	/**
	 * 根据复杂查询条件返回分页数据
	 * @param productQueryParam
	 * @return
	 */
	PageInfoVo productPageInfo(PmsProductQueryParam productQueryParam);

	/**
	 * 保存商品
	 * @param productParam
	 */
	void saveProduct(PmsProductParam productParam);
}
