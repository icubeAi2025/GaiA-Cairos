package kr.co.ideait.platform.gaiacairos.comp.system.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmAuthorityGroup;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmDepartment;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.user.UserMybatisParam.UserOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.portal.ProfileDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.portal.PortalMybatisParam.MainComprehensiveProjectInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.portal.ProfileDto.Profile;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.FileService.FileMeta;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MypageService extends AbstractGaiaCairosService{
	  /**
     * MyPage [dsJung]
     * 
     */
    @Autowired
    FileService fileService;

    /**
     * 
     * @param loginId ({@link String}) 로그인 한 유저의 로그인 아이디 - ex) apple@juice.com
     * @return {@link UserOutput} 유저 정보를 담고 있는 객체
     */
//	MyBatis
    public UserOutput getLoginUserInfo(String loginId) {
		UserOutput result = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.mypage.selectLoginUserDetail", loginId);
		if(result != null) {
			return result;
		}
		return null;
	}
    

	/**
	 * 직인 파일 추가
	 * 
	 * @param stampFile ({@link MultipartFile}) 저장될 파일
	 * @param usrId ({@link String})      저장될 파일의 등록자 ID
	 * @return {@link ProfileDto.Profile} DB에 저장된 파일의 정보
	 */
	@Transactional
	public Profile insertProfileFile(MultipartFile stampFile, String usrId, String rgstrId, Profile apiSavedFile) {
		String orgName = stampFile.getOriginalFilename();
		// usrId\MM\DD
		String baseDirPath = getUploadPathByWorkTypeForPersonal(usrId);
		String fullPath = String.format("%s%s%s", uploadPersonalPath, File.separator, baseDirPath);
		Profile fileDto = null;
		// api로 넘겨받은 파일이 있는 경우
		if(apiSavedFile != null) {
			fileService.saveByFullPath(fullPath, stampFile, apiSavedFile.getFileDiskNm());
			fileDto = apiSavedFile;
		}
		else{
			FileMeta fileMeta = fileService.saveByFullPath(fullPath, stampFile);

			fileDto = new Profile();
			fileDto.setUsrId(usrId);
			fileDto.setFileDiskNm(fileMeta.getFileName());
			fileDto.setFileDiskPath(fileMeta.getDirPath());
			fileDto.setFileOrgNm(orgName);
			fileDto.setFileSize(new BigDecimal(fileMeta.getSize()));
			fileDto.setStampYn("Y");
			fileDto.setRgstrId(rgstrId);
			fileDto.setChgId(rgstrId);
		}

		if(mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.mypage.insertFile", fileDto) == 1) {
			Profile result = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.mypage.selectFileByDiskNm",fileDto.getFileDiskNm());
			return result;
		}
		return null;
	}

	/**
	 * 직인 파일 수정(기존 파일 삭제 및 새로운 파일 업로드)
	 *
	 * @param stampFile ({@link MultipartFile}) 저장될 파일
	 * @param userId ({@link String}) 저장될 파일의 대상 ID
	 * @param chgId ({@link String}) 저장될 파일의 등록자 및 삭제될 파일의 삭제자 ID
	 * @return {@link ProfileDto.Profile} DB에 저장된 파일의 정보
	 */

	@Transactional
	public Profile updateProfileFile(MultipartFile stampFile, String userId, String chgId, Profile apiSavedFile) {
		List<Profile> list = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.mypage.selectFilesByUsrId",
				userId);

		Profile savedFile = null;
		// 파일이 있었던 경우를 체크할 변수들
		Integer updatedStampFileFileNo = 0;
		for (Profile profile : list) {
			if ("Y".equals(profile.getStampYn())) {
				updatedStampFileFileNo = profile.getFileNo();
			}
		}
		if (updatedStampFileFileNo != null && updatedStampFileFileNo != 0) {
			// 스탬프 파일 수정
			if(deleteProfileFile(updatedStampFileFileNo,userId,chgId)){
				savedFile = insertProfileFile(stampFile, userId, chgId, apiSavedFile);
			}
		}
		else{
			savedFile = insertProfileFile(stampFile, userId, chgId, apiSavedFile);
		}
		return savedFile;
	}

	/**
	 * 직인 파일 삭제
	 *
	 * @param fileNo {@link Integer} 삭제될 파일의 번호(pk)
	 * @param userId ({@link String}) 삭제될 파일의 삭제자 ID
	 * @return <em>boolean</em> 성공 실패 여부
	 */
	@Transactional
	public boolean deleteProfileFile(Integer fileNo, String userId,String dltId) {
		//TODO : 물리삭제
		HashMap<String, Object> map = new HashMap<>();
		map.put("fileNo", fileNo);
		map.put("userId", userId);
		map.put("dltId", dltId);
		return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.mypage.updateDltYn", map) == 1;
	}


	/**
	 * 직인 파일 조회
	 * 
	 * @param usrId ({@link String}) 로그인 한 유저의 회원 아이디
	 * @return List<ProfileDto.Profile> 로그인 한 유저의 모든 프로필 파일 리스트
	 */
	public List<Profile> selectProfileFileList(String usrId) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.mypage.selectFilesByUsrId", usrId);
	}

	/**
	 * 썸네일 생성을 위한 파일의 리소스 반환
	 *
	 * @param fileNo (<em>int</em>) 리소스를 반환받을 파일의 번호
	 * @return {@link Resource} 파일 리소스
	 * @throws IOException
	 */
	public HashMap<String, Object> getFileResource(int fileNo) throws IOException{
		HashMap<String, Object> result = new HashMap<String, Object>();
		Profile fileDto = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.mypage.selectFileByFileNo",fileNo);
		String fileFullDiskPath = String.format("%s/%s", fileDto.getFileDiskPath(), fileDto.getFileDiskNm());
		Path path = Paths.get(fileFullDiskPath);
		String mimeType = Files.probeContentType(path);
		Resource resource = new InputStreamResource(Files.newInputStream(path));
		result.put("resource", resource);
		result.put("mimeType", mimeType);
		return result;
	}

	/**
	 * 로그인 된 유저의 부서 및 권한그룹 데이터 조회
	 * 
	 * @param loginId ({@link String}) 로그인 한 유저의 로그인 아이디
	 * @param platform ({@link String}) 현재 접속중인 서비스의 플랫폼
	 * @return {@link HashMap} 조회된 부서 및 권한그룹 데이터 리스트들이 담긴 HashMap
	 */
	public HashMap<String, Object> getDeptAndAutorityGrpInfo(String loginId, String platform) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		HashMap<String, Object> datas = new HashMap<String, Object>();
		datas.put("loginId", loginId);
		datas.put("platform", platform.toUpperCase());

		List<SmAuthorityGroup> authList = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.mypage.selectAuthInfoByUsrId",datas);

		result.put("authList", authList);
		return result;
	}
	
//	TODO : 내가 속한 모든 계약, 모든 프로젝트 불러오는 로직 작성해야 함
	public List<Map<String, Object>> selectNormalMainComprehensiveProjectList(MainComprehensiveProjectInput param, boolean isAll) {

		if(isAll){
			return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.mypage.selectAllProjectAndContract",param.getUsrId());
		}
		else{
			if ("CAIROS".equals(platform.toUpperCase())) {
				return mybatisSession.selectList(
						"kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.selectCAIROSMainComprehensiveProjectList", param);
			} else {
				return mybatisSession.selectList(
						"kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.selectGAIAMainComprehensiveProjectList", param);
			}
		}
	}

}








