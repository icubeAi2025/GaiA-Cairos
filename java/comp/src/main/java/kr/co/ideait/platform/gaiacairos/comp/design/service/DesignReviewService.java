package kr.co.ideait.platform.gaiacairos.comp.design.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import kr.co.ideait.platform.gaiacairos.comp.design.helper.DesignHelper;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDesignReview;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDwg;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.designreview.DesignReviewForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.designreview.DesignReviewForm.DesignReviewListGet;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.designreview.DesignReviewMybatisParam.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Service
public class DesignReviewService extends AbstractGaiaCairosService {

    @Autowired
    DmDesignReviewRepository designRepository;

    @Autowired
    DmDesignPhaseRepository designPhaseRepository;

    @Autowired
    DmDesignScheduleRepository dmDesignScheduleRepository;

    @Autowired
    DmAttachmentsRepository dmAttachmentsRepository;

    @Autowired
    DmDwgRepository dmDwgRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    DesignReviewForm designReviewForm;

    @Autowired
    DesignHelper designHelper;




    /*
     * 설계단계 리스트 조회
     */
    public List<Map<String, ?>> getDsgnPhaseList(String cntrctNo, String dsgnPhaseCd) {
        MybatisInput input = MybatisInput.of().add("cntrctNo", cntrctNo)
                .add("dsgnPhaseCd", dsgnPhaseCd);
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designReview.getDsgnPhaseList", input);
    }

    /**
     * 설계 검토 상세 조회
     * @param designReviewListGet
     * @param searchInput
     * @param langInfo
     * @return
     */
    public List<DesignReviewListOutput> getDsgnDetail(DesignReviewListGet designReviewListGet, DsgnSearchInput searchInput,
                                                      String langInfo, String usrId) {

        MybatisInput input = MybatisInput.of().add("cntrctNo", designReviewListGet.getCntrctNo())
                .add("dsgnPhaseNo", designReviewListGet.getDsgnPhaseNo())
                .add("lang", langInfo)
                .add("pageable", designReviewListGet.getPageable())
                .add("searchInput", searchInput)
                .add("usrId", usrId)
                .add("rplyStatus", CommonCodeConstants.DSGN_REPLY_CODE_GROUP_CODE)
                .add("apprerStatus", CommonCodeConstants.DSGN_APPRER_CODE_GROUP_CODE)
                .add("backchkStatus", CommonCodeConstants.DSGN_BACKCHK_CODE_GROUP_CODE)
                .add("dsgnCd", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);
        input.setPageable(designReviewListGet.getPageable());


        List<DesignReviewListOutput> output = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designReview.getDsgnList", input);

        for (DesignReviewListOutput item : output) {
            // 결함 첨부파일 조회 및 매핑
            if (item.getAtchFileNo() != null) {
                List<DmAttachments> files = dmAttachmentsRepository.findByFileNoAndDltYn(item.getAtchFileNo(), "N");
                item.setFiles(files);
            }

            // 검토도서 조회 및 매핑
            if (item.getRvwDwgNo() != null) {
                DmDwg dmDwg = dmDwgRepository.findByDwgNoAndDltYn(item.getRvwDwgNo(), "N");

                if (dmDwg != null) {
                    DmAttachments rvwDwgFiles = dmAttachmentsRepository.findByFileNoAndSnoAndDltYn(dmDwg.getAtchFileNo(), dmDwg.getSno(), "N");
                    item.setRvwDwgFile(rvwDwgFiles);
                }
            }

            // 변경요청도서 조회 및 매핑
            if (item.getChgDwgNo() != null) {
                DmDwg dmDwg = dmDwgRepository.findByDwgNoAndDltYn(item.getChgDwgNo(), "N");

                if (dmDwg != null) {
                    DmAttachments chgDwgFiles = dmAttachmentsRepository.findByFileNoAndSnoAndDltYn(dmDwg.getAtchFileNo(), dmDwg.getSno(), "N");
                    item.setChgDwgFile(chgDwgFiles);
                }
            }

            // 답변 첨부파일 조회 및 매핑
            if (item.getRplyAtchNo() != null) {
                List<DmAttachments> files = dmAttachmentsRepository.findByFileNoAndDltYn(item.getRplyAtchNo(), "N");
                item.setReplyFiles(files);
            }

            // 답변 도서 조회 및 매핑
            if (item.getRplyDwgNo() != null) {
                DmDwg dmDwg = dmDwgRepository.findByDwgNoAndDltYn(item.getRplyDwgNo(), "N");

                if (dmDwg != null) {
                    DmAttachments rplyDwgFiles = dmAttachmentsRepository.findByFileNoAndSnoAndDltYn(dmDwg.getAtchFileNo(), dmDwg.getSno(), "N");
                    item.setRplyDwgFile(rplyDwgFiles);
                }
            }
        }

        return output;
    }

