package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.authoritygroup;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmAuthorityGroup;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmAuthorityGroupUsers;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MapDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface AuthorityGroupDto {

    AuthorityGroupMap fromAuthorityGroupMybatis(MybatisOutput mybatisOutput);

    AuthorityGroup fromSmAuthorityGroup(SmAuthorityGroup smAuthorityGroup);

    List<AuthorityGroup> fromSmAuthorityGroupList(List<SmAuthorityGroup> smAuthorityGroupList);

    AuthorityGroupUser fromSmAuthorityGroupUsers(SmAuthorityGroupUsers smAuthorityGroupUsers);

    List<AuthorityGroupUser> fromSmAuthorityGroupUsers(List<SmAuthorityGroupUsers> smAuthorityGroupUsers);

    AuthorityGroupUser fromAuthorityGroupUsersMybatis(MybatisOutput mybatisOutput);

    @Data
    @EqualsAndHashCode(callSuper = false)
    class AuthorityGroupMap extends MapDto {
        Integer rghtGrpNo;
        String rghtGrpCd;
        String pjtNo;
        String cntrctNo;
        String pjtType;
        // String rghtGrpNmEng;
        // String rghtGrpNmKrn;
        String rghtGrpNm;
        String rghtGrpDscrpt;
        String rghtGrpTy;
        String rghtGrpTyNm;
        String rghtGrpRole;
        String rghtGrpRoleNm;
        String rghtGrpKind;
        String useYn;
        String dltYn;
        LocalDateTime chgDt;
    }

    class AuthorityGroupUser extends MapDto {
        Integer rghtGrpUsrNo;
        Integer rghtGrpNo;
        String rghtGrpCd;
        String rghtGrpUsrTy;
        Integer authNo;
        String compNm;
        String deptNm;
        String loginId;
        String usrNm;
        String ratngCd;
        String pstnCd;
        String useYn;
        LocalDateTime chgDt;
    }

    @Data
    class AuthorityGroup {
        Integer rghtGrpNo;
        String rghtGrpCd;
        String pjtNo;
        String cntrctNo;
        String pjtType;
        String rghtGrpNmEng;
        String rghtGrpNmKrn;
        String rghtGrpDscrpt;
        String rghtGrpTy;
        String rghtGrpRole;
        String useYn;
    }

}
