package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.authoritygroup;

import org.apache.ibatis.type.Alias;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;

public interface AuthorityGroupMybatisParam {
	@Data
    @Alias("authorityGroupUserInput")
    public class AuthorityGroupUserInput extends MybatisPageable{
        Integer rghtGrpNo;
        String cntrctNo;
        String rghtGrpTy;

		String lang;
		String pstnCd; // 직책 공통 코드
        String ratngCd; // 직급 공통 코드
		String flagCd; // 근무상태 공통 코드

        String columnNm;
        String text;

    }

    @Data
    @Alias("authorityGroupUsersOutput")
    public class AuthorityGroupUsersOutput {
        Integer rghtGrpUsrNo;
        String rghtGrpCd;
        Integer authNo;
        String deptNm;
        String deptId;
        String loginId;
        String usrNm;
        String ratngCd;
        String pstnCd;
        String flag;
        String pstnNm;
        String ratngNm;
        String flagNm;
        String deptUsers;
    }
}