    /*
     * 설계 리스트 조회(Grid) - 페이징
     */
    public Page<DesignReviewListOutput> getDsgnListToGrid(MybatisInput input) {
        String confirm = "완료";
        String ing = "진행중";
        String end = "종료";

        input.add("confirm", confirm)
                .add("ing", ing)
                .add("end", end)
                .add("rplyStatus", CommonCodeConstants.DSGN_REPLY_CODE_GROUP_CODE)
                .add("apprerStatus", CommonCodeConstants.DSGN_APPRER_CODE_GROUP_CODE)
                .add("backchkStatus", CommonCodeConstants.DSGN_BACKCHK_CODE_GROUP_CODE)
                .add("dsgnCd", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);

        List<DesignReviewListOutput> output = mybatisSession
                .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designReview.getDsgnList", input);

        for (DesignReviewListOutput item : output) {
            // 결함 첨부파일 조회 및 매핑
            if (item.getAtchFileNo() != null) {
                List<DmAttachments> files = dmAttachmentsRepository.findByFileNoAndDltYn(item.getAtchFileNo(), "N");
                item.setFiles(files);
            }

            // 검토도서 조회 및 매핑
            if (item.getRvwDwgNo() != null) {
                DmDwg dmDwg = dmDwgRepository.findByDwgNoAndDltYn(item.getRvwDwgNo(), "N");

                if (dmDwg == null) {
//                    continue;
                }
                else {
                    DmAttachments rvwDwgFiles = dmAttachmentsRepository.findByFileNoAndSnoAndDltYn(dmDwg.getAtchFileNo(), dmDwg.getSno(), "N");
                    item.setRvwDwgFile(rvwDwgFiles);
                }
            }

            // 변경요청도서 조회 및 매핑
            if (item.getChgDwgNo() != null) {
                DmDwg dmDwg = dmDwgRepository.findByDwgNoAndDltYn(item.getChgDwgNo(), "N");

                if (dmDwg == null) {
//                    continue;
                }
                else {
                    DmAttachments chgDwgFiles = dmAttachmentsRepository.findByFileNoAndSnoAndDltYn(dmDwg.getAtchFileNo(), dmDwg.getSno(), "N");
                    item.setChgDwgFile(chgDwgFiles);
                }
            }

            // 답변 첨부파일 조회 및 매핑
            if (item.getRplyAtchNo() != null) {
                List<DmAttachments> files =
                        dmAttachmentsRepository.findByFileNoAndDltYn(item.getRplyAtchNo(), "N");
                item.setReplyFiles(files);
            }

            // 답변 도서 조회 및 매핑
            if (item.getRplyDwgNo() != null) {
                DmDwg dmDwg = dmDwgRepository.findByDwgNoAndDltYn(item.getRplyDwgNo(), "N");

                if (dmDwg == null) {
//                    continue;
                }
                else {
                    DmAttachments rplyDwgFiles = dmAttachmentsRepository.findByFileNoAndSnoAndDltYn(dmDwg.getAtchFileNo(), dmDwg.getSno(), "N");
                    item.setRplyDwgFile(rplyDwgFiles);
                }
            }
        }
        log.info("처리 이후의 output : {}", output);

        long totalCount = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designReview.getDsgnListCount", input);

        // Page 객체 생성하여 반환
        return new PageImpl<>(output, input.getPageable(), totalCount);
    }

    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public Map<String, Object> registDesignReview(DesignReviewForm.CreateUpdateDsgn dsgn, List<MultipartFile> files, Map<String, Object> params) throws JsonProcessingException {
        // 1. 결함정보
        DmDesignReview designReview = designReviewForm.toDesignReview(dsgn);

        if (params == null) {
            params = Maps.newHashMap();
        }

        final String transferPath = getUploadPathByWorkType(FileUploadType.DESIGN, designReview.getCntrctNo());
        FileService.FileMeta tempRvwPhotoFileMeta = null;
        FileService.FileMeta rvwPhotoFileMeta = null;
        FileService.FileMeta tempChgPhotoFileMeta = null;
        FileService.FileMeta chgPhotoFileMeta = null;

        DmDesignReview dmDesignReview = (DmDesignReview) params.get("dmDesignReview");
        String userId = "";

        if (dmDesignReview == null) {
            userId = UserAuth.get(true).getUsrId();

            String dsgnNo = designHelper.createDsgnNo(); //설계 단계 번호 생성.
            Integer dsgnSeq = designHelper.createDsgnSeq();
            designReview.setDsgnNo(dsgnNo);
            designReview.setDsgnSeq(dsgnSeq);
        } else {
            userId = dmDesignReview.getRgstrId();

            designReview.setDsgnNo(dmDesignReview.getDsgnNo());
            designReview.setDsgnSeq(dmDesignReview.getDsgnSeq());
        }

        designReview.setDltYn("N");
        designReview.setRgstrId(userId);
        designReview.setChgId(userId);

        // 2. 파일
        String fileNo = "";
        List<DmAttachments> attachmentsList = objectMapper.convertValue(params.get("dmAttachmentsList"), new TypeReference<List<DmAttachments>>() {});
        List<DmAttachments> dmAttachmentsList = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
            int i = 0;

            for (MultipartFile file : files) {
                FileService.FileMeta fileMeta = fileService.save(getUploadPathByWorkType(FileUploadType.DESIGN,designReview.getCntrctNo()), file);

                DmAttachments dmAttachments = new DmAttachments();

                if (attachmentsList != null && attachmentsList.size() == files.size()) {
                    dmAttachments.setFileKey(attachmentsList.get(i++).getFileKey());
                }

                dmAttachments.setFileNm(file.getOriginalFilename());
                dmAttachments.setFileDiskNm(fileMeta.getFileName());
                dmAttachments.setFileDiskPath(fileMeta.getDirPath());
                dmAttachments.setFileSize(fileMeta.getSize());
                dmAttachments.setDltYn("N"); // DB에서 기본값 세팅되면 코드 삭제
                dmAttachments.setFileHitNum((short)0); // DB에서 기본값 세팅되면 코드 삭제
                dmAttachments.setRgstrId(userId);
                dmAttachments.setChgId(userId);

                dmAttachmentsList.add(dmAttachments);
            }

            // 파일이 있을 때 경우 첨부파일 번호 포함 저장
//            dmAttachmentsList = designHelper.createDmAttachmentsList(dmAttachmentsList);
//            designReview.setAtchFileNo(dmAttachmentsList.get(0).getFileNo());
            designHelper.addDmAttachmentsList(dmAttachmentsList, fileNo);
            designReview.setAtchFileNo(dmAttachmentsList.get(0).getFileNo());

            // dsgnNo = designReviewService.createDesignReview(designReview).getDsgnNo();
        }

