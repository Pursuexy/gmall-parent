package com.itcast.gmall.portal.controller;

import com.itcast.gmall.cart.service.CartService;
import com.itcast.gmall.utils.CommonResult;
import com.itcast.gmall.vo.cart.CartResponse;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.ExecutionException;

/**
 * 购物车
 */
@RequestMapping("/cart")
@RestController
public class CartController {

	@Reference
	private CartService cartService;

	/**
	 * 添加商品到购物车
	 * @param skuId
	 * @param accessToken
	 * @param cartKey
	 * @return
	 */
	@PostMapping("/add")
	public CommonResult addToCart(@RequestParam("skuId") Long skuId,
	                              @RequestParam(value = "accessToken",required = false) String accessToken,
	                              @RequestParam(value = "cartKey",required = false) String cartKey) throws ExecutionException, InterruptedException {
		CartResponse cartResponse=cartService.addToCart(skuId, accessToken, cartKey);
		return new CommonResult().success(cartResponse);
	}
}
