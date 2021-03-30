package com.itcast.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.itcast.gmall.cart.component.MemberComponent;
import com.itcast.gmall.cart.entity.Cart;
import com.itcast.gmall.cart.entity.CartConstant;
import com.itcast.gmall.cart.entity.CartItem;
import com.itcast.gmall.cart.entity.UserCartKey;
import com.itcast.gmall.cart.service.CartService;
import com.itcast.gmall.pms.entity.Product;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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
	public CartResponse addToCart(Long skuId,Integer num, String accessToken, String cartKey) throws ExecutionException, InterruptedException {

		Member member = memberComponent.getMemberByAccessToken(accessToken);
		if (member != null && !StringUtils.isEmpty(cartKey)) {
			//合并离线购物车商品信息
			mergeCart(cartKey, member.getId());
		}

		//String finalCartKey = "";
		// if (member != null) {
		//1.用户登录，购物车用在线购物车，cart：user：skuId
		// 	finalCartKey = CartConstant.USER_CART_KEY_PREFIX + member.getId();

		//添加商品到购物车
		// 	CartItem cartItem = addCartItemToCart(skuId,num, finalCartKey);
		// 	CartResponse cartResponse = new CartResponse();
		// 	cartResponse.setCartItem(cartItem);
		// 	return cartResponse;
		// }

		// if (StringUtils.isEmpty(cartKey)) {
		//2.没登录，使用浏览器临时购物车，cart：temp：cartKey
		// 	finalCartKey = CartConstant.TEMP_CART_KEY_PREFIX + cartKey;

		//添加商品到购物车
		// 	CartItem cartItem = addCartItemToCart(skuId,num,  finalCartKey);
		// 	CartResponse cartResponse = new CartResponse();
		// 	cartResponse.setCartItem(cartItem);
		// 	return cartResponse;
		// }

		//3.参数没完整提供，自动分配临时购物车
		// String newCartKey = UUID.randomUUID().toString().replace("-","");
		// finalCartKey = CartConstant.TEMP_CART_KEY_PREFIX + newCartKey;

	    //添加商品到购物车
		// CartItem cartItem = addCartItemToCart(skuId, num, finalCartKey);
		// CartResponse cartResponse = new CartResponse();
		// cartResponse.setCartItem(cartItem);
		// cartResponse.setCartKey(newCartKey);
		// return cartResponse;

		//针对上面的优化代码
		UserCartKey userCartKey = memberComponent.getCartKey(cartKey, accessToken);
		String finalCartKey = userCartKey.getFinalCartKey();
		//添加商品到购物车
		CartItem cartItem = addCartItemToCart(skuId,num, finalCartKey);
		CartResponse cartResponse = new CartResponse();
		cartResponse.setCartItem(cartItem);
		cartResponse.setCartKey(userCartKey.getFinalCartKey());
		return cartResponse;
	}

	/**
	 * 修改购物项数量
	 * @param skuId
	 * @param num
	 * @param accessToken
	 * @param cartKey
	 * @return
	 */
	@Override
	public CartResponse updateCartItemNum(Long skuId, Integer num, String accessToken, String cartKey) {
		//判断是哪个cartKey
		UserCartKey userCartKey = memberComponent.getCartKey(cartKey, accessToken);
		String finalCartKey = userCartKey.getFinalCartKey();
		RMap<String, String> map = RedissonClient.getMap(finalCartKey);
		String json = map.get(skuId.toString());
		CartItem cartItem = JSON.parseObject(json, CartItem.class);
		cartItem.setCount(num);
		String jsonString = JSON.toJSONString(json);
		map.put(skuId.toString(), jsonString);
		CartResponse cartResponse = new CartResponse();
		cartResponse.setCartItem(cartItem);
		return cartResponse;
	}

	/**
	 * 查看购物车清单
	 * @param accessToken
	 * @param cartKey
	 * @return
	 */
	@Override
	public CartResponse getCartList(String accessToken, String cartKey) {
		UserCartKey userCartKey = memberComponent.getCartKey(cartKey, accessToken);
		//查询用户的购物车时候是否需要判断购物车是否需要进行合并
		if (userCartKey.isLoginStatus()) {
			//用户登录了，必须进行合并购物车
			mergeCart(cartKey,userCartKey.getUserId());
		}
		//查询购物车数据
		String finalCartKey = userCartKey.getFinalCartKey();
		//自动设定时间
		stringRedisTemplate.expire(finalCartKey, 30L, TimeUnit.DAYS);
		RMap<String, String> map = RedissonClient.getMap(finalCartKey);
		Cart cart = new Cart();
		List<CartItem> cartItems = new ArrayList<>();
		CartResponse cartResponse = new CartResponse();
		if (map != null && !map.isEmpty()) {
			//购物项的json数据
			map.forEach((key, value) -> {
				CartItem cartItem = JSON.parseObject(value, CartItem.class);
				cartItems.add(cartItem);
			});
			cart.setCartItems(cartItems);
		}else {
			//用户没有购物车，创建一个空的
			cartResponse.setCartKey(userCartKey.getFinalCartKey());
		}
		cartResponse.setCart(cart);
		return cartResponse;
	}

	/**
	 * 合并离线购物车商品信息
	 * @param cartKey
	 * @param id
	 */
	private void mergeCart(String cartKey, Long id) {
		String oldCartKey = CartConstant.TEMP_CART_KEY_PREFIX + cartKey;
		String newCartKey = CartConstant.USER_CART_KEY_PREFIX + id.toString();

		//获取离线购物车map
		RMap<String, String> oldCartMap = RedissonClient.getMap(oldCartKey);
		if (oldCartMap != null && !oldCartMap.isEmpty()) {
			//map不为空且map有数据
			//skuId
			//购物项的json数据
			oldCartMap.forEach((key, value) -> {
				CartItem cartItem = JSON.parseObject(value, CartItem.class);
				try {
					addCartItemToCart(Long.parseLong(key), cartItem.getCount(), newCartKey);
					//移除老购物车的key,防止太频繁，最后一次性清空
					//oldCartMap.remove(oldCartKey);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			//移除老购物车的key,防止太频繁，最后一次性清空
			oldCartMap.clear();
		}
	}

	/**
	 * 添加商品到购物车
	 * @param skuId
	 * @param num
	 * @param finalCartKey
	 * @return
	 */
	private CartItem addCartItemToCart(Long skuId,Integer num, String finalCartKey) throws ExecutionException, InterruptedException {
		//根据skuId查询商品sku信息
		CartItem newCartItem = new CartItem();
		CompletableFuture<Void> skuFuture = CompletableFuture.supplyAsync(() -> {
			return skuStockService.getById(skuId);//查询时间长且慢。考虑未来任务
		}).thenAcceptAsync((skuStock) -> {
			Long productId = skuStock.getProductId();
			Product product = productService.getById(productId);
			BeanUtils.copyProperties(skuStock, newCartItem);
			newCartItem.setName(product.getName());
			newCartItem.setSkuId(productId);
			newCartItem.setCheckStatus(true);
			newCartItem.setCount(num);
		});

		/*
		 * 购物车集合 k[skuId]:string,v[购物项]：string，购物项集合（json)
		 * 其中含有k[checked]:v[选中购物项的数组]
		 */
		RMap<String, String> map = RedissonClient.getMap(finalCartKey);
		//检查是否为空，防止数据叠加
		String itemJson = map.get(skuId.toString());
		//在线阻塞等待结果，拿到skuFuture的异步结果
		skuFuture.get();
		if (!StringUtils.isEmpty(itemJson)) {
			//叠加购物项信息；购物车获取老的item的数量，商品信息用新查询到的数据信息
			CartItem cartItem = JSON.parseObject(itemJson, CartItem.class);
			Integer cartItemCount = cartItem.getCount();
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