        // 3. 검토도서 사진
        DmAttachments rvwDwgAttach = (DmAttachments) params.getOrDefault("rvwDwgAttach", new DmAttachments());

        String rvwPhotoMeta = MapUtils.getString(params, "rvwPhotoMeta", null);

        if (rvwPhotoMeta != null) {
//            FileService.FileMeta fileMeta = fileService.save(getUploadPathByWorkType(FileUploadType.DESIGN,designReview.getCntrctNo()), rvwFile);
            tempRvwPhotoFileMeta = objectMapper.readValue(rvwPhotoMeta, FileService.FileMeta.class);
            rvwPhotoFileMeta = fileService.build(rvwPhotoMeta, String.format("%s/%s", uploadPath, transferPath));

            DmAttachments dmAttachments = new DmAttachments();
            dmAttachments.setFileKey(rvwDwgAttach.getFileKey());
            dmAttachments.setFileNm(rvwPhotoFileMeta.getOriginalFilename());
            dmAttachments.setFileDiskNm(rvwPhotoFileMeta.getFileName());
            dmAttachments.setFileDiskPath(rvwPhotoFileMeta.getDirPath());
            dmAttachments.setFileSize(rvwPhotoFileMeta.getSize());
            dmAttachments.setDltYn("N"); // DB에서 기본값 세팅되면 코드 삭제
            dmAttachments.setFileHitNum((short)0); // DB에서 기본값 세팅되면 코드 삭제
            dmAttachments.setRgstrId(userId);
            dmAttachments.setChgId(userId);

            rvwDwgAttach = designHelper.createDmAttachment(dmAttachments);
        }

