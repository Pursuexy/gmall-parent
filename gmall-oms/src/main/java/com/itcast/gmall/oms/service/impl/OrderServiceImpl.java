package com.itcast.gmall.oms.service.impl;

import com.itcast.gmall.oms.entity.Order;
import com.itcast.gmall.oms.mapper.OrderMapper;
import com.itcast.gmall.oms.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

}
