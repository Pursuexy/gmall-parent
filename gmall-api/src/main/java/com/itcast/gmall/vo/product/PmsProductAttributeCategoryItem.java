package com.itcast.gmall.vo.product;

import com.itcast.gmall.pms.entity.ProductAttribute;
import com.itcast.gmall.pms.entity.ProductAttributeCategory;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 包含有分类下属性的vo
 */
@Data
public class PmsProductAttributeCategoryItem extends ProductAttributeCategory implements Serializable {
    private List<ProductAttribute> productAttributeList;
}
