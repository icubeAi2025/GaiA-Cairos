package kr.co.ideait.platform.gaiacairos.comp.common.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardMybatisParam.BoardListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardMybatisParam.BoardOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardMybatisParam.BoardViewInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainBoardService extends AbstractGaiaCairosService {

    @Autowired
    SmAttachmentsRepository smAttachmentsRepository;

    // 메인화면 게시글 조회
    /* TODO 메인화면 서비스로 이동 20250814 jhkim */
    public List<BoardOutput> getMainBoardList(BoardListInput boardListInput) {
        MybatisInput input = new MybatisInput();
        input.add("userId", boardListInput.getUserId());

        List<Map<String, Object>> projectList;
        if ("ADMIN".equals(boardListInput.getUserType()) || "관리자".equals(boardListInput.getUserType())) {
            projectList = mybatisSession
                    .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.common.board.selectAdminGAIAProjectList");
        } else {
            if ("PGAIA".equals(boardListInput.getSystemType()) || "GAIA".equals(boardListInput.getSystemType())) {
                projectList = mybatisSession
                        .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.common.board.selectGAIAUserProjectList", input);
            } else {
                projectList = mybatisSession
                        .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.common.board.selectCMISUserProjectList", input);
                List<String> pjtNoList = projectList.stream()
                        .map(project -> (String) project.get("pjt_no"))
                        .collect(Collectors.toList());
                input.add("pjtNoList", pjtNoList);

                projectList = mybatisSession
                        .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.common.board.selectCMISUserCntrctList", input);
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

        return mybatisSession
                .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.common.board.getMainBoardList", boardListInput);
    }

    // 메인화면 게시글 목록 조회
    /* TODO 메인화면 서비스로 이동 20250814 jhkim */
    public Page<BoardOutput> getMainBoardReadList(BoardListInput boardListInput) {
        MybatisInput input = new MybatisInput();
        input.add("userId", boardListInput.getUserId());

        List<Map<String, Object>> projectList;
        if ("ADMIN".equals(boardListInput.getUserType()) || "관리자".equals(boardListInput.getUserType())) {
            projectList = mybatisSession
                    .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.common.board.selectAdminGAIAProjectList");
        } else {
            if ("PGAIA".equals(boardListInput.getSystemType()) || "GAIA".equals(boardListInput.getSystemType())) {
                projectList = mybatisSession
                        .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.common.board.selectGAIAUserProjectList", input);
            } else {
                projectList = mybatisSession
                        .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.common.board.selectCMISUserProjectList", input);
                List<String> pjtNoList = projectList.stream()
                        .map(project -> (String) project.get("pjt_no"))
                        .collect(Collectors.toList());
                input.add("pjtNoList", pjtNoList);

                projectList = mybatisSession
                        .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.common.board.selectCMISUserCntrctList", input);
            }

        }
        log.info(">>> userId: {}", boardListInput.getUserId());
        List<String> pjtNoList = projectList.stream()
                .map(project -> (String) project.get("pjt_no"))
                .collect(Collectors.toList());
        List<String> cntrctNoList = projectList.stream()
                .map(cntrct -> (String) cntrct.get("cntrct_no"))
                .collect(Collectors.toList());

        boardListInput.setPjtNoList(pjtNoList);
        boardListInput.setCntrctNoList(cntrctNoList);

        List<BoardOutput> boardOutputs = mybatisSession
                .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.common.board.getMainBoardList", boardListInput);

        Long totalCount = 0L;
        if (boardOutputs != null && !boardOutputs.isEmpty()) {
            totalCount = mybatisSession
                    .selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.common.board.getMainBoardListCount", boardListInput);
            return new PageImpl<>(boardOutputs, boardListInput.getPageable(), totalCount);
        }
        return null;
    }

    public BoardOutput getUpdateData(Integer boardNo) {
        BoardOutput boardOutput = mybatisSession
                .selectOne(
                        "kr.co.ideait.platform.gaiacairos.mybatis.mappers.common.board.getUpdateData",
                        boardNo);

        return boardOutput;
    }

    public List<SmAttachments> getSmAttachments(String boardCd) {
        return smAttachmentsRepository.findByBoardCdAndDltYn(boardCd, "N");
    }

    @Transactional
    public void updateView(BoardViewInput input) {
        mybatisSession
                .update(
                        "kr.co.ideait.platform.gaiacairos.mybatis.mappers.common.board.updateBoardView",
                        input);
    }

}
