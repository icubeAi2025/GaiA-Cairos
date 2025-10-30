package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.user;

import java.time.LocalDateTime;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;

import org.apache.ibatis.type.Alias;

import lombok.Data;
import lombok.EqualsAndHashCode;

public interface UserMybatisParam {

    @Data
    @Alias("userListInput")
    @EqualsAndHashCode(callSuper = true)
    public class UserListInput extends MybatisPageable {
        String searchGroup;
        String searchName;
        String keyword;
        String lang;

        String userId;
        String userType;
        String systemType;

        String cntrctNo;
        String pjtNo;

        String cmnGrpCdRatng;
        String cmnGrpCdPstn;
    }

    @Data
    @Alias("userOutput")
    @EqualsAndHashCode(callSuper = true)
    public class UserOutput extends MybatisPageable {
        String usrId;
        String NcpUsrId;
        String OciUsrId;
        String loginId;
        String usrNm;
        String ratngCd;
        String pstnCd;
        String phoneNo;
        String telNo;
        String emailAdrs;
        String mngDiv;
        String mngDivNm;
        String useYn;
        String dltYn;
        String rgstrId;
        LocalDateTime rgstrDt;
        String chgId;
        String chgDt;
        String dltId;
        Long cnt;
        LocalDateTime dltDt;

        String ratngCdKrn;
        String pstnCdKrn;

        String compNm;
        String ratngNm;
        String pstnNm;
        String flagNm;

        Long totalNum;

        String userId;
        String userType;
        String systemType;

        String fileOrgNm;
        String fileDiskNm;
        String fileDiskPath;
    }
    
    @Data
    @Alias("syncUserInfo")
    public class SyncUserInfo {
        String usrId;
        String loginId;
        String ociUsrId;
        String ncpUsrId;
        String usrNm;
        String phoneNo;
        String telNo;
        String emailAdrs;
        String rgstrId;
        String corpNo;
    }
    
    @Data
    @Alias("userInput")
    @EqualsAndHashCode(callSuper = true)
    public class UserInput extends MybatisPageable {
        String usrId;
        String cmnGrpCdRatng;
        String cmnGrpCdPstn;
    }

    @Data
    @Alias("syncUserListInput")
    @EqualsAndHashCode(callSuper = true)
    public class SyncUserListInput extends MybatisPageable {
        String searchType;
        String searchValue;

        public Integer getEndRow() {
            if (getPageable() != null) {
                return (int) getPageable().getOffset() + getPageable().getPageSize();
            }
            return null;
        }

        public Integer getOffset() {
            return getPageable() != null ? (int) getPageable().getOffset() : null;
        }
    }
    
    @Data
    @Alias("updateUserInfo")
    public class UpdateUserInfo {
        String usrId;
        String flag;
        String usrNm;
        String telNo;
        String phoneNo;
        String ratngCd;
        String pstnCd;
        String emailAdrs;
        String mngDiv;
        String useYn;
        String chgId;
        String dltYn;
    }
}
