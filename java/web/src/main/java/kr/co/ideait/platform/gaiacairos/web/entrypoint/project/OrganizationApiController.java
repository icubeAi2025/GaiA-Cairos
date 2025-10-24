package kr.co.ideait.platform.gaiacairos.web.entrypoint.project;

import jakarta.validation.Valid;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.project.OrganizationComponent;
import kr.co.ideait.platform.gaiacairos.comp.project.service.OrganizationService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContract;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractOrg;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.organization.OrganizationForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.organization.OrganizationMybatisParam.OrganizationInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.organization.OrganizationMybatisParam.OrganizationListInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("api/project/organization")
public class OrganizationApiController extends AbstractController {

    @Autowired
    OrganizationComponent organizationComponent;

    @Autowired
    OrganizationService organizationService;

    @Autowired
    OrganizationForm organizationForm;

    /*
     * 조직도 이미지 불러오기
     */
    @GetMapping("/getImg/{cntrctNo}")
    @Description(name = "조직도 이미지 조회", description = "프로젝트의 계약별 조직도 이미지 조회", type = Description.TYPE.MEHTOD)
    public Result getOrgImg(@PathVariable("cntrctNo") String cntrctNo) {

        CnContract cnContract = organizationService.getByCntrctNo(cntrctNo);

        if (cnContract == null) {
            throw new GaiaBizException(ErrorType.NO_DATA, "계약이 존재하지않습니다.");
        }

        String orgchrtAtchFileNo = cnContract.getOrgchrtAtchFileNo();
        CnAttachments attachment = null;

        if (orgchrtAtchFileNo != null && !orgchrtAtchFileNo.isEmpty()) {
            attachment = organizationService.getFile(Integer.parseInt(orgchrtAtchFileNo));
        }

        return Result.ok().put("organization", attachment);
    }

    /*
     * 조직도 목록 조회
     */
    @GetMapping("/list")
    @Description(name = "조직도 목록 조회", description = "프로젝트의 계약별 조직도 목록 조회", type = Description.TYPE.MEHTOD)
    public GridResult getOrgList(OrganizationForm.OrganizationListGet organizationListGet) {
        log.debug("organizationListGet = {}", organizationListGet);
        OrganizationListInput input = organizationForm.toOrganizationListInput(organizationListGet);

        return GridResult
                .ok(organizationService.getOrgList(input));

    }

    /**
     * 조직도 조회
     * 
     */
    @GetMapping("/{cntrctNo}/{cntrctOrgId}")
    @Description(name = "조직 상세조회", description = "프로젝트의 계약별 조직 상세조회", type = Description.TYPE.MEHTOD)
    public Result getOrg(OrganizationForm.OrganizationListGet organizationListGet) {

        OrganizationInput input = organizationForm.toOrganizationInput(organizationListGet);
        return Result.ok()
                .put("organization", organizationService.getOrgDetail(input));
    }

    /*
     * 조직도 생성
     */
    @PostMapping("/create")
    @Description(name = "조직 추가", description = "프로젝트의 계약별 조직 추가", type = Description.TYPE.MEHTOD)
    public Result orgCreate(CommonReqVo commonReqVo, @RequestBody @Valid OrganizationForm.CreateOrganization organization) {

        organizationComponent.orgCreate(organizationForm.toCreateOrganization(organization),commonReqVo.getPjtDiv(),commonReqVo.getApiYn());

        return Result.ok();
    }

    /**
     * 조직 수정
     * 
     */
    @PostMapping("/update")
    @Description(name = "조직 수정", description = "프로젝트의 계약별 조직 수정", type = Description.TYPE.MEHTOD)
    public Result updateOrg(CommonReqVo commonReqVo, @RequestBody @Valid OrganizationForm.UpdateOrganization organization) {

        if (organization != null) { // 데이터가 있는지 확인
            CnContractOrg org = organizationService.getOrg(organization.getCntrctNo(),organization.getCntrctOrgId());
            organizationForm.toUpdateOrganization(organization, org); // 전달된 내용만 매핑
            CnContractOrg cnContractOrg = organizationComponent.orgUpdate(org,commonReqVo.getPjtDiv(),commonReqVo.getApiYn());

            return Result.ok().put("organization", cnContractOrg);
        }
        throw new GaiaBizException(ErrorType.NO_DATA);
    }

    /*
     * 조직 삭제
     */
    @PostMapping("/delete")
    @Description(name = "조직 삭제", description = "프로젝트의 계약별 조직 삭제", type = Description.TYPE.MEHTOD)
    public Result orgDelete(CommonReqVo commonReqVo, @RequestBody @Valid OrganizationForm.OrganizationList organizationList) {
        organizationComponent.orgDelete(organizationList.getOrganizationList(),commonReqVo.getPjtDiv(),commonReqVo.getApiYn());
        return Result.ok();
    }


    /**
     * 조직도 이미지 추가
     */
    @PostMapping("/img/create/{cntrctNo}")
    @Description(name = "조직도 이미지 추가,수정", description = "프로젝트의 계약별 조직도 이미지 추가,수정", type = Description.TYPE.MEHTOD)
    public Result orgCreateImg(CommonReqVo commonReqVo, @PathVariable(value = "cntrctNo") String cntrctNo,
            @RequestPart(value = "files", required = false) MultipartFile file)
            throws IllegalStateException {

        organizationComponent.orgCreateImg(cntrctNo,file,commonReqVo.getPjtDiv(),commonReqVo.getApiYn());

        return Result.ok();
    }

    /**
     * 조직도 이미지 삭제
     */
    @PostMapping("/img/delete/{cntrctNo}")
    @Description(name = "조직도 이미지 삭제", description = "프로젝트의 계약별 조직도 이미지 삭제", type = Description.TYPE.MEHTOD)
    public Result orgDeleteImg(CommonReqVo commonReqVo, @PathVariable(value = "cntrctNo") String cntrctNo)
            throws IllegalStateException {
    	log.info("컨트롤러 값 : >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> {}", commonReqVo.getApiYn());

        organizationComponent.orgDeleteImg(cntrctNo,commonReqVo.getPjtDiv(),commonReqVo.getApiYn());

        return Result.ok();
    }
}
