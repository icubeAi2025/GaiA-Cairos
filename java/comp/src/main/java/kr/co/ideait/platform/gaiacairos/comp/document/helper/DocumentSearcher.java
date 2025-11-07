package kr.co.ideait.platform.gaiacairos.comp.document.helper;

import kr.co.ideait.platform.gaiacairos.comp.document.dto.SearchVO;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class DocumentSearcher {

	static final Integer DISPLAY_COUNT = 100000;

	private final SolrTemplate solrTemplate;

	public Map<String, Object> search(Map<String, Object> params, int pageIndex, int fetchSize) {
		Map<String, Object> resultMap = new HashMap<>();

		String refSysKey = MapUtils.getString(params, "refSysKey");
		String naviKey = MapUtils.getString(params, "naviKey");
		String naviDiv = MapUtils.getString(params, "naviDiv");
		String naviFolderType = MapUtils.getString(params, "naviFolderType");
		String searchType = MapUtils.getString(params, "searchType");
		String keyword = MapUtils.getString(params, "keyword");
		String orderType = MapUtils.getString(params, "orderType");

		String[] keywords = keyword.split(" ");

		try {
			Pageable pageable = PageRequest.of(pageIndex - 1, fetchSize, sort(orderType));
			Query search = new SimpleQuery(createSearchConditions(refSysKey, naviKey, naviDiv, naviFolderType, searchType, keywords));
			search.setOffset(0L);
			search.setRows(DISPLAY_COUNT);
			search.addSort(sort(orderType));
			search.setPageRequest(pageable);

			Page page = solrTemplate.query("doc-mgmt", search, SearchVO.class);

			resultMap.put("totalCnt", page.getTotalElements());
			resultMap.put("data", page.getContent());
			resultMap.put("pageable", pageable);

			log.info("resultMap: {}", resultMap);
		} catch (GaiaBizException e) {
			log.error("solrQuery error", e);
			throw new GaiaBizException(ErrorType.DATABSE_ERROR, "solrQuery error", e);
		}

		return resultMap;
	}

	private Criteria createSearchConditions(String refSysKey, String naviKey, String naviDiv, String naviFolderType, String searchType, String[] keywords) {
		Criteria conditions = new Criteria(SearchVO.FIELD_REF_SYS_KEY).is(refSysKey)
				.and(new Criteria(SearchVO.FIELD_NAVI_KEY).is(naviKey))
				.and(new Criteria(SearchVO.FIELD_NAVI_DIV).is(naviDiv));

		if(naviFolderType != null && !naviFolderType.isEmpty()){
			conditions = conditions.and(new Criteria(SearchVO.FIELD_NAVI_FOLDER_TYPE).is(naviFolderType));
		}

		for (String keyword : keywords) {
			if ("doc_name".equals(searchType)) {
				conditions = conditions.or(new Criteria(SearchVO.FIELD_DOC_NM).contains(keyword));
			} else if ("user_name".equals(searchType)) {
				conditions = conditions.or(new Criteria(SearchVO.FIELD_RGSTR_NM).contains(keyword));
			} else if ("user_id".equals(searchType)) {
				conditions = conditions.or(new Criteria(SearchVO.FIELD_RGSTR_ID).contains(keyword));
			} else {
				conditions = conditions.and(
						new Criteria(SearchVO.FIELD_NAVI_NO).contains(keyword)
						.or(new Criteria(SearchVO.FIELD_NAVI_ID).contains(keyword))
						.or(new Criteria(SearchVO.FIELD_NAVI_PATH).contains(keyword))
						.or(new Criteria(SearchVO.FIELD_NAVI_NM).contains(keyword))
						.or(new Criteria(SearchVO.FIELD_DOC_NO).contains(keyword))
						.or(new Criteria(SearchVO.FIELD_DOC_ID).contains(keyword))
						.or(new Criteria(SearchVO.FIELD_DOC_TYPE).contains(keyword))
						.or(new Criteria(SearchVO.FIELD_DOC_PATH).contains(keyword))
						.or(new Criteria(SearchVO.FIELD_DOC_NM).contains(keyword))
						.or(new Criteria(SearchVO.FIELD_PROPERTY_DATA).contains(keyword))
						.or(new Criteria(SearchVO.FIELD_RGSTR_NM).contains(keyword))
						.or(new Criteria(SearchVO.FIELD_RGSTR_ID).contains(keyword))
						.or(new Criteria(SearchVO.FIELD_RGST_DT).contains(keyword))
						.or(new Criteria(SearchVO.FIELD_CHG_DT).contains(keyword))
				);
			}
		}

		return conditions;
	}

	private Sort sort(String order) {
		return Sort.by("O".equals(order) ? Sort.Direction.ASC : Sort.Direction.DESC, SearchVO.FIELD_RGST_DT, SearchVO.FIELD_CHG_DT);
	}
}
