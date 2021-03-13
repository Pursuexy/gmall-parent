package com.itcast.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itcast.gmall.pms.entity.*;
import com.itcast.gmall.pms.mapper.*;
import com.itcast.gmall.pms.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcast.gmall.utils.PageInfoVo;
import com.itcast.gmall.vo.product.PmsProductParam;
import com.itcast.gmall.vo.product.PmsProductQueryParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Slf4j
@Component
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

	@Autowired
	private ProductMapper productMapper;

	@Autowired
	private ProductAttributeValueMapper productAttributeValueMapper;

	@Autowired
	private ProductFullReductionMapper productFullReductionMapper;

	@Autowired
	private ProductLadderMapper productLadderMapper;

	@Autowired
	private SkuStockMapper skuStockMapper;

	//当前线程共享数据productId
	private ThreadLocal<Long> threadLocal=new ThreadLocal<>();
	//ThreadLocal原理：
	//private Map<Thread, Long> map = new HashMap<>();
	//存入值：map.put(Thread.currentThread(), product.getId());
	//取出值：Long productId = map.get(Thread.currentThread());
	// 		System.out.println(productId);

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

	/**
	 * 保存商品信息
	 * @param productParam
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void saveProduct(PmsProductParam productParam) {
		//自身service调用自身方法的无法添加真正意义上的事务行为
		ProductServiceImpl currentProxy = (ProductServiceImpl) AopContext.currentProxy();
		//保存商品的基本信息
		currentProxy.saveBaseProductInfo(productParam);
		//保存商品的基本信息
		currentProxy.saveProductAttributeValue(productParam);
		//以下都可以try······catch····
		//保存商品的满减信息
		currentProxy.saveProductFullReduction(productParam);
		//保存商品的阶梯价格
		currentProxy.saveProductLadder(productParam);
		//保存商品的sku表
		currentProxy.saveSkuStock(productParam);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveSkuStock(PmsProductParam productParam) {
		//保存商品的sku表
		List<SkuStock> skuStockList = productParam.getSkuStockList();
		for (int i = 1; i <= skuStockList.size(); i++) {
			SkuStock skuStock = skuStockList.get(i-1);
			if (StringUtils.isEmpty(skuStock.getSkuCode())) {
				//skuCode的规则  skuId+productId+
				skuStock.setSkuCode(threadLocal.get() +"_"+ i);
			}
			skuStock.setProductId(threadLocal.get());
			skuStockMapper.insert(skuStock);
		}
		log.debug("当前线程·····{}····{}",Thread.currentThread().getId(),Thread.currentThread().getName());
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveProductLadder(PmsProductParam productParam) {
		//保存商品的阶梯价格
		List<ProductLadder> productLadderList = productParam.getProductLadderList();
		productLadderList.forEach((productLadder)->{
			productLadder.setProductId(threadLocal.get());
			productLadderMapper.insert(productLadder);
		});
		log.debug("当前线程·····{}····{}",Thread.currentThread().getId(),Thread.currentThread().getName());
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveProductFullReduction(PmsProductParam productParam) {
		//保存商品的满减信息
		List<ProductFullReduction> productFullReductionList = productParam.getProductFullReductionList();
		productFullReductionList.forEach((productFullReduction)->{
			productFullReduction.setProductId(threadLocal.get());
			productFullReductionMapper.insert(productFullReduction);
		});
		log.debug("当前线程·····{}····{}",Thread.currentThread().getId(),Thread.currentThread().getName());
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveProductAttributeValue(PmsProductParam productParam) {
		//保存商品属性列表值
		List<ProductAttributeValue> productAttributeValueList = productParam.getProductAttributeValueList();
		productAttributeValueList.forEach((productAttributeValue)->{
			productAttributeValue.setProductId(threadLocal.get());
			productAttributeValueMapper.insert(productAttributeValue);
		});
		log.debug("当前线程·····{}····{}",Thread.currentThread().getId(),Thread.currentThread().getName());
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@NotNull
	public void saveBaseProductInfo(PmsProductParam productParam) {
		//保存商品的基本信息
		Product product = new Product();
		BeanUtils.copyProperties(productParam,product);
		productMapper.insert(product);
		threadLocal.set(product.getId());
		log.debug("当前线程·····{}····{}",Thread.currentThread().getId(),Thread.currentThread().getName());
	}
}
