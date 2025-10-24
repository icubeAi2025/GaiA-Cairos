package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.user;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmUserInfo;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class UserFormImpl implements UserForm {

    @Override
    public UserMybatisParam.SyncUserListInput toSyncUserListInput(SyncUserListSearch syncUserListSearch) {
        if ( syncUserListSearch == null ) {
            return null;
        }

        UserMybatisParam.SyncUserListInput syncUserListInput = new UserMybatisParam.SyncUserListInput();

        syncUserListInput.setPageable( syncUserListSearch.getPageable() );
        syncUserListInput.setSearchType( syncUserListSearch.getSearchType() );
        syncUserListInput.setSearchValue( syncUserListSearch.getSearchValue() );

        return syncUserListInput;
    }

    @Override
    public UserMybatisParam.UserListInput toUserListInput(UserListGet userListGet) {
        if ( userListGet == null ) {
            return null;
        }

        UserMybatisParam.UserListInput userListInput = new UserMybatisParam.UserListInput();

        userListInput.setPageable( userListGet.getPageable() );
        userListInput.setSearchGroup( userListGet.getSearchGroup() );
        userListInput.setSearchName( userListGet.getSearchName() );
        userListInput.setKeyword( userListGet.getKeyword() );
        userListInput.setUserId( userListGet.getUserId() );
        userListInput.setUserType( userListGet.getUserType() );
        userListInput.setSystemType( userListGet.getSystemType() );
        userListInput.setCntrctNo( userListGet.getCntrctNo() );
        userListInput.setPjtNo( userListGet.getPjtNo() );

        return userListInput;
    }

    @Override
    public SmUserInfo toSmUserInfo(UserCreate user) {
        if ( user == null ) {
            return null;
        }

        SmUserInfo smUserInfo = new SmUserInfo();

        smUserInfo.setLoginId( user.getLoginId() );
        smUserInfo.setUsrNm( user.getUsrNm() );
        smUserInfo.setRatngCd( user.getRatngCd() );
        smUserInfo.setPstnCd( user.getPstnCd() );
        smUserInfo.setPhoneNo( user.getPhoneNo() );
        smUserInfo.setTelNo( user.getTelNo() );
        smUserInfo.setEmailAdrs( user.getEmailAdrs() );
        smUserInfo.setUseYn( user.getUseYn() );

        return smUserInfo;
    }

    @Override
    public void updateSmUserInfo(UserUpdate user, SmUserInfo smUserInfo) {
        if ( user == null ) {
            return;
        }

        if ( user.getUsrId() != null ) {
            smUserInfo.setUsrId( user.getUsrId() );
        }
        if ( user.getUsrNm() != null ) {
            smUserInfo.setUsrNm( user.getUsrNm() );
        }
        if ( user.getRatngCd() != null ) {
            smUserInfo.setRatngCd( user.getRatngCd() );
        }
        if ( user.getPstnCd() != null ) {
            smUserInfo.setPstnCd( user.getPstnCd() );
        }
        if ( user.getPhoneNo() != null ) {
            smUserInfo.setPhoneNo( user.getPhoneNo() );
        }
        if ( user.getTelNo() != null ) {
            smUserInfo.setTelNo( user.getTelNo() );
        }
        if ( user.getEmailAdrs() != null ) {
            smUserInfo.setEmailAdrs( user.getEmailAdrs() );
        }
        if ( user.getMngDiv() != null ) {
            smUserInfo.setMngDiv( user.getMngDiv() );
        }
        if ( user.getUseYn() != null ) {
            smUserInfo.setUseYn( user.getUseYn() );
        }
    }
}
