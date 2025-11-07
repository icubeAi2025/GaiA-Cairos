package kr.co.ideait.platform.gaiacairos.comp.design.helper;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractBase;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DmAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DmDesignReviewRepository;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class DesignHelper extends AbstractBase {
    @Autowired
    @Qualifier("sqlSessionTemplate")
    protected SqlSessionTemplate mybatisSession;


    @Autowired
    DmDesignReviewRepository designRepository;

    @Autowired
    DmAttachmentsRepository dmAttachmentsRepository;

    /**
     * UUID 생성
     * @return
     */
    public String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public DmAttachments getDmAttachments(DmAttachments dmAttachments) {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mappers.design.designAttachments.selectDmAttachments", dmAttachments);
    }

    /**
     * 설계번호 생성
     */
    public String createDsgnNo() {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designReview.selectMaxDsgnNoOfDay");
    }

    public Integer createDsgnSeq() {
        Integer maxDsgnSeq = designRepository.findMaxDsgnSeq();
        return maxDsgnSeq==null ? 1 : maxDsgnSeq+1;
    }

    public Short generateSno(String fileNo) {
        return dmAttachmentsRepository.findMaxSnoByFileNo(fileNo);
    }

    /**
     * 단일 첨부파일 저장
     *
     */
    public DmAttachments createDmAttachment(DmAttachments dmAttachments) {
        DmAttachments selectDm = null;
        String fileNo = null;

        if (dmAttachments != null) {
            fileNo = dmAttachments.getFileNo();

            if (fileNo == null) {
                fileNo = this.generateUUID(); // 가장 큰 fileNo 값을 기반으로 새 fileNo 생성
            }

            short sno = 1; // sno는 1부터 시작

            if (dmAttachments.getFileNo() == null) { // 기존 fileNo 없을시
                dmAttachments.setFileNo(fileNo); // 파일들에 동일한 fileNo 설정
                dmAttachments.setSno(sno); // 각 파일에 대해 순차적인 sno 설정
            } else { // 파일 수정
                dmAttachments.setSno((short) (dmAttachmentsRepository.findMaxSnoByFileNo(dmAttachments.getFileNo()) + 1));
            }

//        return dmAttachmentsRepository.save(dmAttachments); // 파일 저장
            selectDm = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mappers.design.designAttachments.selectDmAttachments", dmAttachments);

            if (selectDm != null) {
                dmAttachments.setFileKey(selectDm.getFileKey());
                mybatisSession.update("kr.co.ideait.platform.gaiacairos.mappers.design.designAttachments.updateDmAttachments", dmAttachments);
            } else {
                dmAttachments.setFileKey( StringUtils.isEmpty(dmAttachments.getFileKey()) ? generateUUID() : dmAttachments.getFileKey());
                mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mappers.design.designAttachments.insertDmAttachments", dmAttachments);

                selectDm = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mappers.design.designAttachments.selectDmAttachments", dmAttachments);
            }
        }

        return selectDm;
    }


    /**
     * 첨부파일 리스트 저장
     *
     */
//    public List<DmAttachments> createDmAttachmentsList(List<DmAttachments> dmAttachmentsList) {
//        String fileNo = null; // 가장 큰 fileNo 값을 기반으로 새 fileNo 생성
//
//        if (dmAttachmentsList != null && !dmAttachmentsList.isEmpty()) {
//            fileNo = dmAttachmentsList.get(0).getFileNo();
//        }
//
//        if (fileNo == null) {
//            fileNo = this.generateUUID();
//        }
//
//        short sno = 1; // sno는 1부터 시작
//        List<DmAttachments> result = new ArrayList<>();
//
//        for (DmAttachments dmAttachments : dmAttachmentsList) { // 파일 새로 추가
//            if (dmAttachments.getFileNo() == null) { // 기존 fileNo 없을시
//                dmAttachments.setFileNo(fileNo); // 파일들에 동일한 fileNo 설정
//                dmAttachments.setSno(sno++); // 각 파일에 대해 순차적인 sno 설정, 다음 파일의 sno 값 증가
//            } else { // 파일 수정
//                dmAttachments.setSno((short) (dmAttachmentsRepository.findMaxSnoByFileNo(dmAttachments.getFileNo()) + 1));
//            }
//
//            result.add(dmAttachmentsRepository.save(dmAttachments)); // 파일 저장
//        }
//
//        return result;
//    }
//    private String insertAttachmentsList(List<DmAttachments> dmAttachmentsList, String existingFileNo) {
//        String fileNo = existingFileNo != null ? existingFileNo : UUID.randomUUID().toString();
//        short sno = 1;
//
//        for (DmAttachments dmAttachments : dmAttachmentsList) { 	// 파일 새로 추가
//            if (dmAttachments.getFileNo() == null) { 				// 기존 fileNo 없을시
//                dmAttachments.setFileNo(fileNo); 					// 파일들에 동일한 fileNo 설정
//                dmAttachments.setSno(sno); 							// 각 파일에 대해 순차적인 sno 설정
//                sno++; 												// 다음 파일의 sno 값 증가
//            } else { // 파일 수정
//                dmAttachments.setSno((short) (dmAttachmentsRepository.findMaxSnoByFileNo(dmAttachments.getFileNo()) + 1));
//            }
//            dmAttachmentsRepository.save(dmAttachments); // 파일 저장
//        }
//        return fileNo;
//    }

    public List<DmAttachments> addDmAttachmentsList(List<DmAttachments> dmAttachmentsList, String existingFileNo) {
        String fileNo = StringUtils.isNotEmpty(existingFileNo) ? existingFileNo : generateUUID();
        short sno = 1;

        for (DmAttachments dmAttachments : dmAttachmentsList) {
            if (dmAttachments.getFileNo() == null) {
                dmAttachments.setFileNo(fileNo);
            } else {
                sno = dmAttachmentsRepository.findMaxSnoByFileNo(dmAttachments.getFileNo());
                sno += 1;
            }

            dmAttachments.setSno(sno++);

//            dmAttachmentsRepository.save(dmAttachments); // 파일 저장
            DmAttachments selectDm = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mappers.design.designAttachments.selectDmAttachments", dmAttachments);

            if (selectDm != null) {
                dmAttachments.setFileKey(selectDm.getFileKey());
                mybatisSession.update("kr.co.ideait.platform.gaiacairos.mappers.design.designAttachments.updateDmAttachments", dmAttachments);
            } else {
                dmAttachments.setFileKey( StringUtils.isEmpty(dmAttachments.getFileKey()) ? generateUUID() : dmAttachments.getFileKey());
                mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mappers.design.designAttachments.insertDmAttachments", dmAttachments);
            }
        }

        return dmAttachmentsList;
    }
}
