package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.authoritygroup;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmAuthorityGroup;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmAuthorityGroupUsers;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:16+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class AuthorityGroupDtoImpl implements AuthorityGroupDto {

    @Override
    public AuthorityGroupMap fromAuthorityGroupMybatis(MybatisOutput mybatisOutput) {
        if ( mybatisOutput == null ) {
            return null;
        }

        AuthorityGroupMap authorityGroupMap = new AuthorityGroupMap();

        for ( java.util.Map.Entry<String, Object> entry : mybatisOutput.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            authorityGroupMap.put( key, value );
        }

        return authorityGroupMap;
    }

    @Override
    public AuthorityGroup fromSmAuthorityGroup(SmAuthorityGroup smAuthorityGroup) {
        if ( smAuthorityGroup == null ) {
            return null;
        }

        AuthorityGroup authorityGroup = new AuthorityGroup();

        authorityGroup.setRghtGrpNo( smAuthorityGroup.getRghtGrpNo() );
        authorityGroup.setRghtGrpCd( smAuthorityGroup.getRghtGrpCd() );
        authorityGroup.setPjtNo( smAuthorityGroup.getPjtNo() );
        authorityGroup.setCntrctNo( smAuthorityGroup.getCntrctNo() );
        authorityGroup.setPjtType( smAuthorityGroup.getPjtType() );
        authorityGroup.setRghtGrpNmEng( smAuthorityGroup.getRghtGrpNmEng() );
        authorityGroup.setRghtGrpNmKrn( smAuthorityGroup.getRghtGrpNmKrn() );
        authorityGroup.setRghtGrpDscrpt( smAuthorityGroup.getRghtGrpDscrpt() );
        authorityGroup.setRghtGrpTy( smAuthorityGroup.getRghtGrpTy() );
        authorityGroup.setRghtGrpRole( smAuthorityGroup.getRghtGrpRole() );
        authorityGroup.setUseYn( smAuthorityGroup.getUseYn() );

        return authorityGroup;
    }

    @Override
    public List<AuthorityGroup> fromSmAuthorityGroupList(List<SmAuthorityGroup> smAuthorityGroupList) {
        if ( smAuthorityGroupList == null ) {
            return null;
        }

        List<AuthorityGroup> list = new ArrayList<AuthorityGroup>( smAuthorityGroupList.size() );
        for ( SmAuthorityGroup smAuthorityGroup : smAuthorityGroupList ) {
            list.add( fromSmAuthorityGroup( smAuthorityGroup ) );
        }

        return list;
    }

    @Override
    public AuthorityGroupUser fromSmAuthorityGroupUsers(SmAuthorityGroupUsers smAuthorityGroupUsers) {
        if ( smAuthorityGroupUsers == null ) {
            return null;
        }

        AuthorityGroupUser authorityGroupUser = new AuthorityGroupUser();

        return authorityGroupUser;
    }

    @Override
    public List<AuthorityGroupUser> fromSmAuthorityGroupUsers(List<SmAuthorityGroupUsers> smAuthorityGroupUsers) {
        if ( smAuthorityGroupUsers == null ) {
            return null;
        }

        List<AuthorityGroupUser> list = new ArrayList<AuthorityGroupUser>( smAuthorityGroupUsers.size() );
        for ( SmAuthorityGroupUsers smAuthorityGroupUsers1 : smAuthorityGroupUsers ) {
            list.add( fromSmAuthorityGroupUsers( smAuthorityGroupUsers1 ) );
        }

        return list;
    }

    @Override
    public AuthorityGroupUser fromAuthorityGroupUsersMybatis(MybatisOutput mybatisOutput) {
        if ( mybatisOutput == null ) {
            return null;
        }

        AuthorityGroupUser authorityGroupUser = new AuthorityGroupUser();

        for ( java.util.Map.Entry<String, Object> entry : mybatisOutput.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            authorityGroupUser.put( key, value );
        }

        return authorityGroupUser;
    }
}
