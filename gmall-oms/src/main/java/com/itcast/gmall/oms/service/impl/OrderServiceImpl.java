package com.itcast.gmall.oms.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.RpcContext;
import com.itcast.gmall.cart.entity.CartItem;
import com.itcast.gmall.cart.service.CartService;
import com.itcast.gmall.constant.SystemCacheConstant;
import com.itcast.gmall.oms.entity.Order;
import com.itcast.gmall.oms.mapper.OrderMapper;
import com.itcast.gmall.oms.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcast.gmall.order.entity.OrderConfirm;
import com.itcast.gmall.ums.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author Pursuexy
 * @since 2021-02-26
 */
@Component
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

	@Reference
	private MemberService memberService;

	@Reference
	private CartService cartService;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	/**
	 * 订单确认
	 * @param id
	 * @return
	 */
	@Override
	public OrderConfirm orderConfirm(Long id) {
		//获取隐式传参的参数，不用修改接口便可实现所需要的参数
		String accessToken = RpcContext.getContext().getAttachment("accessToken");
		List<CartItem> cartItems=cartService.getCartItemsForOrder(accessToken);
		OrderConfirm orderConfirm = new OrderConfirm();
		orderConfirm.setMemberReceiveAddresses(memberService.getMemberAddress(id));//会员收货地址
		orderConfirm.setCoupons(null);//会员优惠券信息
		orderConfirm.setCartItems(cartItems);
		orderConfirm.setTransPrice(new BigDecimal("10"));

		String token = UUID.randomUUID().toString().replace("-", "");
		//给令牌设置过期时间，使用业务逻辑去尝试完成其他的验证
		String orderToken = token + "_" + System.currentTimeMillis() + "_" + 60 * 10;
		orderConfirm.setOrderToken(orderToken);//设置反重复令牌
		orderConfirm.setCouponPrice(null);
		cartItems.forEach((cartItem)->{
			Integer count = cartItem.getCount();
			orderConfirm.setCount(orderConfirm.getCount()+count);
			BigDecimal price = cartItem.getPrice();
			orderConfirm.setPriceTotalPrice(orderConfirm.getPriceTotalPrice().add(price));
		});
		orderConfirm.setTotalPrice(null);
		orderConfirm.setTotalPrice(orderConfirm.getPriceTotalPrice().add(orderConfirm.getTransPrice()).multiply(orderConfirm.getCouponPrice()));
		//存入缓存中的防重复令牌
		stringRedisTemplate.opsForSet().add(SystemCacheConstant.ORDER_UNIQUE_TOKEN, orderToken);
		return orderConfirm;
	}
}