        // 4. 변경요청도서 사진
        DmAttachments chgDwgAttach = (DmAttachments) params.getOrDefault("chgDwgAttach", new DmAttachments());

        String chgPhotoMeta = MapUtils.getString(params, "chgPhotoMeta", null);

        if (chgPhotoMeta != null) {
//            FileService.FileMeta fileMeta = fileService.save(getUploadPathByWorkType(FileUploadType.DESIGN,designReview.getCntrctNo()), chgFile);
            tempChgPhotoFileMeta = objectMapper.readValue(chgPhotoMeta, FileService.FileMeta.class);
            chgPhotoFileMeta = fileService.build(chgPhotoMeta, String.format("%s/%s", uploadPath, transferPath));

            DmAttachments dmAttachments = new DmAttachments();
            dmAttachments.setFileKey(chgDwgAttach.getFileKey());
            dmAttachments.setFileNm(chgPhotoFileMeta.getOriginalFilename());
            dmAttachments.setFileDiskNm(chgPhotoFileMeta.getFileName());
            dmAttachments.setFileDiskPath(chgPhotoFileMeta.getDirPath());
            dmAttachments.setFileSize(chgPhotoFileMeta.getSize());
            dmAttachments.setDltYn("N"); // DB에서 기본값 세팅되면 코드 삭제
            dmAttachments.setFileHitNum((short)0); // DB에서 기본값 세팅되면 코드 삭제
            dmAttachments.setRgstrId(userId);
            dmAttachments.setChgId(userId);

            chgDwgAttach = designHelper.createDmAttachment(dmAttachments);
        }

//        List<DmDwg> dwgList = (List<DmDwg>) params.getOrDefault("dmDwgList", new ArrayList<>());
        List<DmDwg> dmDwgList = new ArrayList<>();

        // 5. 설계 도면(Dm_Dwg) 정보 저장
        if (!dsgn.getDwgs().isEmpty()) {
            for(int i = 0; i < dsgn.getDwgs().size(); i++) {
                DmDwg dmDwg = designReviewForm.toDmDwg(dsgn.getDwgs().get(i));

                String dwgNo = designHelper.generateUUID();
                dmDwg.setDwgNo(dwgNo);
                dmDwg.setDltYn("N");
                dmDwg.setRgstrId(userId);
                dmDwg.setChgId(userId);

                // 검토 도서, 변경 요청 도서 구분하여 설정.
                if("0501".equals(dmDwg.getDwgCd())){        // 검토 도서
                    dmDwg.setAtchFileNo(rvwDwgAttach.getFileNo());
                    dmDwg.setSno(rvwDwgAttach.getSno());
                    dmDwg.setFileKey(rvwDwgAttach.getFileKey());

                    designReview.setRvwDwgNo(dmDwg.getDwgNo());
                }
                else if("0502".equals(dmDwg.getDwgCd())){   // 변경 요청 도서
                    dmDwg.setAtchFileNo(chgDwgAttach.getFileNo());
                    dmDwg.setSno(chgDwgAttach.getSno());
                    dmDwg.setFileKey(chgDwgAttach.getFileKey());

                    designReview.setChgDwgNo(dmDwg.getDwgNo());
                }

                dmDwgList.add(this.createUpdateDmDwg(dmDwg));
            }
        }

        designReview = this.createDesignReview(designReview);

        if (rvwPhotoFileMeta != null) {
            fileService.moveFile(tempRvwPhotoFileMeta.getFilePath(), rvwPhotoFileMeta.getFilePath());
        }

        if (chgPhotoFileMeta != null) {
            fileService.moveFile(tempChgPhotoFileMeta.getFilePath(), chgPhotoFileMeta.getFilePath());
        }

        Map<String, Object> result = Maps.newHashMap();
        result.put("designReview", designReview);
        result.put("dmAttachmentsList", dmAttachmentsList);
        result.put("rvwDwgAttach", rvwDwgAttach);
        result.put("chgDwgAttach", chgDwgAttach);
        result.put("dmDwgList", dmDwgList);

