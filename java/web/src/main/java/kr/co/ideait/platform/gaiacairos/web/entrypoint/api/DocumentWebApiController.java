package kr.co.ideait.platform.gaiacairos.web.entrypoint.api;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.platform.gaiacairos.comp.document.DocumentComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.*;


/**
 * GAIA CMIS API 통신용
 */
@Slf4j
@RestController
@RequestMapping("/webApi")
@RequiredArgsConstructor
public class DocumentWebApiController extends AbstractController {

    private final DocumentComponent documentComponent;

    // dc_search 데이터 조회
    @PostMapping(value="/document/search")
    public Result documentSearch(@RequestParam Map<String, Object> params, HttpServletRequest request) throws Exception {

        Map<String, Object> searchDataList = documentComponent.getDcSearchDataAll(params);

        if(searchDataList == null || searchDataList.isEmpty()) {
            log.info("searchDataList is null or empty");
            throw new GaiaBizException(ErrorType.NOT_FOUND, "searchDataList must not be null or empty");
        }

        return Result.ok(searchDataList);

    }
}
