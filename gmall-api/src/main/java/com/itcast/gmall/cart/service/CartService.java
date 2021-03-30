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
	CartResponse addToCart(Long skuId,Integer num, String accessToken, String cartKey) throws ExecutionException, InterruptedException;

	/**
	 * 修改购物项数量
	 * @param skuId
	 * @param num
	 * @param accessToken
	 * @param cartKey
	 * @return
	 */
	CartResponse updateCartItemNum(Long skuId, Integer num, String accessToken, String cartKey);

	/**
	 * 查看购物车清单
	 * @param accessToken
	 * @param cartKey
	 * @return
	 */
	CartResponse getCartList(String accessToken, String cartKey);
}
