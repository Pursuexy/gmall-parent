package com.itcast.gmall.search.search.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itcast.gmall.constant.EsConstant;
import com.itcast.gmall.search.ProductSearchService;
import com.itcast.gmall.vo.search.SearchParam;
import com.itcast.gmall.vo.search.SearchResponse;
import freemarker.template.utility.Execute;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
@Service
public class ProductSearchServiceImpl implements ProductSearchService {

	@Autowired
	private JestClient jestClient;

	/**
	 * 检索商品
	 * @param searchParam
	 * @return
	 */
	@Override
	public SearchResponse searchProduct(SearchParam searchParam) {

		//1.构建检索条件
		String dsl = buildDsl(searchParam);
		Search build = new Search.Builder(dsl).addIndex(EsConstant.PRODUCT_ES_INDEX).addType(EsConstant.PRODUCT_INFO_ES_TYPE).build();
		//2.进行检索查询
		SearchResult execute = null;
		try {
			execute = jestClient.execute(build);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//3.将检索结果SearchResult封装成返回结果SearchResponse
		SearchResponse searchResponse = buildSearchResponse(execute);
		return searchResponse;
	}

	private SearchResponse buildSearchResponse(SearchResult execute) {
		return null;
	}

	private String buildDsl(SearchParam searchParam) {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		//1.查询
		//1.1检索
		String keyword = searchParam.getKeyword();
		if (!StringUtils.isEmpty(keyword)) {
			MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("skuProductInfos.skuTitle", keyword);
			NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("skuProductInfos", matchQueryBuilder, ScoreMode.None);
			boolQueryBuilder.must(nestedQueryBuilder);

		}

		//1.2过滤
		//1.2.1  按照三级分类的条件过滤
		String[] catelog3 = searchParam.getCatelog3();
		if (catelog3.length > 0 && catelog3 != null) {

			boolQueryBuilder.filter(QueryBuilders.termsQuery("productCategoryId", catelog3));
		}

		//1.2.2  按照品牌过滤
		String[] brand = searchParam.getBrand();
		if (brand.length > 0 && brand != null) {
			boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", brand));
		}

		//1.2.3  按照属性过滤
		String[] props = searchParam.getProps();
		if (props != null & props.length > 0) {
			for (String prop : props) {
				//2:4G-3G  2号属性的属性值为4G或者3G
				String[] split = prop.split(":");
				BoolQueryBuilder must = QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery("attrValueList.productAttributeId", split[0]))
						.must(QueryBuilders.termsQuery("attrValueList.value", split[1].split("-")));
				NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrValueList", must, ScoreMode.None);
				boolQueryBuilder.filter(nestedQueryBuilder);
			}
		}

		//1.2.4  按照价格区间过滤
		Integer priceFrom = searchParam.getPriceFrom();
		Integer priceTo = searchParam.getPriceTo();
		if (priceFrom != null || priceTo != null) {
			RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");
			if (priceFrom != null) {
				rangeQueryBuilder.from(priceFrom);
			}
			if (priceTo != null) {
				rangeQueryBuilder.to(priceTo);
			}
			boolQueryBuilder.filter(rangeQueryBuilder);
		}
		sourceBuilder.query(boolQueryBuilder);

		//2.聚合
		// sourceBuilder.aggregation();


		//3.分页
		Integer pageNum = searchParam.getPageNum();
		Integer pageSize = searchParam.getPageSize();
		sourceBuilder.from((pageNum-1)*pageSize);
		sourceBuilder.size(pageSize);

		//4.高亮
		//TODO
		sourceBuilder.highlighter(new HighlightBuilder());


		//5.排序
		String order = searchParam.getOrder();
		//order=0:desc
		if (!StringUtils.isEmpty(order)) {
			String[] split = order.split(":");
			if (split[0].equals("0")) {
				//0：综合排序，默认顺序
			}
			if (split[0].equals("1")) {
				//1：销量排序
				FieldSortBuilder fieldSortBuilder = SortBuilders.fieldSort("sale");
				if (split[1].equalsIgnoreCase("desc")) {
					fieldSortBuilder.order(SortOrder.DESC);
				} else {
					fieldSortBuilder.order(SortOrder.ASC);
				}
				sourceBuilder.sort(fieldSortBuilder);
			}
			if (split[0].equals("2")) {
				//1：销量排序
				FieldSortBuilder fieldSortBuilder = SortBuilders.fieldSort("price");
				if (split[1].equalsIgnoreCase("desc")) {
					fieldSortBuilder.order(SortOrder.DESC);
				} else {
					fieldSortBuilder.order(SortOrder.ASC);
				}
				sourceBuilder.sort(fieldSortBuilder);
			}
		}

		return null;
	}
}
