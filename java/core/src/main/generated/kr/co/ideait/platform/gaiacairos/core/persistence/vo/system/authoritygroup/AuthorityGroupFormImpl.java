package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.authoritygroup;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmAuthorityGroup;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmAuthorityGroupUsers;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class AuthorityGroupFormImpl implements AuthorityGroupForm {

    @Override
    public SmAuthorityGroup toAuthorityGroup(AuthorityGroup authorityGroup) {
        if ( authorityGroup == null ) {
            return null;
        }

        SmAuthorityGroup smAuthorityGroup = new SmAuthorityGroup();

        smAuthorityGroup.setRghtGrpNo( authorityGroup.getRghtGrpNo() );
        smAuthorityGroup.setRghtGrpCd( authorityGroup.getRghtGrpCd() );
        smAuthorityGroup.setPjtNo( authorityGroup.getPjtNo() );
        smAuthorityGroup.setCntrctNo( authorityGroup.getCntrctNo() );
        smAuthorityGroup.setPjtType( authorityGroup.getPjtType() );
        smAuthorityGroup.setRghtGrpNmEng( authorityGroup.getRghtGrpNmEng() );
        smAuthorityGroup.setRghtGrpNmKrn( authorityGroup.getRghtGrpNmKrn() );
        smAuthorityGroup.setRghtGrpDscrpt( authorityGroup.getRghtGrpDscrpt() );
        smAuthorityGroup.setRghtGrpTy( authorityGroup.getRghtGrpTy() );
        smAuthorityGroup.setRghtGrpRole( authorityGroup.getRghtGrpRole() );
        smAuthorityGroup.setUseYn( authorityGroup.getUseYn() );

        return smAuthorityGroup;
    }

    @Override
    public SmAuthorityGroup toAuthorityGroup(AuthorityGroupUpdate authorityGroup) {
        if ( authorityGroup == null ) {
            return null;
        }

        SmAuthorityGroup smAuthorityGroup = new SmAuthorityGroup();

        smAuthorityGroup.setRghtGrpNo( authorityGroup.getRghtGrpNo() );
        smAuthorityGroup.setRghtGrpNmEng( authorityGroup.getRghtGrpNmEng() );
        smAuthorityGroup.setRghtGrpNmKrn( authorityGroup.getRghtGrpNmKrn() );
        smAuthorityGroup.setRghtGrpDscrpt( authorityGroup.getRghtGrpDscrpt() );
        smAuthorityGroup.setRghtGrpTy( authorityGroup.getRghtGrpTy() );
        smAuthorityGroup.setRghtGrpRole( authorityGroup.getRghtGrpRole() );
        smAuthorityGroup.setUseYn( authorityGroup.getUseYn() );

        return smAuthorityGroup;
    }

    @Override
    public SmAuthorityGroup toAuthorityGroup(AuthorityGroupUserGet authorityGroupUserGet) {
        if ( authorityGroupUserGet == null ) {
            return null;
        }

        SmAuthorityGroup smAuthorityGroup = new SmAuthorityGroup();

        smAuthorityGroup.setRghtGrpNo( authorityGroupUserGet.getRghtGrpNo() );
        smAuthorityGroup.setCntrctNo( authorityGroupUserGet.getCntrctNo() );
        smAuthorityGroup.setRghtGrpTy( authorityGroupUserGet.getRghtGrpTy() );

        return smAuthorityGroup;
    }

    @Override
    public SmAuthorityGroupUsers toAuthorityGroupUsers(AuthorityGroupUser authorityGroupUser) {
        if ( authorityGroupUser == null ) {
            return null;
        }

        SmAuthorityGroupUsers smAuthorityGroupUsers = new SmAuthorityGroupUsers();

        smAuthorityGroupUsers.setRghtGrpUsrNo( authorityGroupUser.getRghtGrpUsrNo() );
        smAuthorityGroupUsers.setRghtGrpNo( authorityGroupUser.getRghtGrpNo() );
        smAuthorityGroupUsers.setRghtGrpCd( authorityGroupUser.getRghtGrpCd() );
        smAuthorityGroupUsers.setRghtGrpUsrTy( authorityGroupUser.getRghtGrpUsrTy() );
        smAuthorityGroupUsers.setAuthNo( authorityGroupUser.getAuthNo() );

        return smAuthorityGroupUsers;
    }

    @Override
    public List<SmAuthorityGroupUsers> toAuthorityGroupUsersList(List<AuthorityGroupUser> authorityGroupUserList) {
        if ( authorityGroupUserList == null ) {
            return null;
        }

        List<SmAuthorityGroupUsers> list = new ArrayList<SmAuthorityGroupUsers>( authorityGroupUserList.size() );
        for ( AuthorityGroupUser authorityGroupUser : authorityGroupUserList ) {
            list.add( toAuthorityGroupUsers( authorityGroupUser ) );
        }

        return list;
    }

    @Override
    public void updateSmAuthorityGroup(AuthorityGroup authorityGroup, SmAuthorityGroup smAuthorityGroup) {
        if ( authorityGroup == null ) {
            return;
        }

        if ( authorityGroup.getRghtGrpNo() != null ) {
            smAuthorityGroup.setRghtGrpNo( authorityGroup.getRghtGrpNo() );
        }
        if ( authorityGroup.getRghtGrpCd() != null ) {
            smAuthorityGroup.setRghtGrpCd( authorityGroup.getRghtGrpCd() );
        }
        if ( authorityGroup.getPjtNo() != null ) {
            smAuthorityGroup.setPjtNo( authorityGroup.getPjtNo() );
        }
        if ( authorityGroup.getCntrctNo() != null ) {
            smAuthorityGroup.setCntrctNo( authorityGroup.getCntrctNo() );
        }
        if ( authorityGroup.getPjtType() != null ) {
            smAuthorityGroup.setPjtType( authorityGroup.getPjtType() );
        }
        if ( authorityGroup.getRghtGrpNmEng() != null ) {
            smAuthorityGroup.setRghtGrpNmEng( authorityGroup.getRghtGrpNmEng() );
        }
        if ( authorityGroup.getRghtGrpNmKrn() != null ) {
            smAuthorityGroup.setRghtGrpNmKrn( authorityGroup.getRghtGrpNmKrn() );
        }
        if ( authorityGroup.getRghtGrpDscrpt() != null ) {
            smAuthorityGroup.setRghtGrpDscrpt( authorityGroup.getRghtGrpDscrpt() );
        }
        if ( authorityGroup.getRghtGrpTy() != null ) {
            smAuthorityGroup.setRghtGrpTy( authorityGroup.getRghtGrpTy() );
        }
        if ( authorityGroup.getRghtGrpRole() != null ) {
            smAuthorityGroup.setRghtGrpRole( authorityGroup.getRghtGrpRole() );
        }
        if ( authorityGroup.getUseYn() != null ) {
            smAuthorityGroup.setUseYn( authorityGroup.getUseYn() );
        }
    }

    @Override
    public void updateSmAuthorityGroup(AuthorityGroupUpdate authorityGroup, SmAuthorityGroup smAuthorityGroup) {
        if ( authorityGroup == null ) {
            return;
        }

        if ( authorityGroup.getRghtGrpNo() != null ) {
            smAuthorityGroup.setRghtGrpNo( authorityGroup.getRghtGrpNo() );
        }
        if ( authorityGroup.getRghtGrpNmEng() != null ) {
            smAuthorityGroup.setRghtGrpNmEng( authorityGroup.getRghtGrpNmEng() );
        }
        if ( authorityGroup.getRghtGrpNmKrn() != null ) {
            smAuthorityGroup.setRghtGrpNmKrn( authorityGroup.getRghtGrpNmKrn() );
        }
        if ( authorityGroup.getRghtGrpDscrpt() != null ) {
            smAuthorityGroup.setRghtGrpDscrpt( authorityGroup.getRghtGrpDscrpt() );
        }
        if ( authorityGroup.getRghtGrpTy() != null ) {
            smAuthorityGroup.setRghtGrpTy( authorityGroup.getRghtGrpTy() );
        }
        if ( authorityGroup.getRghtGrpRole() != null ) {
            smAuthorityGroup.setRghtGrpRole( authorityGroup.getRghtGrpRole() );
        }
        if ( authorityGroup.getUseYn() != null ) {
            smAuthorityGroup.setUseYn( authorityGroup.getUseYn() );
        }
    }

    @Override
    public AuthorityGroupMybatisParam.AuthorityGroupUserInput toAuthorityGroupUserInput(AuthorityGroupUserGet authorityGroupUserGet) {
        if ( authorityGroupUserGet == null ) {
            return null;
        }

        AuthorityGroupMybatisParam.AuthorityGroupUserInput authorityGroupUserInput = new AuthorityGroupMybatisParam.AuthorityGroupUserInput();

        authorityGroupUserInput.setRghtGrpNo( authorityGroupUserGet.getRghtGrpNo() );
        authorityGroupUserInput.setCntrctNo( authorityGroupUserGet.getCntrctNo() );
        authorityGroupUserInput.setRghtGrpTy( authorityGroupUserGet.getRghtGrpTy() );
        authorityGroupUserInput.setColumnNm( authorityGroupUserGet.getColumnNm() );
        authorityGroupUserInput.setText( authorityGroupUserGet.getText() );

        return authorityGroupUserInput;
    }

    @Override
    public AuthorityGroupMybatisParam.AuthorityGroupUserInput toAuthorityGroupUserInput(AuthorityGroupUserGridGet authorityGroupUserGet) {
        if ( authorityGroupUserGet == null ) {
            return null;
        }

        AuthorityGroupMybatisParam.AuthorityGroupUserInput authorityGroupUserInput = new AuthorityGroupMybatisParam.AuthorityGroupUserInput();

        authorityGroupUserInput.setPageable( authorityGroupUserGet.getPageable() );
        authorityGroupUserInput.setRghtGrpNo( authorityGroupUserGet.getRghtGrpNo() );
        authorityGroupUserInput.setCntrctNo( authorityGroupUserGet.getCntrctNo() );
        authorityGroupUserInput.setRghtGrpTy( authorityGroupUserGet.getRghtGrpTy() );
        authorityGroupUserInput.setColumnNm( authorityGroupUserGet.getColumnNm() );
        authorityGroupUserInput.setText( authorityGroupUserGet.getText() );

        return authorityGroupUserInput;
    }
}
