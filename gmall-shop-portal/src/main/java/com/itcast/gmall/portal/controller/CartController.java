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
	                              @RequestParam(value = "num",defaultValue = "1") Integer num,
	                              @RequestParam(value = "accessToken",required = false) String accessToken,
	                              @RequestParam(value = "cartKey",required = false) String cartKey) throws ExecutionException, InterruptedException {
		CartResponse cartResponse=cartService.addToCart(skuId, num,accessToken, cartKey);
		return new CommonResult().success(cartResponse);
	}

	/**
	 * 修改购物项数量
	 * @param skuId
	 * @param num
	 * @param accessToken
	 * @param cartKey
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@PostMapping("/add")
	public CommonResult updateCartItemNum(@RequestParam("skuId") Long skuId,
	                              @RequestParam(value = "num",defaultValue = "1") Integer num,
	                              @RequestParam(value = "accessToken",required = false) String accessToken,
	                              @RequestParam(value = "cartKey",required = false) String cartKey) throws ExecutionException, InterruptedException {
		CartResponse cartResponse=cartService.updateCartItemNum(skuId, num,accessToken, cartKey);
		return new CommonResult().success(cartResponse);
	}

	/**
	 * 查看购物车清单
	 * @param accessToken
	 * @param cartKey
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@PostMapping("/cartList")
	public CommonResult getCartList(@RequestParam(value = "accessToken",required = false) String accessToken,
	                                @RequestParam(value = "cartKey",required = false) String cartKey) throws ExecutionException, InterruptedException {
		CartResponse cartResponse=cartService.getCartList(accessToken, cartKey);
		return new CommonResult().success(cartResponse);
	}
}
