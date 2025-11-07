package kr.co.ideait.platform.gaiacairos.comp.resources;

import kr.co.ideait.platform.gaiacairos.core.util.restclient.Doc24Client;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceComponent {

    private final Doc24Client doc24Client;

    private final CommonCodeService commonCodeService;

    /**
     * 문서24 조직 정보 검색
     * @param keyword
     * @param currentPage
     * @return
     */
    public Map<String, Object> searchDoc24Organization(String keyword, Integer currentPage) {
        Map<String, Object> result = doc24Client.searchOrganization(keyword, currentPage);
        List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("list");

        commonCodeService.asyncSaveOrgList(null, list); //async service

        return result;
    }

    /**
     * 문서24 조직 정보 조회
     * @param parentCode
     * @return
     */
    public List<Map<String, Object>> getDoc24TreeOrganization(String parentCode, String searchTreeOrgNm) {

        List<Map<String, Object>> list = doc24Client.getOrganizationByTree(parentCode, searchTreeOrgNm);

        commonCodeService.asyncSaveOrgList(parentCode, list); //async service

        return list;
    }
}