        return result;
    }

    /**
     * 설계 검토 생성
     * @param designReview
     * @return
     */
    private DmDesignReview createDesignReview(DmDesignReview designReview) {
        DmDesignReview review = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designReview.selectDesignReview", designReview);

        if (review == null) {
//            review = designRepository.save(designReview);
            mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designReview.insertDesignReview", designReview);
        } else {
            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designReview.updateDesignReview", designReview);
        }
        review = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designReview.selectDesignReview", designReview);

        return review;
    }

    /**
     * 설계 도면 생성
     * @param dmDwg
     */
    private DmDwg createUpdateDmDwg(DmDwg dmDwg) {
        DmDwg newDwg = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designReview.selectDmDwg", dmDwg);

        if (newDwg == null) {
//            newDwg = dmDwgRepository.save(dmDwg);
            mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designReview.insertDmDwg", dmDwg);
            newDwg = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designReview.selectDmDwg", dmDwg);
        } else {
            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designReview.updateDmDwg", dmDwg);
        }

        return newDwg;
    }

    /**
     * 설계 검토 수정 - 설계 검토 조회
     */
    public DsgnUpdateOutPut getDesignReview(String cntrctNo, String dsgnPhaseNo, String dsgnNo, String lang) {
        MybatisInput input = MybatisInput.of().add("cntrctNo", cntrctNo)
                .add("dsgnPhaseNo", dsgnPhaseNo)
                .add("dsgnNo", dsgnNo)
                .add("dsgnCd", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE)
                .add("lang", lang);
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designReview.getDesignReview", input);
    }

    /**
     * 설계 검토 수정 - 설계 첨부파일 조회
     *
     * @param fileNo
     * @return
     */
    public List<DmAttachments> getFileList(String fileNo) {
        return dmAttachmentsRepository.findByFileNoAndDltYn(fileNo, "N");
    }

    /**
     * 설계 검토 수정 - 설계 도면 이미지파일 조회
     *
     * @param fileNo
     * @return
     */
    public DmAttachments getDwgFile(String fileNo, Short sno) {
        return dmAttachmentsRepository.findByFileNoAndSnoAndDltYn(fileNo, sno, "N");
    }

    /**
     * 설계 검토 엔터티 조회
     * @param cntrctNo
     * @param dsgnNo
     * @return
     */
    public DmDesignReview getDesignReview(String cntrctNo, String dsgnNo) {
        return designRepository.findByCntrctNoAndDsgnNoAndDltYn(cntrctNo, dsgnNo, "N");
    }

    /**
     * 첨부파일 삭제
     */
    private void deleteDmAttachments(String fileNo, String fileKey, String userId) {
        DmAttachments dmAttachments = dmAttachmentsRepository.findByFileNoAndFileKey(fileNo, fileKey);

        if (dmAttachments != null) {
            dmAttachmentsRepository.updateDelete(dmAttachments, userId);
        }
    }

    /**
     * 설계 도면 조회
     * @return
     */
    public DmDwg getDmDwg(String dwgNo, String dwgCd) {
        return dmDwgRepository.findByDwgNoAndDwgCdAndDltYn(dwgNo, dwgCd, "N");
    }

    /**
     * 설계 도면 삭제
     */
    public void deleteDmDwg(DmDwg dmDwg, String userId) {
        if (userId == null) {
            dmDwgRepository.updateDelete(dmDwg);
        } else {
            dmDwgRepository.updateDelete(dmDwg, userId);
        }
    }

    /**
     * 설계 검토 삭제
     * @param dsgnNoList
     */
    public void deleteDesignReviewList(List<String> dsgnNoList) {
        deleteDesignReviewList(dsgnNoList, null);
    }
    public void deleteDesignReviewList(List<String> dsgnNoList, String userId) {
        designRepository.findAllById(dsgnNoList).forEach(design -> {
            if (userId == null) {
                designRepository.updateDelete(design);
            } else {
                designRepository.updateDelete(design, userId);
            }
        });
    }

    /**
     * 설계 검토 수정
     * @param updateDsgn
     * @return
     */
    public Map<String, Object> updateDesignReview(DesignReviewForm.CreateUpdateDsgn updateDsgn, Map<String, Object> params) throws JsonProcessingException {

        if (params == null) {
            params = new HashMap();
        }

        String userId = (String) params.get("userId");
        String atchFileNo = (String) params.get("atchFileNo");

        if (StringUtils.isEmpty(userId)) {
            userId = UserAuth.get(true).getUsrId();
        }

        DmDesignReview oldDesignReview = getDesignReview(updateDsgn.getCntrctNo(), updateDsgn.getDsgnNo());
        // 설계 검토 입력 데이터 수정
        oldDesignReview.setAtchFileNo(atchFileNo);
        designReviewForm.updateDesignReview(updateDsgn, oldDesignReview);

        // 파일 삭제 처리
//        if (removedFiles != null && !removedFiles.isEmpty()) {
//            for (int i = 0; i < removedFiles.size(); i++) {
//                DmAttachments removedFile = removedFiles.get(i);
//                removedFile.setChgId(userId);
//                removedFile.setDltId(userId);
//                mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designReview.deleteDesignReviewAttachment",removedFile);
//            }
//        }

        // 새 파일 추가 처리
        //API로 넘겨받은 파일 정보들
//        List<DmAttachments> attachmentsList = objectMapper.convertValue(params.get("dmAttachmentsList"), new TypeReference<List<DmAttachments>>() {});
//        //로직을 위한 List
//        List<DmAttachments> dmAttachmentsList = new ArrayList<>();
//
//        if (newFiles != null && !newFiles.isEmpty()) {
//            int i = 0;
//
//            String fileNo = StringUtils.defaultString(oldDesignReview.getAtchFileNo(), designHelper.generateUUID());
//            oldDesignReview.setAtchFileNo(fileNo);
//            for (MultipartFile file : newFiles) {
//                //이건 방금 저장된 파일 정보
//                FileMeta fileMeta = fileService.save(getUploadPathByWorkType(FileUploadType.DESIGN,updateDsgn.getCntrctNo()), file);
//
//
//
//                //이건 로직을 위한 파일 DTO
//                DmAttachments dmAttachments = new DmAttachments();
//                dmAttachments.setFileNo(fileNo);
//                //넘겨받은 파일 정보들이 있다면
//                if (attachmentsList != null && attachmentsList.size() == newFiles.size()) {
//                    //이건 API로 넘겨받은 저장된 파일 정보
//                    DmAttachments savedFileMeta = attachmentsList.get(i);
//                    dmAttachments.setFileKey(savedFileMeta.getFileKey());
//                }
//                else{
//                    dmAttachments.setFileKey(UUID.randomUUID().toString());
//                }
//
//                dmAttachments.setFileNm(file.getOriginalFilename());
//                dmAttachments.setFileDiskNm(fileMeta.getFileName());
//                dmAttachments.setFileDiskPath(fileMeta.getDirPath());
//                dmAttachments.setFileSize(fileMeta.getSize());
//                dmAttachments.setDltYn("N");
//                dmAttachments.setFileHitNum((short) 0);
//                dmAttachments.setRgstrId(userId);
//                dmAttachments.setChgId(userId);
//
//                short sno = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designReview.selectMaxSno",fileNo);
//
//                dmAttachments.setSno((short)(sno+1));
//
//                mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designReview.insertDesignReviewAttachment",dmAttachments);
//                dmAttachmentsList.add(dmAttachments);
//                i++;
//            }
//        }

        oldDesignReview.setRgstrId(userId);
        oldDesignReview.setChgId(userId);

        // 검토도서, 변경요청 사진 삭제 처리
        handleFileDeletion(updateDsgn, oldDesignReview, userId);

        // 검토도서 사진 추가 처리

        String transferPath = getUploadPathByWorkType(FileUploadType.DESIGN, updateDsgn.getCntrctNo());

        FileService.FileMeta tempRvwPhotoFileMeta = null;
        FileService.FileMeta rvwPhotoFileMeta = null;
        DmAttachments rvwDwgAttach = null;

        String rvwPhotoMeta = MapUtils.getString(params, "rvwPhotoMeta");

        if (!StringUtils.isEmpty(rvwPhotoMeta)) {
            tempRvwPhotoFileMeta = objectMapper.readValue(rvwPhotoMeta, FileService.FileMeta.class);
            rvwPhotoFileMeta = fileService.build(rvwPhotoMeta, String.format("%s/%s", uploadPath, transferPath));

            rvwDwgAttach = handleFileAddition(rvwPhotoFileMeta, "0501", userId);
        }

        // 변경요청도서 사진 추가 처리
        FileService.FileMeta tempChgPhotoFileMeta = null;
        FileService.FileMeta chgPhotoFileMeta = null;
        DmAttachments chgDwgAttach = null;

        String chgPhotoMeta = MapUtils.getString(params, "chgPhotoMeta");

        if (!StringUtils.isEmpty(chgPhotoMeta)) {
            tempChgPhotoFileMeta = objectMapper.readValue(chgPhotoMeta, FileService.FileMeta.class);
            chgPhotoFileMeta = fileService.build(chgPhotoMeta, String.format("%s/%s", uploadPath, transferPath));
            chgDwgAttach = handleFileAddition(chgPhotoFileMeta, "0502", userId);
        }

        // 설계 도면 정보 저장
        saveDesignDrawings(updateDsgn, oldDesignReview, rvwDwgAttach, chgDwgAttach, userId);



        if (rvwPhotoFileMeta != null) {
            Path source = Paths.get(tempRvwPhotoFileMeta.getFilePath());

            if (Files.exists(source)) {
                fileService.moveFile(tempRvwPhotoFileMeta.getFilePath(), rvwPhotoFileMeta.getFilePath());
            }
        }

        if (chgPhotoFileMeta != null) {
            Path source = Paths.get(tempChgPhotoFileMeta.getFilePath());

            if (Files.exists(source)) {
                fileService.moveFile(tempChgPhotoFileMeta.getFilePath(), chgPhotoFileMeta.getFilePath());
            }
        }

        Map<String, Object> result = Maps.newHashMap();
        result.put("updateDsgn", createDesignReview(oldDesignReview));
//        result.put("dmAttachmentsList", dmAttachmentsList);
        result.put("rvwDwgAttach", rvwDwgAttach);
        result.put("chgDwgAttach", chgDwgAttach);
//        result.put("dmDwgList", dmDwgList);

        return result;
    }

    /**
     * 설계 검토 도면 삭제
     */
    private void handleFileDeletion(DesignReviewForm.CreateUpdateDsgn updateDsgn, DmDesignReview oldDesignReview, String userId) {

        if (updateDsgn.getDeleteRvwFileNo() != null && updateDsgn.getDeleteRvwFileKey() != null) {
            DmDwg oldRvwDmDwg = getDmDwg(oldDesignReview.getRvwDwgNo(), "0501");

            if (oldRvwDmDwg != null) {
                deleteDmDwg(oldRvwDmDwg, userId);
                deleteDmAttachments(updateDsgn.getDeleteRvwFileNo(), updateDsgn.getDeleteRvwFileKey(), userId);
                oldDesignReview.setRvwDwgNo(null);
            } else {
                //오류아님. 미첨부시 없을수 있음.
//                throw new GaiaBizException(ErrorType.NO_DATA, "검토 설계 도면 정보가 없습니다.");
            }
        }

        if (updateDsgn.getDeleteChgFileNo() != null && updateDsgn.getDeleteChgFileKey() != null) {
            DmDwg oldChgDmDwg = getDmDwg(oldDesignReview.getChgDwgNo(), "0502");

            if (oldChgDmDwg != null) {
                deleteDmDwg(oldChgDmDwg, userId);
                deleteDmAttachments(updateDsgn.getDeleteChgFileNo(), updateDsgn.getDeleteChgFileKey(), userId);
                oldDesignReview.setChgDwgNo(null);
            } else {
                //오류아님. 미첨부시 없을수 있음.
//                throw new GaiaBizException(ErrorType.NO_DATA, "변경요청 설계 도면 정보가 없습니다.");
            }
        }
    }

    /**
     * 파일 추가
     * @param fileMeta
     * @param dwgCd
     * @param userId
     * @return
     */
    private DmAttachments handleFileAddition(FileService.FileMeta fileMeta, String dwgCd, String userId) {
        if (fileMeta == null) {
            return null;
        }

        DmAttachments dmAttachments = new DmAttachments();
        dmAttachments.setFileNm(fileMeta.getOriginalFilename());
        dmAttachments.setFileDiskNm(fileMeta.getFileName());
        dmAttachments.setFileDiskPath(fileMeta.getDirPath());
        dmAttachments.setFileSize(fileMeta.getSize());
        dmAttachments.setDltYn("N");

        if (userId == null) {
            dmAttachments.setRgstrId(UserAuth.get(true).getUsrId());
            dmAttachments.setChgId(UserAuth.get(true).getUsrId());
        } else {
            dmAttachments.setRgstrId(userId);
            dmAttachments.setChgId(userId);
        }

        dmAttachments.setFileHitNum((short) 0);

        return designHelper.createDmAttachment(dmAttachments);
    }


    /**
     * 설계 도면 정보 저장
     * @param updateDsgn
     * @param oldDesignReview
     * @param rvwDwgAttach
     * @param chgDwgAttach
     */
    private void saveDesignDrawings(DesignReviewForm.CreateUpdateDsgn updateDsgn, DmDesignReview oldDesignReview, DmAttachments rvwDwgAttach, DmAttachments chgDwgAttach, String userId) {
        if (updateDsgn.getDwgs() == null||updateDsgn.getDwgs().isEmpty()) {
            return;
        }

        for (DesignReviewForm.Dwg dwg : updateDsgn.getDwgs()) {
            DmDwg dmDwg = designReviewForm.toDmDwg(dwg);
            dmDwg.setSno(null);
            dmDwg.setDwgNo(designHelper.generateUUID());
            dmDwg.setDltYn("N");

            if (userId == null) {
                dmDwg.setRgstrId(UserAuth.get(true).getUsrId());
                dmDwg.setChgId(UserAuth.get(true).getUsrId());
            } else {
                dmDwg.setRgstrId(userId);
                dmDwg.setChgId(userId);
            }

            if ("0501".equals(dmDwg.getDwgCd()) && rvwDwgAttach != null) {
                dmDwg.setAtchFileNo(rvwDwgAttach.getFileNo());
                dmDwg.setSno(rvwDwgAttach.getSno());
                oldDesignReview.setRvwDwgNo(dmDwg.getDwgNo());
            } else if ("0502".equals(dmDwg.getDwgCd()) && chgDwgAttach != null) {
                dmDwg.setAtchFileNo(chgDwgAttach.getFileNo());
                dmDwg.setSno(chgDwgAttach.getSno());
                oldDesignReview.setChgDwgNo(dmDwg.getDwgNo());
            }

            if (dmDwg.getSno() != null) {
                createUpdateDmDwg(dmDwg);
            }
        }
    }

    /**
     * 평가 데이터 조회
     * @param dsgnNo
     * @param cntrctNo
     * @return
     */
    public List<DsgnApprerOutput> getApprerData(String dsgnNo, String cntrctNo, String lang) {
        MybatisInput input = MybatisInput.of().add("dsgnNo", dsgnNo)
                .add("cntrctNo", cntrctNo)
                .add("lang", lang)
                .add("apprerStatus", CommonCodeConstants.DSGN_APPRER_CODE_GROUP_CODE);
        List<DsgnApprerOutput> output = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designReview.getApprerDataList", input);

        for (DsgnApprerOutput item : output) {
            // 결함 첨부파일 조회 및 매핑
            if (item.getApprerAtchFileNo() != null) {
                List<DmAttachments> files = dmAttachmentsRepository.findByFileNoAndDltYn(item.getApprerAtchFileNo(), "N");
                item.setApprerFiles(files);
            }
        }

        return output;
    }

    /**
     * 백체크 데이터 조회
     * @param dsgnNo
     * @param cntrctNo
     * @return
     */
    public List<DsgnBackchkOutput> getBackchkData(String dsgnNo, String cntrctNo, String lang) {
        MybatisInput input = MybatisInput.of().add("dsgnNo", dsgnNo)
                .add("cntrctNo", cntrctNo)
                .add("lang", lang)
                .add("backchkStatus", CommonCodeConstants.DSGN_BACKCHK_CODE_GROUP_CODE);
        List<DsgnBackchkOutput> output = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designReview.getBackchkDataList", input);

        for (DsgnBackchkOutput item : output) {
            // 결함 첨부파일 조회 및 매핑
            if (item.getBackchkAtchFileNo() != null) {
                List<DmAttachments> files = dmAttachmentsRepository.findByFileNoAndDltYn(item.getBackchkAtchFileNo(), "N");
                item.setBackchkFiles(files);
            }
        }

        return output;
    }
}
