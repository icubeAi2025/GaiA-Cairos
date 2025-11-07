package kr.co.ideait.platform.gaiacairos.comp.system.service;

import com.fasterxml.jackson.core.type.TypeReference;
import kr.co.ideait.iframework.BizException;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBoard;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBoardReception;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmPopupMsg;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmBoardReceptionRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmBoardRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmPopupMsgRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardMybatisParam.BoardListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardMybatisParam.BoardOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardMybatisParam.BoardViewInput;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.FileService.FileMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService extends AbstractGaiaCairosService {
    @Autowired
    SmBoardRepository smBoardRepository;

    @Autowired
    SmBoardReceptionRepository smBoardReceptionRepository;

    @Autowired
    SmAttachmentsRepository smAttachmentsRepository;

    @Autowired
    SmPopupMsgRepository smPopupMsgRepository;

    @Autowired
    FileService fileService;

    @Autowired
    BoardForm boardForm;

    @Value("${link.domain.url}")
    private String domainUrl;


    /**
     * 파일 업로드 경로
     */
    String baseDirPath = FileUploadType.SYSTEM.getDirPath(); // 기본 디렉토리 경로 생성

    String cmnGrpCdCategory = CommonCodeConstants.FAQ_CODE_GROUP_CODE;

    // 게시글 목록 조회
    @SuppressWarnings("null")
    public Page<BoardOutput> getBoardList(BoardListInput boardListInput) {
        MybatisInput input = new MybatisInput();
        input.add("userId", boardListInput.getUserId());

        List<Map<String, Object>> projectList;
        if ("ADMIN".equals(boardListInput.getUserType()) || "관리자".equals(boardListInput.getUserType())) {
            projectList = mybatisSession
                    .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.board.selectAdminGAIAProjectList");
        } else {
            if ("PGAIA".equals(platform.toUpperCase()) || "GAIA".equals(platform.toUpperCase())) {
                projectList = mybatisSession
                        .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.board.selectGAIAUserProjectList", input);
            } else {
                projectList = mybatisSession
                        .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.board.selectCMISUserProjectList", input);
                List<String> pjtNoList = projectList.stream()
                        .map(project -> (String) project.get("pjt_no"))
                        .collect(Collectors.toList());
                input.add("pjtNoList", pjtNoList);

                projectList = mybatisSession
                        .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.board.selectCMISUserCntrctList", input);
            }

        }

        List<String> pjtNoList = projectList.stream()
                .map(project -> (String) project.get("pjt_no"))
                .collect(Collectors.toList());
        List<String> cntrctNoList = projectList.stream()
                .map(cntrct -> (String) cntrct.get("cntrct_no"))
                .collect(Collectors.toList());

        boardListInput.setPjtNoList(pjtNoList);
        boardListInput.setCntrctNoList(cntrctNoList);
        boardListInput.setSystemType(platform.toUpperCase());

        List<BoardOutput> boardOutputs = null;

        boardOutputs = mybatisSession
                .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.board.getBoardList", boardListInput);

        Long totalCount = 0L;
        if (boardOutputs != null) {
            if(!boardOutputs.isEmpty()){
                totalCount = boardOutputs.getFirst().getTotalNum();
            }
            return new PageImpl<>(boardOutputs, boardListInput.getPageable(), totalCount);
        }
        return null;
    }

    // 게시글 조회
    public SmBoard getBoard(Integer boardNo) {
        return smBoardRepository.findByBoardNo(boardNo).orElse(null);
    }

    public List getSmBoardReceptionList(String boardCd) {
        return smBoardReceptionRepository.findByBoardCdAndDltYn(boardCd, "N");
    }

    // 게시글 추가
    @Transactional
    public SmBoard createBoard(BoardForm.Board board, List<MultipartFile> files, CommonReqVo commonReqVo) {
        String[] userParam = commonReqVo.getUserParam();

        SmBoard smBoard = boardForm.toBoard(board);

        if (StringUtils.isEmpty(smBoard.getRgstrId())) {
            smBoard.setRgstrId(userParam[3]);
            smBoard.setChgId(userParam[3]);
        }

        List<SmBoardReception> receptionList = new ArrayList<>();
        List<SmPopupMsg> popupList = new ArrayList<>();

        //시스템 전체 수신일 때
        if ("1".equals(board.getBoardDiv())) {
            SmBoardReception smBoardReception = boardForm.toBoardReception(board);
            smBoardReception.setPjtType(userParam[2]);
            smBoardReception.setBoardView(0);
            receptionList.add(smBoardReception);

            if (("Y").equals(board.getPopupYn())) {
                SmPopupMsg smPopupMsg = boardForm.toBoardPopupMsg(board);
                smPopupMsg.setPopDiv(board.getBoardDiv());
                smPopupMsg.setPjtType(userParam[2]);
                popupList.add(smPopupMsg);
            }
        }
        //프로젝트, 계약 수신일 때
        else {
            //수신 목록만큼 돌며
            for (BoardForm.ReceptionItem item : board.getReceptionList()) {
                SmBoardReception smBoardReception = boardForm.toBoardReception(board);
                smBoardReception.setPjtType(userParam[2]);
                smBoardReception.setBoardView(0);
                //프로젝트 번호 채우기
                smBoardReception.setPjtNo(item.getPjtNo());
                //계약 수신인 경우
                if ("3".equals(board.getBoardDiv())) {
                    //계약번호 채우기
                    smBoardReception.setCntrctNo(item.getCntrctNo());
                }
                //receptionList에 만들어진 아이템 추가
                receptionList.add(smBoardReception);

                if (("Y").equals(board.getPopupYn())) {
                    SmPopupMsg smPopupMsg = boardForm.toBoardPopupMsg(board);
                    smPopupMsg.setPopDiv(board.getBoardDiv());
                    smPopupMsg.setPjtType(userParam[2]);
                    smPopupMsg.setPjtNo(item.getPjtNo());
                    if ("3".equals(board.getBoardDiv())) {
                        smPopupMsg.setCntrctNo(item.getCntrctNo());
                    }
                    popupList.add(smPopupMsg);
                }
            }
        }
        return this.createBoard(smBoard, receptionList, popupList, files, userParam[3]);
    }

    @Transactional
    public SmBoard createBoard(SmBoard smBoard, List<SmBoardReception> smBoardReceptionList, List<SmPopupMsg> popupList, List<MultipartFile> files, String userId) {

        String fullPath = null;
        if("1".equals(smBoard.getBoardType())){
            fullPath = getUploadPathByWorkType(FileUploadType.NOTICE, userId);
        }
        else{
            fullPath = getUploadPathByWorkType(FileUploadType.FAQ, userId);
        }

        if ( StringUtils.isEmpty(smBoard.getBoardCd()) ) {
            smBoard.setBoardCd(UUID.randomUUID().toString());
        }

        if (StringUtils.isEmpty(smBoard.getDltYn())) {
            smBoard.setDltYn("N");
        }

        SmBoard savedBoard = smBoardRepository.save(smBoard);

        // 게시글 수신
//        List<SmBoardReception> savedBoardReceptions = new ArrayList<>();

        for (SmBoardReception smBoardReception : smBoardReceptionList) {
            if (StringUtils.isEmpty(smBoardReception.getRgstrId())) {
                smBoardReception.setRgstrId(smBoard.getRgstrId());
                smBoardReception.setChgId(smBoard.getChgId());
            }

            SmBoardReception savedBoardReception = createBoardReception(savedBoard.getBoardCd(), smBoardReception);
//            savedBoardReceptions.add(savedBoardReception);
        }

        // 팝업창
//        List<SmPopupMsg> savedPopups = new ArrayList<>();

        for (SmPopupMsg smPopupMsg : popupList) {
            if (StringUtils.isEmpty(smPopupMsg.getRgstrId())) {
                smPopupMsg.setRgstrId(smBoard.getRgstrId());
            }

            SmPopupMsg savedPopup = createPopupMsg(savedBoard, smPopupMsg);
//            savedPopups.add(savedPopup);
        }

        // 첨부파일
        List<SmAttachments> smAttachmentsList = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                // 파일 이름이 비어있거나 null인 경우 건너뛰기
                if (fileName == null || fileName.trim().isEmpty()) {
                    continue;
                }
                FileMeta fileMeta = fileService.save(fullPath, file);

                SmAttachments smAttachments = new SmAttachments();
                smAttachments.setBoardCd(savedBoard.getBoardCd());
                smAttachments.setFileOrgNm(file.getOriginalFilename());
                smAttachments.setFileDiskNm(fileMeta.getFileName());
                smAttachments.setFileDiskPath(fileMeta.getDirPath());
                smAttachments.setFileSize(fileMeta.getSize());
                smAttachments.setDltYn("N");

                if (StringUtils.isEmpty(smAttachments.getRgstrId())) {
                    smAttachments.setRgstrId(smBoard.getRgstrId());
                    smAttachments.setChgId(smBoard.getChgId());
                }

                smAttachmentsList.add(smAttachments);
            }

            createSmAttachmentsList(smAttachmentsList);
        }

        return savedBoard;
    }

    // 게시글 수신 추가
    @Transactional
    public SmBoardReception createBoardReception(String boardCd, SmBoardReception smBoardReception) {
        smBoardReception.setBoardCd(boardCd);
        if (smBoardReception.getDltYn() == null) {
            smBoardReception.setDltYn("N");
        }
        return smBoardReceptionRepository.save(smBoardReception);
    }

    // 게시글 팝업 추가
    @Transactional
    public SmPopupMsg createPopupMsg(SmBoard smBoard, SmPopupMsg smPopupMsg) {
        smPopupMsg.setPopMsgCd(smBoard.getBoardCd());
        smPopupMsg.setPopTitle(smBoard.getBoardTitle());
        smPopupMsg.setPopContent(smBoard.getBoardTxt());
        smPopupMsg.setUseYn("Y");
        smPopupMsg.setDltYn("N");
        return smPopupMsgRepository.save(smPopupMsg);
    }

    // 게시글 수정
    @Transactional
    public SmBoard updateBoard(SmBoard smBoard, List<SmBoardReception> smBoardReceptionList,
                               List<SmPopupMsg> smPopupMsgList, List<MultipartFile> files, String preShareYn, String userId) {
        String fullPath = null;
        if(smBoard != null) {
            if("1".equals(smBoard.getBoardType())){
                fullPath = getUploadPathByWorkType(FileUploadType.NOTICE, userId);
            }
            else{
                fullPath = getUploadPathByWorkType(FileUploadType.FAQ, userId);
            }
            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.board.updateBoard", smBoard);

            SmBoard savedBoard = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.board.selectBoard", smBoard);

            // 게시판 수신 수정
            updateBoardReception(savedBoard.getBoardCd(), smBoardReceptionList);

            // 팝업 수정
            updatePopupMsg(savedBoard, smPopupMsgList);

            // 첨부파일 수정
            List<SmAttachments> newSmAttachments = new ArrayList<>(); // 화면에서 넘어온 전체 파일
            List<SmAttachments> addSmAttachments = new ArrayList<>(); // db에 저장할 파일
            List<SmAttachments> getSmAttachments = getSmAttachments(smBoard.getBoardCd()); // db에 있는 파일


            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {

                    String[] param = StringUtils.defaultString(file.getOriginalFilename()).split(":");
                    if(param != null && param.length > 0){
                        String fileName = param[0];
                        if (fileName == null || fileName.trim().isEmpty()) {
                            continue;
                        }
                        FileMeta fileMeta = fileService.save(fullPath, file);
                        String fileDiskName;

                        if (param.length > 1) {
                            fileDiskName = param[1];
                        } else {
                            fileDiskName = fileMeta.getFileName();
                        }

                        boolean fileExists = getSmAttachments.stream()
                                .anyMatch(attachment -> attachment.getFileDiskNm().equals(fileDiskName));

                        SmAttachments smAttachments = new SmAttachments();
                        smAttachments.setFileOrgNm(fileName);
                        smAttachments.setBoardCd(smBoard.getBoardCd());
                        smAttachments.setFileDiskNm(fileDiskName);
                        smAttachments.setFileDiskPath(fileMeta.getDirPath());
                        smAttachments.setFileSize(fileMeta.getSize());
                        smAttachments.setDltYn("N");

                        if (StringUtils.isEmpty(smAttachments.getRgstrId())) {
                            smAttachments.setRgstrId(smBoard.getRgstrId());
                            smAttachments.setChgId(smBoard.getChgId());
                        }

                        if (!fileExists) {
                            addSmAttachments.add(smAttachments);
                        }
                        newSmAttachments.add(smAttachments);
                    }
                }
                if (!addSmAttachments.isEmpty()) {
                    createSmAttachmentsList(addSmAttachments);
                }
            }

            for (SmAttachments attachment : getSmAttachments) {
                // newSmAttachments에서 fileDiskNm만 추출하여 비교
                boolean fileExistsInNewList = newSmAttachments.stream()
                        .anyMatch(newAttachment -> newAttachment.getFileDiskNm().equals(attachment.getFileDiskNm()));

                if (!fileExistsInNewList) {
                    attachment.setDltYn("Y");

                    if (StringUtils.isEmpty(attachment.getRgstrId())) {
                        attachment.setRgstrId(smBoard.getRgstrId());
                        attachment.setChgId(smBoard.getChgId());
                    }

                    deleteSmAttachments(attachment); // 없어진 파일 삭제
                }
            }
            return savedBoard;
        }
        return null;
    }

    // 게시글 수신 수정
    private void updateBoardReception(String boardCd, List<SmBoardReception> smBoardReceptionList) {
        List<SmBoardReception> existingReceptions = smBoardReceptionRepository
                .findByBoardCdAndDltYn(boardCd, "N");

        Map<String, SmBoardReception> existingMap = existingReceptions.stream()
                .collect(Collectors.toMap(
                        reception -> reception.getPjtNo() + "|" + reception.getBoardDiv() + "|"
                                + reception.getCntrctNo(),
                        reception -> reception));
        String dltId = null;
        for (SmBoardReception newReception : smBoardReceptionList) {
            String key = newReception.getPjtNo() + "|" + newReception.getBoardDiv() + "|" + newReception.getCntrctNo();

            if (existingMap.containsKey(key)) {
                existingMap.remove(key);
            } else {
                SmBoardReception createdBoardReception = createBoardReception(boardCd, newReception);
                dltId = createdBoardReception.getChgId();
            }
        }
        for (SmBoardReception reception : existingMap.values()) {
            HashMap<String,Object> datas = new HashMap<>();
            datas.put("dltId",dltId);
            datas.put("receSeq",reception.getReceSeq());
            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.board.deleteSmBoardReception",datas);
        }
    }

    // 팝업 수정
    private void updatePopupMsg(SmBoard smboard, List<SmPopupMsg> smPopupMsgList) {
        List<SmPopupMsg> existingReceptions = smPopupMsgRepository
                .findByPopMsgCdAndDltYn(smboard.getBoardCd(), "N");

        Map<String, SmPopupMsg> existingMap = existingReceptions.stream()
                .collect(Collectors.toMap(
                        popup -> popup.getPjtNo() + "|" + popup.getPopDiv() + "|"
                                + popup.getCntrctNo(),
                        popup -> popup));

        for (SmPopupMsg newReception : smPopupMsgList) {
            String key = newReception.getPjtNo() + "|" + newReception.getPopDiv() + "|" + newReception.getCntrctNo();

            if (existingMap.containsKey(key)) {
                SmPopupMsg existing = existingMap.get(key);

                existing.setPopTitle(smboard.getBoardTitle());
                existing.setPopContent(smboard.getBoardTxt());
                existing.setPopStartDt(newReception.getPopStartDt());
                existing.setPopEndDt(newReception.getPopEndDt());

                smPopupMsgRepository.save(existing);

                existingMap.remove(key);
            } else {
                createPopupMsg(smboard, newReception);
            }
        }
        for (SmPopupMsg reception : existingMap.values()) {
            smPopupMsgRepository.updateDelete(reception);
        }
    }

    public BoardOutput getUpdateData(Integer boardNo) {
        BoardOutput boardOutput = mybatisSession
                .selectOne(
                        "kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.board.getUpdateData",
                        boardNo);

        return boardOutput;
    }

    // 게시글 삭제
    public List<SmBoard> deleteBoard(List<String> board) throws GaiaBizException{
        return deleteBoard(board, null);
    }
    public List<SmBoard> deleteBoard(List<String> boardCdList, String dltId) {
        List<SmBoard> smBoardList = new ArrayList<>();
        boardCdList.forEach(boardCd -> {
            HashMap<String,Object> datas = new HashMap<>();
            datas.put("dltId",dltId);
            datas.put("boardCd",boardCd);
            SmBoard smBoard = smBoardRepository.findByBoardCd(boardCd);
            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.board.deleteSmBoard",datas);
            if (smBoard != null) {
                smBoardList.add(smBoard);
                smPopupMsgRepository.softDeleteByBoardCd(smBoard.getBoardCd(), dltId);
            }
        });
        return smBoardList;
    }

    @Transactional
    public void createSmAttachmentsList(List<SmAttachments> smAttachmentsList) {
        for (SmAttachments smAttachments : smAttachmentsList) {
            smAttachmentsRepository.save(smAttachments); // 파일 저장
        }
    }

    public List<SmAttachments> getSmAttachments(String boardCd) {
        return smAttachmentsRepository.findByBoardCdAndDltYn(boardCd, "N");
    }

    @Transactional
    public void deleteSmAttachments(SmAttachments smAttachments) {
        smAttachmentsRepository.updateDelete(smAttachments);
    }

    public List getProjectList(String userType, MybatisInput input) {

        if ("ADMIN".equals(userType) || "관리자".equals(userType)) {
            return mybatisSession
                    .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.board.selectAdminGAIAProjectList");
        } else {
            if ("PGAIA".equals(platform.toUpperCase()) || "GAIA".equals(platform.toUpperCase())) {
                return mybatisSession
                        .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.board.selectGAIAUserProjectList", input);
            } else {
                return mybatisSession
                        .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.board.selectCMISUserProjectList", input);
            }

        }
    }

    public List getCntrctList(String userType, List<String> pjtNoList, MybatisInput input) {
        // 시스템 타입 설정
        input.add("pjtNoList", pjtNoList);

        if ("ADMIN".equals(userType) || "관리자".equals(userType)) {
            return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.board.selectGaiaUserCntrctList",
                    input);
        } else {
            if ("PGAIA".equals(platform.toUpperCase()) || "GAIA".equals(platform.toUpperCase())) {
                return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.board.selectGaiaUserCntrctList",
                        input);
            } else {
                return mybatisSession
                        .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.board.selectCMISUserCntrctList", input);
            }

        }

    }

    // ----------------------------------------API통신--------------------------------------------

    private void sendParams(String transactionId, Map<String, Object> params) {
        Map<String, String> headers = new HashMap<>();

        headers.put("ServiceKey", apiKey);

        Map body = invokeServiceClient.invoke(String.format("%s/%s", apiDomain, apiUrl + transactionId), headers, params);

        log.info("API 연동 body : {}", body);

        if (MapUtils.isEmpty(body)) {
            throw new BizException("response body is empty");
        }

        if ("01".equals(body.get("resultCode"))) {
            throw new BizException("통신 오류 발생");
        }
    }

    /**
     * 게시글 API 통신
     *
     * @param msgId
     * @param params
     * @return
     */
    @Transactional
    public Map<String, Object> insertBoardApi(String msgId, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        if (MapUtils.isEmpty(params)) {
            throw new BizException("params is empty");
        }

        if ("GACA0006".equals(msgId)) {
            log.info("API 연동 params : {}", params);
            // PGAIA에 게시글 추가
            insertPgaiaBoard(objectMapper.convertValue(params.get("boardList"), SmBoard.class),
                    objectMapper.convertValue(params.get("boardReceptionList"),
                            new TypeReference<List<SmBoardReception>>() {
                            }),
                    objectMapper.convertValue(params.get("popupList"),
                            new TypeReference<List<SmPopupMsg>>() {
                            }),
                    objectMapper.convertValue(params.get("attachmentsList"), new TypeReference<List<SmAttachments>>() {
                    }));
        }

        result.put("resultCode", "00");

        return result;
    }

    @Transactional
    public Map<String, Object> updateBoardApi(String msgId, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        if (MapUtils.isEmpty(params)) {
            throw new BizException("params is empty");
        }

        if ("GACA0007".equals(msgId)) {
            log.info("API 연동 params : {}", params);
            // PGAIA에 게시글 수정
            updatePgaiaBoard(objectMapper.convertValue(params.get("boardList"), SmBoard.class),
                    objectMapper.convertValue(params.get("boardReceptionList"),
                            new TypeReference<List<SmBoardReception>>() {
                            }),
                    objectMapper.convertValue(params.get("popupList"),
                            new TypeReference<List<SmPopupMsg>>() {
                            }),
                    objectMapper.convertValue(params.get("attachmentsList"), new TypeReference<List<SmAttachments>>() {
                    }),
                    (String) params.get("usrId"));
        }

        result.put("resultCode", "00");

        return result;
    }

    @Transactional
    public Map<String, Object> deleteBoardApi(String msgId, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        if (MapUtils.isEmpty(params)) {
            throw new BizException("params is empty");
        }

        if ("GACA0008".equals(msgId)) {
            log.info("API 연동 params : {}", params);
            // PGAIA에 게시글 삭제
            deletePgaiaBoard(
                    objectMapper.convertValue(params.get("boardList"),
                            new TypeReference<List<SmBoard>>() {
                            }),
                    (String) params.get("usrId"));
        }

        result.put("resultCode", "00");

        return result;
    }

    /**
     * 게시글 PGAIA에 추가
     *
     */
    @Transactional
    public void insertPgaiaBoard(SmBoard smBoard, List<SmBoardReception> smBoardReceptions,
                                 List<SmPopupMsg> smPopupMsgs, List<SmAttachments> smAttachments) {

        smBoardRepository.save(smBoard);
        smBoardRepository.flush();

        if (smBoardReceptions != null && !smBoardReceptions.isEmpty()) {
            for (SmBoardReception smBoardReception : smBoardReceptions) {
                smBoardReception.setBoardCd(smBoard.getBoardCd());
                smBoardReceptionRepository.save(smBoardReception);
            }
        }

        if (smPopupMsgs != null && !smPopupMsgs.isEmpty()) {
            for (SmPopupMsg smPopupMsg : smPopupMsgs) {
                smPopupMsg.setPopMsgCd(smBoard.getBoardCd());
                smPopupMsgRepository.save(smPopupMsg);
            }
        }

        if (smAttachments != null && !smAttachments.isEmpty()) {
            for (SmAttachments smAttachment : smAttachments) {
                smAttachment.setBoardCd(smBoard.getBoardCd());
                smAttachmentsRepository.save(smAttachment);
            }
        }
    }

    /**
     * 게시글 PGAIA에 수정
     *
     */
    @Transactional
    public void updatePgaiaBoard(SmBoard smBoard, List<SmBoardReception> smBoardReceptionList,
                                 List<SmPopupMsg> smPopupMsgs, List<SmAttachments> newSmAttachments, String usrId) {

        SmBoard existingBoard = smBoardRepository.findByBoardCd(smBoard.getBoardCd());

        if (existingBoard != null) {
            smBoard.setBoardNo(existingBoard.getBoardNo());
        }

        SmBoard savedBoard = smBoardRepository.save(smBoard);

        // 게시판 수신 테이블
        @SuppressWarnings("unchecked")
        List<SmBoardReception> existingReceptions = smBoardReceptionRepository
                .findByBoardCdAndDltYn(savedBoard.getBoardCd(), "N");

        Map<String, SmBoardReception> existingMap = existingReceptions.stream()
                .collect(Collectors.toMap(
                        reception -> reception.getPjtNo() + "|" + reception.getBoardDiv() + "|"
                                + reception.getCntrctNo(),
                        reception -> reception));

//        List<SmBoardReception> savedBoardReceptions = new ArrayList<>();

        for (SmBoardReception newReception : smBoardReceptionList) {
            String key = newReception.getPjtNo() + "|" + newReception.getBoardDiv() + "|" + newReception.getCntrctNo();

            if (existingMap.containsKey(key)) {
                existingMap.remove(key);
            } else {
                SmBoardReception savedBoardReception = createBoardReception(savedBoard.getBoardCd(), newReception);
//                savedBoardReceptions.add(savedBoardReception);
            }
        }
        for (SmBoardReception reception : existingMap.values()) {
            smBoardReceptionRepository.softDeleteByReceSeq(reception.getReceSeq(), usrId);
        }

        // 팝업 테이블
        List<SmPopupMsg> existingPopups = smPopupMsgRepository
                .findByPopMsgCdAndDltYn(savedBoard.getBoardCd(), "N");

        Map<String, SmPopupMsg> existingPopupMap = existingPopups.stream()
                .collect(Collectors.toMap(
                        popup -> popup.getPjtNo() + "|" + popup.getPopDiv() + "|"
                                + popup.getCntrctNo(),
                        popup -> popup));

        for (SmPopupMsg newReception : smPopupMsgs) {
            String key = newReception.getPjtNo() + "|" + newReception.getPopDiv() + "|" + newReception.getCntrctNo();

            if (existingPopupMap.containsKey(key)) {
                SmPopupMsg existing = existingPopupMap.get(key);

                existing.setPopTitle(savedBoard.getBoardTitle());
                existing.setPopContent(savedBoard.getBoardTxt());
                existing.setPopStartDt(newReception.getPopStartDt());
                existing.setPopEndDt(newReception.getPopEndDt());

                smPopupMsgRepository.save(existing);

                existingPopupMap.remove(key);
            } else {
                createPopupMsg(savedBoard, newReception);
            }
        }
        for (SmPopupMsg reception : existingPopupMap.values()) {
            smPopupMsgRepository.updateDelete(reception);
        }

        // === 첨부파일 처리 ===
        List<SmAttachments> existingAttachments = smAttachmentsRepository.findByBoardCdAndDltYn(savedBoard.getBoardCd(),
                "N");

        Map<String, SmAttachments> existingAttachmentMap = existingAttachments.stream()
                .collect(Collectors.toMap(SmAttachments::getFileDiskNm, a -> a));

        for (SmAttachments newAttachment : newSmAttachments) {
            String fileKey = newAttachment.getFileDiskNm();

            if (existingAttachmentMap.containsKey(fileKey)) {
                existingAttachmentMap.remove(fileKey); // 유지
            } else {
                smAttachmentsRepository.save(newAttachment); // 추가
            }
        }

        for (SmAttachments attachment : existingAttachmentMap.values()) {
            smAttachmentsRepository.softDeleteByFileNo(attachment.getFileNo(), usrId); // 삭제
        }
    }

    /**
     * 게시글 PGAIA에서 삭제
     *
     */
    @Transactional
    public void deletePgaiaBoard(List<SmBoard> smBoardList, String usrId) {
        for (SmBoard board : smBoardList) {
            smPopupMsgRepository.softDeleteByBoardCd(board.getBoardCd(), usrId);
            smBoardRepository.softDeleteByBoardCd(board.getBoardCd(), usrId);
        }
    }
}
