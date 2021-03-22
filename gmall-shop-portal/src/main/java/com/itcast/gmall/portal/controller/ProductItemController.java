package com.itcast.gmall.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itcast.gmall.es.product.EsProduct;
import com.itcast.gmall.pms.service.ProductService;
import com.itcast.gmall.utils.CommonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductItemController {

	@Reference
	private ProductService productService;

	/**
	 * 查询商品信息详情
	 * @param id
	 * @return
	 */
	@GetMapping("/item/{id}.html")
	public CommonResult productInfo(@PathVariable("id") Long id) {

		EsProduct esProduct = productService.productAllInfo(id);
		return new CommonResult().success(esProduct);
	}

	/**
	 * 根据skuId查询商品信息
	 * @param id
	 * @return
	 */
	@GetMapping("/item/sku/{id}.html")
	public CommonResult productSkuInfo(@PathVariable("id") Long id) {
		EsProduct esProduct = productService.productSkuInfo(id);
		return new CommonResult().success(esProduct);
	}


}
