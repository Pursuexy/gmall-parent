package com.itcast.gmall.cart.service;

import com.itcast.gmall.vo.cart.CartResponse;

import java.util.concurrent.ExecutionException;

/**
 * 购物车服务
 */
public interface CartService {

	/**
	 * 添加商品到购物车
	 * @param skuId
	 * @param accessToken
	 * @param cartKey
	 * @return
	 */
	CartResponse addToCart(Long skuId, String accessToken, String cartKey) throws ExecutionException, InterruptedException;
}
