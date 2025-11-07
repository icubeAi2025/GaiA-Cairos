package kr.co.ideait.platform.gaiacairos.comp.eapproval;

import kr.co.ideait.platform.gaiacairos.comp.eapproval.service.LineSetService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApLineSet;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApLinesetMng;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LineSetComponent extends AbstractComponent {

    @Autowired
    LineSetService lineSetService;

    /**
     * 나의 결재선 조회
     * @return
     */
    public List getMyLineSetList() {
        MybatisInput input = MybatisInput.of().add("cntrctNo", UserAuth.get(true).getCntrctNo())
                .add("rgstrId", UserAuth.get(true).getUsrId());
        return lineSetService.getMyLineSetList(input);
    }


    /**
     * 관리자 결재선 조회
     * @param input
     * @return
     */
    public List getAdminLineSetList(MybatisInput input) {
        return lineSetService.getAdminLineSetList(input);
    }


    /**
     * 나의 결재선 상세 조회
     * @param apLineNo
     * @param userType
     * @param cntrctNo
     * @return
     */
    public List getMyLineSetDetail(Integer apLineNo, String userType, String cntrctNo) {
        MybatisInput input = MybatisInput.of().add("cntrctNo", cntrctNo)
                .add("pjtNo", UserAuth.get(true).getPjtNo())
                .add("pjtType", platform.toUpperCase())
                .add("apLineNo", apLineNo)
                .add("userType", userType);
        return "ADMIN".equals(userType) ? lineSetService.getAdminLineSetDetail(input) : lineSetService.getMyLineSetDetail(input);
    }


    /**
     * 관리자 결재선 상세 조회
     * @param apLineNo
     * @param cntrctNo
     * @return
     */
    public List getAdminLineSetDetail(Integer apLineNo, String cntrctNo) {
        MybatisInput input = MybatisInput.of().add("cntrctNo", cntrctNo)
                .add("pjtNo", UserAuth.get(true).getPjtNo())
                .add("pjtType", platform.toUpperCase())
                .add("apLineNo", apLineNo);
        return lineSetService.getAdminLineSetDetail(input);
    }


    /**
     * 결재선 삭제
     * @param delList
     */
    @Transactional
    public void deleteLineSetList(List<Integer> delList) {
        MybatisInput input = MybatisInput.of().add("delList", delList).add("usrId", UserAuth.get(true).getUsrId());
        lineSetService.deleteApLineSetList(input);
        lineSetService.deleteApLineSetMngList(input);
    }


    /**
     * 결재선 추가
     * @param apLinesetMng
     * @param apLineSet
     * @param cntrctNo
     */
    @Transactional
    public void createLineSet(ApLinesetMng apLinesetMng, List<ApLineSet> apLineSet, String cntrctNo) {
        // ApLinesetMng 저장
        apLinesetMng.setCntrctNo(cntrctNo);
        apLinesetMng.setPjtType(platform.toUpperCase());
        apLinesetMng.setDltYn("N");
        ApLinesetMng savedApLinesetMng = lineSetService.saveApLinesetMng(apLinesetMng);

        // ApLineSet 저장
        apLineSet.forEach(line -> {
            line.setApLineNo(savedApLinesetMng.getApLineNo());
            line.setDltYn("N");
        });

        lineSetService.saveApLineSet(apLineSet);
    }


    /**
     * 결재선 수정
     * @param apLinesetMng
     * @param apLineSet
     */
    @Transactional
    public void updateLineSet(ApLinesetMng apLinesetMng, List<ApLineSet> apLineSet) {
        // 기존 결재선 라인 삭제
        lineSetService.deleteApLineSet(apLinesetMng.getApLineNo());

        ApLinesetMng findObj = lineSetService.getApLinesetMng(apLinesetMng.getApLineNo());
        if(findObj != null) {
            // ApLinesetMng 저장
            findObj.setApType(apLinesetMng.getApType());
            findObj.setApLineNm(apLinesetMng.getApLineNm());
            lineSetService.saveApLinesetMng(findObj);

            // ApLineSet 저장
            apLineSet.forEach(line -> {
                line.setDltYn("N");
            });

            lineSetService.saveApLineSet(apLineSet);
        }
    }


    /**
     * 관리자 결재선 - 부서 조회
     * @param input
     * @return
     */
    public List<MybatisOutput> selectAdminLinesetDeptInfo(MybatisInput input) {
        return lineSetService.selectAdminLinesetDeptInfo(input);
    }


    /**
     * 관리자 결재선 - 결재자 검색
     * @param input
     * @return
     */
    public List<MybatisOutput> selectAdminLinesetUser(MybatisInput input) {
        return lineSetService.selectAdminLinesetUser(input);
    }


    /**
     * 관리자 결재선 - 중복체크
     * @param input
     * @return
     */
    public Integer checkDuplicate(MybatisInput input) {
        return lineSetService.checkDuplicate(input);
    }


    /**
     * 관리자 결재선 - 조회
     * @param apLineNo
     */
    public ApLinesetMng getAdminLineSet(Integer apLineNo) {
        return lineSetService.getApLinesetMng(apLineNo);
    }
}
