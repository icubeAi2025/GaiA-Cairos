package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.authoritygroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmAuthorityGroup;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmAuthorityGroupUsers;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface AuthorityGroupForm {

    SmAuthorityGroup toAuthorityGroup(AuthorityGroup authorityGroup);

    SmAuthorityGroup toAuthorityGroup(AuthorityGroupUpdate authorityGroup);

    SmAuthorityGroup toAuthorityGroup(AuthorityGroupUserGet authorityGroupUserGet);

    SmAuthorityGroupUsers toAuthorityGroupUsers(AuthorityGroupUser authorityGroupUser);

    List<SmAuthorityGroupUsers> toAuthorityGroupUsersList(List<AuthorityGroupUser> authorityGroupUserList);

    // AuthorityGroupUserListInput toAuthorityGroupUsers(AuthorityGroupUserSearch
    // authorityGroupUserSearch);

    void updateSmAuthorityGroup(AuthorityGroup authorityGroup, @MappingTarget SmAuthorityGroup smAuthorityGroup);

    void updateSmAuthorityGroup(AuthorityGroupUpdate authorityGroup, @MappingTarget SmAuthorityGroup smAuthorityGroup);

    AuthorityGroupMybatisParam.AuthorityGroupUserInput toAuthorityGroupUserInput(AuthorityGroupUserGet authorityGroupUserGet);
    
    AuthorityGroupMybatisParam.AuthorityGroupUserInput toAuthorityGroupUserInput(AuthorityGroupUserGridGet authorityGroupUserGet);

    /**
     * 권한 그룹 리스트 조회 form
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    class AuthorityGroupSearch extends CommonForm {
        @NotBlank
        String cntrctNo;
        String pjtNo;

        String columnNm;
        String text;
    }

    /**
     * 권한 그룹 form
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    class AuthorityGroup extends CommonForm {
        Integer rghtGrpNo;
        // @NotBlank
        String rghtGrpCd;
        @NotBlank
        String pjtNo;
        @NotBlank
        String cntrctNo;
        @Size(max = 6)
        @NotBlank
        String pjtType;
        String rghtGrpNmEng;
        @NotBlank
        String rghtGrpNmKrn;
        String rghtGrpDscrpt;
        @Size(max = 5)
        @NotBlank
        String rghtGrpTy;
        String rghtGrpRole;
        List<String> rghtKind;
        @Size(max = 1)
        @NotBlank
        String useYn;
    }

    /**
     * 권한 그룹 form
     */
    @Data
    class AuthorityGroupUpdate{
        @NotNull
        Integer rghtGrpNo;
        String rghtGrpNmEng;
        @NotBlank
        String rghtGrpNmKrn;
        String rghtGrpDscrpt;
        @Size(max = 5)
        @NotBlank
        String rghtGrpTy;
        String rghtGrpRole;
        List<String> addRghtKind;
        List<String> delRghtKind;
        @Size(max = 1)
        @NotBlank
        String useYn;
    }

    /**
     * 권한 그룹 리스트 form
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    class AuthorityGroupNoList extends CommonForm {
        List<Integer> authorityGroupNoList;
    }

    /**
     * 권한 그룹 사용자 조회 form
     */
    @Data
    class AuthorityGroupUserGet{
        Integer rghtGrpNo;
        String cntrctNo;
        String rghtGrpTy;

        String columnNm;
        String text;
    }

    /**
     * 권한 그룹 사용자 조회(Grid용) form
     */
    @Data
    class AuthorityGroupUserGridGet extends CommonForm{
        Integer rghtGrpNo;
        String cntrctNo;
        String rghtGrpTy;

        String columnNm;
        String text;
    }

    /**
     * 권한 사용자 form
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    class AuthorityGroupUser extends CommonForm {
        Integer rghtGrpUsrNo;
        Integer rghtGrpNo;
        @Size(max = 50)
        String rghtGrpCd;
        @Size(max = 5)
        String rghtGrpUsrTy;
        Integer authNo;
    }

    /**
     * 권한 사용자 List form
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    class AuthorityGroupUserList extends CommonForm {
        List<AuthorityGroupUser> authorityGroupUserList;
    }

    /**
     * 권한 그룹 사용자 리스트 form
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    class AuthorityGroupUserNoList extends CommonForm {
        List<Integer> authorityGroupUserNoList;
    }
}
