package kr.co.ideait.platform.gaiacairos.comp.project;

import com.google.common.collect.Maps;
import org.springframework.transaction.annotation.Transactional;
import kr.co.ideait.platform.gaiacairos.comp.project.service.OrganizationService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContract;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractOrg;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.organization.OrganizationDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.organization.OrganizationForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrganizationComponent extends AbstractComponent {

	@Autowired
	OrganizationService organizationService;

	@Autowired
	FileService fileService;

	/**
	 * 파일 업로드 경로
	 */
	String baseDirPath = FileUploadType.PROJECT.getDirPath(); // Enum으로 기본 디렉토리 경로 생성
	String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM")); // 현재 날짜를 기반으로 하위 경로 추가
	String fullPath = Path.of(baseDirPath, datePath).toString(); // 전체 경로 생성


	// 조직도 추가
	@Transactional
	public void orgCreate (CnContractOrg cnContractOrg,String pjtDiv,String apiYn){
		CnContractOrg organization = organizationService.orgCreate(cnContractOrg);

		// TODO API 연동 - 사업이 공공(P)일 경우 CAIROS -> PGAIA
		if("Y".equals(apiYn)){
			Map<String, Object> invokeParams = Maps.newHashMap();
			invokeParams.put("cnContractOrg", organization);

			if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(pjtDiv)) {
				Map response;
				response = invokeCairos2Pgaia("CAGA1001", invokeParams);

				if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}

		if(organization == null){
			throw new GaiaBizException(ErrorType.NO_DATA);
		}
	}

	// 조직도 수정
	@Transactional
	public CnContractOrg orgUpdate (CnContractOrg cnContractOrg,String pjtDiv,String apiYn){

		CnContractOrg organization = organizationService.updateOrg(cnContractOrg);

		// TODO API 연동 - 사업이 공공(P)일 경우 CAIROS -> PGAIA
		if("Y".equals(apiYn)) {
			Map<String, Object> invokeParams = Maps.newHashMap();
			invokeParams.put("cnContractOrg", organization);

			if (PlatformType.CAIROS.getName().equals(platform) && "P".equals(pjtDiv)) {
				Map response;
				response = invokeCairos2Pgaia("CAGA1002", invokeParams);

				if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}

		if(organization == null){
			throw new GaiaBizException(ErrorType.NO_DATA);
		}

      	return organization;
	}

	// 조직도 삭제
	@Transactional
	public void orgDelete (List<CnContractOrg> organizationList,String pjtDiv,String apiYn){

		organizationService.orgDelete(organizationList);
		List<CnContractOrg> cnContractOrgList = new ArrayList<>();

		for(CnContractOrg cnContractOrg : organizationList){
			cnContractOrgList.add(organizationService.getOrg(cnContractOrg.getCntrctNo(),cnContractOrg.getCntrctOrgId()));
		}

		// TODO API 연동 - 사업이 공공(P)일 경우 CAIROS -> PGAIA
		if("Y".equals(apiYn)){
			Map<String, Object> invokeParams = Maps.newHashMap();
			invokeParams.put("organizationList", cnContractOrgList);

			if ( PlatformType.CAIROS.getName().equals(platform)  && "P".equals(pjtDiv)) {
				Map response;
				response = invokeCairos2Pgaia("CAGA1003", invokeParams);

				if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}
	}

	// 조직도 이미지 추가
	@Transactional
	public void orgCreateImg (String cntrctNo, MultipartFile file,String pjtDiv,String apiYn){

		CnContract cnContract = organizationService.getByCntrctNo(cntrctNo);
		String orgchrtAtchFileNo = cnContract.getOrgchrtAtchFileNo();
		CnAttachments cnAttachment = new CnAttachments();

		// 이미지가 있을때
		if (orgchrtAtchFileNo != null && !orgchrtAtchFileNo.isEmpty()) {
			// 기존 파일 삭제
			organizationService.deleteAttachment(Integer.valueOf(orgchrtAtchFileNo));
			if (file != null && !file.isEmpty()) {
				FileService.FileMeta fileMeta = fileService.save(fullPath, file);
				cnAttachment.setFileNo(Integer.parseInt(orgchrtAtchFileNo));
				cnAttachment.setSno(organizationService.generateSno(Integer.parseInt(orgchrtAtchFileNo)));
				cnAttachment.setFileNm(file.getOriginalFilename());
				cnAttachment.setFileDiskNm(fileMeta.getFileName());
				cnAttachment.setFileDiskPath(fileMeta.getDirPath());
				cnAttachment.setFileSize(fileMeta.getSize());
				cnAttachment.setDltYn("N");
				cnAttachment.setFileHitNum(0);
				organizationService.saveOrgImg(cnAttachment,null);
			}
		} else {
			if (file != null && !file.isEmpty()) {
				Integer fileNo = organizationService.generateFileNo();

				FileService.FileMeta fileMeta = fileService.save(fullPath, file);
				cnAttachment.setFileNo(fileNo);
				cnAttachment.setSno(1);
				cnAttachment.setFileNm(file.getOriginalFilename());
				cnAttachment.setFileDiskNm(fileMeta.getFileName());
				cnAttachment.setFileDiskPath(fileMeta.getDirPath());
				cnAttachment.setFileSize(fileMeta.getSize());
				cnAttachment.setDltYn("N");
				cnAttachment.setFileHitNum(0);

				organizationService.saveOrgImg(cnAttachment,null);
				cnContract.setOrgchrtAtchFileNo(fileNo.toString());
			}
		}

		// TODO API 연동 - 사업이 공공(P)일 경우 CAIROS -> PGAIA
		if("Y".equals(apiYn)) {
			Map<String, Object> invokeParams = Maps.newHashMap();
			invokeParams.put("cntrctNo", cntrctNo);
			invokeParams.put("usrId", UserAuth.get(true).getUsrId());

			Map<String, Object> fileMap = Maps.newHashMap();
			fileMap.put("file", file);

			if (PlatformType.CAIROS.getName().equals(platform) && "P".equals(pjtDiv)) {
				Map response;
				response = invokeCairos2Pgaia("CAGA1004", invokeParams, fileMap);

				if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}
	}

	// 조직도 이미지 삭제
	@Transactional
	public void orgDeleteImg (String cntrctNo,String pjtDiv,String apiYn) {

		CnContract cnContract = organizationService.getByCntrctNo(cntrctNo);
		Integer fileNo = Integer.valueOf(cnContract.getOrgchrtAtchFileNo());

		// 기존 파일 삭제
		organizationService.deleteAttachment(fileNo);	

    	log.info("서비스 값 : >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> {}", apiYn);

		// TODO API 연동 - 사업이 공공(P)일 경우 CAIROS -> PGAIA
		if ("Y".equals(apiYn)) {
			Map<String, Object> invokeParams = Maps.newHashMap();
			invokeParams.put("cntrctNo", cnContract.getCntrctNo());
			invokeParams.put("usrId", UserAuth.get(true).getUsrId());

			log.info("1차 연동하러 들어옵니다........................................................");
			if (PlatformType.CAIROS.getName().equals(platform) && "P".equals(pjtDiv)) {
				log.info("2차 연동하러 들어옵니다........................................................");
				Map response;
				response = invokeCairos2Pgaia("CAGA1005", invokeParams);

				if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}
	}

}
