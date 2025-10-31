package kr.co.ideait.platform.gaiacairos.comp.design.service;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.ideait.platform.gaiacairos.comp.design.helper.DesignHelper;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDwg;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmResponse;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DmAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DmDwgRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DmResponseRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.responses.DesignResponsesForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.responses.DesignResponsesMybatisParam.DesignResponsesInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.util.*;

@Slf4j
@Service
public class AttachmentsService extends AbstractGaiaCairosService {
    @Autowired
    FileService fileService;
    public DmAttachments createDmAttachment(FileService.FileMeta fileMeta,String userId){
        DmAttachments dmAttachments = new DmAttachments();
        dmAttachments.setFileNo(UUID.randomUUID().toString());
        dmAttachments.setFileKey(UUID.randomUUID().toString());
        dmAttachments.setRgstrId(userId);
        dmAttachments.setChgId(userId);
        dmAttachments.setFileNm(fileMeta.getOriginalFilename());
        dmAttachments.setFileDiskNm(fileMeta.getFileName());
        dmAttachments.setFileDiskPath(fileMeta.getDirPath());
        dmAttachments.setFileSize(fileMeta.getSize());
        dmAttachments.setFileHitNum((short)0);
        dmAttachments.setDltYn("N");
        dmAttachments.setSno((short)1);

        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.attachment.insertAttachment",dmAttachments);

        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.attachment.selectAttachment",dmAttachments);
    }
    public DmAttachments insertDmAttachment(DmAttachments dmAttachments){
        if(StringUtils.isEmpty(dmAttachments.getRgstrId())){
            String userId = UserAuth.get(true).getUsrId();
            dmAttachments.setRgstrId(userId);
        }
        if(dmAttachments.getSno() == null){
            short sno = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designReview.selectMaxSno",dmAttachments);
            dmAttachments.setSno((short)(sno+1));
        }

        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.attachment.insertAttachment",dmAttachments);

        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.attachment.selectAttachment",dmAttachments);
    }

    /**
     * 
     * @param newFiles ({@link List}) 추가 할 MultipartFile 리스트
     * @param cntrctNo
     * @param params
     * @return
     */
    public List<DmAttachments> insertDmAttachments(List<MultipartFile> newFiles, String cntrctNo, Map<String,Object> params){
        if (params == null) {
            params = new HashMap();
        }

        String fileNo = (String)params.get("fileNo");
        if(StringUtils.isEmpty(fileNo)){
            fileNo = UUID.randomUUID().toString();
        }

        String userId = (String) params.get("userId");
        if (StringUtils.isEmpty(userId)) {
            userId = UserAuth.get(true).getUsrId();
        }

        List<DmAttachments> attachmentsList = objectMapper.convertValue(params.get("dmAttachmentsList"), new TypeReference<List<DmAttachments>>() {});

        //로직을 위한 List
        List<DmAttachments> dmAttachmentsList = new ArrayList<>();

        int i= 0 ;
        for (MultipartFile file : newFiles) {
            //이건 방금 저장된 파일 정보
            FileService.FileMeta fileMeta = fileService.save(getUploadPathByWorkType(FileUploadType.DESIGN,cntrctNo), file);



            //이건 로직을 위한 파일 DTO
            DmAttachments dmAttachments = new DmAttachments();
            //넘겨받은 파일 정보들이 있다면
            if (attachmentsList != null && attachmentsList.size() == newFiles.size()) {
                //이건 API로 넘겨받은 저장된 파일 정보
                DmAttachments savedFileMeta = attachmentsList.get(i);
                dmAttachments.setFileKey(savedFileMeta.getFileKey());
                dmAttachments.setFileNo(savedFileMeta.getFileNo());
            }
            else{
                dmAttachments.setFileKey(UUID.randomUUID().toString());
                dmAttachments.setFileNo(fileNo);
            }

            dmAttachments.setFileNm(file.getOriginalFilename());
            dmAttachments.setFileDiskNm(fileMeta.getFileName());
            dmAttachments.setFileDiskPath(fileMeta.getDirPath());
            dmAttachments.setFileSize(fileMeta.getSize());
            dmAttachments.setDltYn("N");
            dmAttachments.setFileHitNum((short) 0);
            dmAttachments.setRgstrId(userId);
            dmAttachments.setChgId(userId);

            short sno = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.attachment.selectMaxSno",dmAttachments.getFileNo());

            dmAttachments.setSno((short)(sno+1));

            mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.attachment.insertAttachment",dmAttachments);
            dmAttachmentsList.add(dmAttachments);
            i++;
        }
        return dmAttachmentsList;
    }

    public boolean deleteDmAttachment(DmAttachments dmAttachments){
        if(StringUtils.isEmpty(dmAttachments.getDltId())){
            String userId = UserAuth.get(true).getUsrId();
            dmAttachments.setDltId(userId);
        }
        return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.attachment.deleteAttachment",dmAttachments) == 1;
    }
    public boolean deleteDmAttachments(List<DmAttachments> dmAttachmentsList,String userId){
        for(int i=0;i<dmAttachmentsList.size();i++){
            DmAttachments dmAttachments = dmAttachmentsList.get(i);
            dmAttachments.setDltId(userId);
            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.attachment.deleteAttachment",dmAttachments);
        }
        return true;
    }
    public int deleteDmAttachmentsOfFileNo(String fileNo, String userId){
        HashMap<String,Object> params = new HashMap<>();
        params.put("fileNo",fileNo);
        params.put("userId",userId);
        return mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.attachment.deleteDmAttachmentsOfFileNo",params);
    }
    public DmAttachments selectAttachment(String fileNo, String fileKey, Short sno){
        DmAttachments dmAttachments = new DmAttachments();
        dmAttachments.setFileKey(fileKey);
        dmAttachments.setFileNo(fileNo);
        dmAttachments.setSno(sno);
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.attachment.selectAttachment",dmAttachments);
    }

    public List<DmAttachments> selectAttachmentsOfFileNo(String fileNo){
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.attachment.selectDmAttachmentsOfFileNo",fileNo);
    }
}
