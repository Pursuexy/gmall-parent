package com.itcast.gmall.cart.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserCartKey implements Serializable {

	private boolean loginStatus;//用户登录状态
	private Long userId;//用户Id
	private String finalCartKey;//用户最终使用的CartKey
}
