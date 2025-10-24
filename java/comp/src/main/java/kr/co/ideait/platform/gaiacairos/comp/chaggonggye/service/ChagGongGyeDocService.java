package kr.co.ideait.platform.gaiacairos.comp.chaggonggye.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.FileService.FileMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChagGongGyeDocService extends AbstractGaiaCairosService {
	
    @Autowired
    FileService fileService;
    
    /**
	 * 착공계 문서 탬플릿 조회
	 * @param templateSeq
	 * @return
	 */
	public Map<String, Object> selectDocTemplate(String templateSeq) {
		
		log.info("templateSeq: {}", templateSeq);
		
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.chaggonggye.chaggonggyeDoc.selectDocTemplate", Integer.parseInt(templateSeq));
		
	}

	/**
	 * 착공계 문서 탬플릿 조회
	 * @param docId
	 * @return
	 */
	public Map<String, Object> selectDocTemplateToStorageMain(String docId) {
		log.info("docId: {}", docId);

		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.chaggonggye.chaggonggyeDoc.selectDocTemplateToStorageMain", docId);
	}
	
	/**
	 * 문서 기본데이터 조회
	 * @param docType
	 * @param cntrctNo
	 * @return
	 */
	public Map<String, Object> selectDefaultData(String docType, String cntrctNo) {

		log.info("docType: {}", docType);
		log.info("cntrctNo: {}", cntrctNo);
		
		String sqlID = environment.getProperty(docType, "");

		// sqlID가 없으면, 기본 쿼리 호출 (TODO: 제공할 수 있는 기본 데이터를 담은 테이블 생성 후, 해당 쿼리 호출하도록 변경)
		if(sqlID.isEmpty()) {
			log.info("sqlID is null or empty");
			log.info("기본 데이터를 조회할 sqlID가 없습니다.");
//			throw new GaiaBizException(ErrorType.NOT_FOUND, "기본 데이터를 조회할 sqlID가 없습니다.");

			return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.chaggonggye.chaggonggyeDoc.selectDefaultData", cntrctNo);
		}

		log.info("sqlID: {}", sqlID);

		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.chaggonggye.chaggonggyeDoc.selectDefault"+sqlID+"Data", cntrctNo);
	}
	
	/**
	 * 추가 데이터 조회
	 * @param naviId
	 * @param docId
	 * @return
	 */
	public List<Map<String, Object>> selectAddData(String naviId, String docId) {
		
		log.info("naviId: {}", naviId);
		log.info("docId: {}", docId);
		MybatisInput input = MybatisInput.of().add("docId", docId)
				.add("naviId", naviId);
				
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.chaggonggye.chaggonggyeDoc.selectAddData", input);
	}
	
	
	/**
	 * 완성된 착공계 문서 DISK 저장 정보 가져오기
	 * @param naviId
	 * @param docId
	 * @return
	 */
	public Map<String, Object> selectFileInfo(String naviId, String docId) {
		
		log.info("naviId: {}", naviId);
		log.info("docId: {}", docId);
		MybatisInput input = MybatisInput.of().add("docId", docId)
				.add("naviId", naviId);
		
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.chaggonggye.chaggonggyeDoc.selectFileInfo", input);
		
	} 
	
    	
	 /**
     * 임시!! 추후 문서관리로 이동
     * @param dcStorageMainForm
     * @param files
     */
    public void addNewFiles(String param, MultipartFile file) {
        /**
         * 파일 업로드 경로
         */
//        String baseDirPath = FileUploadType.DOCUMNET.getDirPath(); // Enum으로 기본 디렉토리 경로 생성
//        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM")); // 현재 날짜를 기반으로 하위 경로 추가
//        String fullPath = Path.of(baseDirPath, datePath).toString(); // 전체 경로 생성
        
        String[] keyParam = param.split("_");
            
    	String originalFileName = null;

    	if("D0001".equals(keyParam[1])) {
    		originalFileName = "착공신고서.pdf";
    	}else if("D0002".equals(keyParam[1])){
    		originalFileName = "현장조직도.pdf";        		
    	}else if("D0003".equals(keyParam[1])){
    		originalFileName = "현장대리인 지정신고서.pdf";        		
    	}else if("D0004".equals(keyParam[1])){
    		originalFileName = "품질관리자 지정신고서.pdf";        		
    	}else if("D0005".equals(keyParam[1])){
    		originalFileName = "안전관리자 지정신고서.pdf";        		
    	}


        // 파일 저장
        FileMeta fileMeta = fileService.save(getUploadPathByWorkType(FileUploadType.DOCUMENT), file);
        
        MybatisInput input = MybatisInput.of().add("docId", keyParam[2]+"_"+keyParam[3])
				.add("naviId", keyParam[0]+"_"+keyParam[1])
				.add("docNm", originalFileName)
				.add("docDiskNm", fileMeta.getFileName())
				.add("docDiskPath", fileMeta.getDirPath())
				.add("docSize", fileMeta.getSize());  
        
        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.chaggonggye.chaggonggyeDoc.insertchagGongGyeDocFile", input);
    }

	/**
	 * 완성된 착공계 문서 DISK 저장
	 * @param hwpFile
	 * @param pdfFile
	 * @param naviId (keyParam[0])
	 * @param docId	 (keyParam[1])
	 * @return
	 */
	public Map<String, Object> updateDiskFileInfo(List<MultipartFile> hwpFile, List<MultipartFile> pdfFile,
			String naviId, String docId) throws IOException {
		
		Map<String, Object> selectFileInfo = selectFileInfo(naviId, docId);
		Map<String, Object> result = new ConcurrentHashMap<>();
        
        if (selectFileInfo != null && !selectFileInfo.isEmpty()) {
            Path dirPath = Path.of((String) selectFileInfo.get("doc_disk_path"));
            
            if(Files.notExists(dirPath)){
                Files.createDirectories(dirPath);
            }
        	
            for (MultipartFile file : hwpFile) {
                log.info("getName: {}", file.getName());
                log.info("getSize: {}", file.getSize());
                log.info("getOriginalFilename: {}", file.getOriginalFilename());
                log.info("isEmpty: {}", file.isEmpty());
                Path filePath = Path.of((String) selectFileInfo.get("doc_disk_path"), selectFileInfo.get("doc_disk_nm")+".hwpx");
        
                try {
                    file.transferTo(filePath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
          
            for (MultipartFile file : pdfFile) {
                log.info("getName: {}", file.getName());
                log.info("getSize: {}", file.getSize());
                log.info("getOriginalFilename: {}", file.getOriginalFilename());
                log.info("isEmpty: {}", file.isEmpty());
                Path filePath = Path.of((String) selectFileInfo.get("doc_disk_path"), selectFileInfo.get("doc_disk_nm")+".pdf");
        
                try {
                    file.transferTo(filePath);

					MybatisInput input = MybatisInput.of().add("docId", docId)
														.add("docSize", file.getSize());
					mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.chaggonggye.chaggonggyeDoc.updateFileInfo", input);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

			result.put("resultCode", "success");
        }else {
        	//파일 정보가 없어서 파일 저장 못하는 오류 발생!!
            log.info("파일 정보가 없습니다.");
			result.put("resultCode", "fail");
			result.put("resultMsg", "파일 정보가 없습니다.");
        }

		return result;

	}

	/**
	 * 병합된 착공계 문서 DISK 저장
	 * @param pdfFile
	 * @param naviId (keyParam[0])
	 * @param docId	 (keyParam[1])
	 * @return
	 */
	public Map<String, Object> updateDiskFileInfo(List<MultipartFile> pdfFile,
												  String naviId, String docId) throws IOException {

		Map<String, Object> selectFileInfo = selectFileInfo(naviId, docId);
		Map<String, Object> result = new ConcurrentHashMap<>();

		if (selectFileInfo != null && !selectFileInfo.isEmpty()) {
			Path dirPath = Path.of((String) selectFileInfo.get("doc_disk_path"));

			if(Files.notExists(dirPath)){
				Files.createDirectories(dirPath);
			}

			for (MultipartFile file : pdfFile) {
				log.info("getName: {}", file.getName());
				log.info("getSize: {}", file.getSize());
				log.info("getOriginalFilename: {}", file.getOriginalFilename());
				log.info("isEmpty: {}", file.isEmpty());
				Path filePath = Path.of((String) selectFileInfo.get("doc_disk_path"), selectFileInfo.get("doc_disk_nm")+".pdf");

				try {
					file.transferTo(filePath);

					MybatisInput input = MybatisInput.of().add("docId", docId)
							.add("docSize", file.getSize());
					mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.chaggonggye.chaggonggyeDoc.updateFileInfo", input);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			result.put("resultCode", "success");
		}else {
			//파일 정보가 없어서 파일 저장 못하는 오류 발생!!
			log.info("파일 정보가 없습니다.");
			result.put("resultCode", "fail");
			result.put("resultMsg", "파일 정보가 없습니다.");
		}

		return result;

	}


	public List<Map<String, Object>> selectMergeAttachmentList(String docId) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.chaggonggye.chaggonggyeDoc.selectMergeAttachmentList", docId);
	}
}
