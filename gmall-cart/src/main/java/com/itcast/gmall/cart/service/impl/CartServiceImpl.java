package com.itcast.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.itcast.gmall.cart.component.MemberComponent;
import com.itcast.gmall.cart.entity.CartConstant;
import com.itcast.gmall.cart.entity.CartItem;
import com.itcast.gmall.cart.service.CartService;
import com.itcast.gmall.pms.entity.Product;
import com.itcast.gmall.pms.entity.SkuStock;
import com.itcast.gmall.pms.service.ProductService;
import com.itcast.gmall.pms.service.SkuStockService;
import com.itcast.gmall.ums.entity.Member;
import com.itcast.gmall.vo.cart.CartResponse;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Component
public class CartServiceImpl implements CartService {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private MemberComponent memberComponent;

	@Autowired
	private RedissonClient RedissonClient;

	@Reference
	private SkuStockService skuStockService;

	@Reference
	private ProductService productService;

	/**
	 * 添加商品到购物车
	 * @param skuId
	 * @param accessToken
	 * @param cartKey
	 * @return
	 */
	@Override
	public CartResponse addToCart(Long skuId, String accessToken, String cartKey) throws ExecutionException, InterruptedException {

		Member member = memberComponent.getMemberByAccessToken(accessToken);
		if (member != null && !StringUtils.isEmpty(cartKey)) {
			//合并离线购物车商品信息
			mergeCart(skuId, member.getId());
		}
		String finalCartKey = "";
		if (member != null) {
			//1.用户登录，购物车用在线购物车，cart：user：skuId
			finalCartKey = CartConstant.USER_CART_KEY_PREFIX + member.getId();

			//添加商品到购物车
			CartItem cartItem = addCartItemToCart(skuId, finalCartKey);
			CartResponse cartResponse = new CartResponse();
			cartResponse.setCartItem(cartItem);
			return cartResponse;
		}

		if (StringUtils.isEmpty(cartKey)) {
			//2.没登录，使用浏览器临时购物车，cart：temp：cartKey
			finalCartKey = CartConstant.TEMP_CART_KEY_PREFIX + cartKey;

			//添加商品到购物车
			CartItem cartItem = addCartItemToCart(skuId, finalCartKey);
			CartResponse cartResponse = new CartResponse();
			cartResponse.setCartItem(cartItem);
			return cartResponse;
		}

		//3.参数没完整提供，自动分配临时购物车
		String newCartKey = UUID.randomUUID().toString().replace("-","");
		finalCartKey = CartConstant.TEMP_CART_KEY_PREFIX + newCartKey;

		//添加商品到购物车
		CartItem cartItem = addCartItemToCart(skuId, finalCartKey);
		CartResponse cartResponse = new CartResponse();
		cartResponse.setCartItem(cartItem);
		cartResponse.setCartKey(newCartKey);
		return cartResponse;
	}

	/**
	 * 合并离线购物车商品信息
	 * @param skuId
	 * @param id
	 */
	private void mergeCart(Long skuId, Long id) {
	}

	/**
	 * 添加商品到购物车
	 * @param skuId
	 * @param finalCartKey
	 * @return
	 */
	private CartItem addCartItemToCart(Long skuId, String finalCartKey) throws ExecutionException, InterruptedException {
		//根据skuId查询商品sku信息
		CartItem newCartItem = new CartItem();
		CompletableFuture<Void> skuFuture = CompletableFuture.supplyAsync(() -> {
			SkuStock skuStock = skuStockService.getById(skuId);//查询时间长且慢。考虑未来任务
			return skuStock;
		}).thenAcceptAsync((skuStock) -> {
			Long productId = skuStock.getProductId();
			Product product = productService.getById(productId);
			BeanUtils.copyProperties(skuStock, newCartItem);
			newCartItem.setName(product.getName());
			newCartItem.setSkuId(productId);
			newCartItem.setCheckStatus(true);
			newCartItem.setCount(1);
		});

		/*
		 * 购物车集合 k[skuId]:string,v[购物项]：string，购物项集合（json)
		 * 其中含有k[checked]:v[选中购物项的数组]
		 */
		RMap<String, String> map = RedissonClient.getMap(finalCartKey);
		//检查是否为空，防止数据叠加
		String itemJson = map.get(skuId.toString());
		if (!StringUtils.isEmpty(itemJson)) {
			//叠加购物项信息；购物车获取老的item的数量，商品信息用新查询到的数据信息
			CartItem cartItem = JSON.parseObject(itemJson, CartItem.class);
			Integer cartItemCount = cartItem.getCount();
			//在线阻塞等待结果，拿到skuFuture的异步结果
			skuFuture.get();
			newCartItem.setCount(cartItemCount+newCartItem.getCount());
			//重新存回map
			String newCartItemJson = JSON.toJSONString(newCartItem);
			map.put(skuId.toString(), newCartItemJson);
		}else {
			//新增购物项
			String newCartItemJson = JSON.toJSONString(newCartItem);
			map.put(skuId.toString(), newCartItemJson);
		}
		return newCartItem;
	}
}
