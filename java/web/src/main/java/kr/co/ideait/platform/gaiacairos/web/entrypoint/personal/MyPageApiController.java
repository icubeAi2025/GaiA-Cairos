package kr.co.ideait.platform.gaiacairos.web.entrypoint.personal;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.platform.gaiacairos.comp.system.MyPageComponent;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmUserInfo;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.iframework.annotation.Description.TYPE;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class MyPageApiController extends AbstractController{
	
	@Autowired
	MyPageComponent myPageComponent;
	
	@Autowired
	PortalService portalService;
	
	@GetMapping("myinfo")
	@Description(name = "내 정보 조회", description = "내 정보를 조회한다", type = TYPE.MEHTOD)
	public Result myInfo(CommonReqVo commonReqVo, HttpServletRequest request) {
		// 공통로그
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("내 정보 조회");
		systemLogComponent.addUserLog(userLog);

		return myPageComponent.getLoginUserInfo(commonReqVo);
	}

	@PostMapping("mypage-update")
	@Description(name = "내 정보 수정", description = "마이페이지 내의 정보 및 파일 수정", type = TYPE.MEHTOD)
	public Result updateMyInfo(CommonReqVo commonReqVo,@RequestPart("user") SmUserInfo user, @RequestPart("flagOfDeleteStamp") boolean flagOfDeleteStamp, @RequestPart(value = "stampFile", required = false) MultipartFile stampFile, HttpServletRequest request) {
		// 공통로그
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("내 정보 수정");
		systemLogComponent.addUserLog(userLog);

		Result ok = Result.ok();

//		String[] userDatas = commonReqVo.getUserParam();
//		String loginId = userDatas[0];

		Map<String, Object> result = myPageComponent.updateProfile(user,commonReqVo.getUserId(), stampFile,flagOfDeleteStamp, commonReqVo);

		if (result.get("FAIL") != null) {
			String failMsg = (String)result.get("FAIL");
			return Result.nok(ErrorType.DATABSE_ERROR,failMsg);
		}
		else {
			Set<Entry<String, Object>> entrySet = result.entrySet();
			for(Entry<String, Object> entry : entrySet) {
				ok = ok.put(entry.getKey(), entry.getValue());
			}
			return ok;
		}
	}

	@GetMapping(value = "/mypage/thumbnail",
		    produces = {
		            MediaType.IMAGE_JPEG_VALUE,
		            MediaType.IMAGE_PNG_VALUE,
		            MediaType.IMAGE_GIF_VALUE,
		            MediaType.APPLICATION_OCTET_STREAM_VALUE
		        })
	@Description(name = "마이페이지 썸네일", description = "마이페이지에 나타날 썸네일 이미지 리소스 반환", type = TYPE.MEHTOD)
	public ResponseEntity<Resource> getThumbnailResource(CommonReqVo commonReqVo, @RequestParam("fileNo") Integer fileNo) {
		// 공통로그
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("마이페이지 썸네일");
		systemLogComponent.addUserLog(userLog);

		HashMap<String, Object> result = null;

		try {
			result = myPageComponent.getFileResource(fileNo);
		} catch (IOException e) {
			throw new GaiaBizException(e);
//			return new ResponseEntity<Resource>(HttpStatusCode.valueOf(500));
		}

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType((String)result.get("mimeType")))
				.body((Resource)result.get("resource"));
	}
	
}
