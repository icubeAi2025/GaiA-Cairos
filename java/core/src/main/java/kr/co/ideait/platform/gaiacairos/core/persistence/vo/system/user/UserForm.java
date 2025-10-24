package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.user;

import jakarta.validation.constraints.NotBlank;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmUserInfo;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface UserForm {

    UserMybatisParam.SyncUserListInput toSyncUserListInput(SyncUserListSearch syncUserListSearch);

    UserMybatisParam.UserListInput toUserListInput(UserListGet userListGet);

    SmUserInfo toSmUserInfo(UserCreate user);

    void updateSmUserInfo(UserUpdate user, @MappingTarget SmUserInfo smUserInfo);

    @Data
    class UserListGet extends CommonForm {
        String searchGroup;
        String searchName;
        String keyword;

        String userId;
        String userType;
        String systemType;

        String cntrctNo;
        String pjtNo;
    }

    @Data
    class UserCreate {
        @NotBlank(message = "계정은 필수 입력 값입니다.")
        String loginId;
        @NotBlank(message = "이름은 필수 입력 값입니다.")
        String usrNm;
        String ratngCd;
        String pstnCd;
        String phoneNo;
        String telNo;
        String emailAdrs;
        @NotBlank(message = "사용여부는 필수 입력 값입니다.")
        String useYn;
    }

    @Data
    class UserUpdate {
        @NotBlank
        String usrId;
        @NotBlank(message = "이름은 필수 입력 값입니다.")
        String usrNm;
        String ratngCd;
        String pstnCd;
        String phoneNo;
        String telNo;
        String emailAdrs;
        String mngDiv;
        @NotBlank(message = "사용여부는 필수 입력 값입니다.")
        String useYn;
    }

    @Data
    class UserDelete {
        List<String> userList;
    }

    @Data
    class UserList {
        List<String> userList;
    }

    @Data
    class SyncUserListSearch extends CommonForm {
        String searchType;
        String searchValue;
    }

    @Data
    class SyncUserIds extends CommonForm {
        String usrId;
    }


}
