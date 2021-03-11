package com.itcast.gmall.pms.service.impl;

import com.itcast.gmall.pms.entity.Product;
import com.itcast.gmall.pms.mapper.ProductMapper;
import com.itcast.gmall.pms.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

}
