package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCodeGroup;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode.CommonCodeMybatisParam.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface CommonCodeForm {

    /*
     * @Null null만 허용한다.
     * 
     * @NotNull 빈 문자열(""), 공백(" ")은 허용하되, Null은 허용하지 않음
     * 
     * @NotEmpty 공백(" ")은 허용하되, Null과 빈 문자열("")은 허용하지 않음
     * 
     * @NotBlank null, 빈 문자열(""), 공백(" ") 모두 허용하지 않는다.
     * 
     * @Email 이메일 형식을 검사한다. 단, 빈 문자열("")의 경우엔 통과 시킨다. ( @Pattern을 통한 정규식 검사를 더 많이 사용
     * 
     * @Pattern(regexp = ) 정규식 검사할 때 사용한다.
     * 
     * @Size(min=, max=) 길이를 제한할 때 사용한다.
     * 
     * @Max(value = ) value 이하의 값만 허용한다.
     * 
     * @Min(value = ) value 이상의 값만 허용한다.
     * 
     * @Positive 값을 양수로 제한한다.
     * 
     * @PositiveOrZero 값을 양수와 0만 가능하도록 제한한다.
     * 
     * @Negative 값을 음수로 제한한다.
     * 
     * @NegativeOrZero 값을 음수와 0만 가능하도록 제한한다.
     * 
     * @Future Now 보다 미래의 날짜, 시간이어야 한다.
     * 
     * @FutureOrPresent Now 거나 미래의 날짜, 시간이어야 한다.
     * 
     * @Past Now 보다 과거의 날짜, 시간이어야 한다.
     * 
     * @PastFutureOrPresent Now 거나 과거의 날짜, 시간이어야 한다.
     */

    SmComCode toSmComCodeTest(Test test);

    SmComCodeGroup toSmComCodeGroup(CommonCodeGroup commonCodeGroup);

    SmComCode toSmComCode(CommonCode commonCode);

    CommonCodeListInput toCommonCodeListInput(CommonCodeSearch commonCodeSearch);

    CommonCodeListInput toCommonCodeListInput(CommonCodeSearchMulti commonCodeSearch);

    void updateSmComCodeGroup(CommonCodeGroupUpdate commonCodeGroup, @MappingTarget SmComCodeGroup smComCodeGroup);

    void updateSmComCode(CommonCodeUpdate commonCode, @MappingTarget SmComCode smComCode);

    // 코드 개별조회
    CodeInput toCodeInput(Integer cmnGrpNo);

    // 그룹코드 이동
    SmComCodeGroup toSmComCodeGroup(GroupMove groupMove);

    @Data
    class Test {
        @NotBlank(message = "아이디는 필수 입력 값입니다.")
        String name;
        @Positive(message = "아이디는 양수여야 합니다.")
        int id;
    }

    // 그룹코드---------------------------------------------------------

    /**
     * 그룹코드 생성 form
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    class CommonCodeGroup extends CommonForm {
        @NotBlank
        String cmnCd;
        String cmnCdNmEng;
        @NotBlank
        String cmnCdNmKrn;
        short cmnCdDsplyOrdr;
        String cmnCdDscrpt;
        @NotNull
        Integer upCmnGrpNo;
        @NotBlank
        String upCmnGrpCd;
        @NotBlank
        String publicYn;
        @NotNull
        Short cmnLevel;
        @NotBlank
        @Size(max = 1)
        String useYn;
    }

    /**
     * 그룹코드 수정 form
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    class CommonCodeGroupUpdate extends CommonForm {
        @NotNull
        Integer cmnGrpNo;
        String cmnCdNmEng;
        @NotBlank
        String cmnCdNmKrn;
        Short cmnCdDsplyOrdr;
        String cmnCdDscrpt;
        @NotBlank
        @Size(max = 1)
        String useYn;
    }

    /**
     * 그룹코드 리스트 form
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    class CommonCodeGroupNoList extends CommonForm {
        @NotNull
        List<String> cmnGrpCdList;
    }

    /**
     * 그룹순번 이동
     */
    @Data
    class GroupMove {
        @NotNull
        Integer cmnGrpNo;
        @NotNull
        Integer upCmnGrpNo;
    }

    // 코드---------------------------------------------------------

    /**
     * 공통코드 검색 form
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    class CommonCodeSearch extends CommonForm {
        Integer cmnGrpNo;

        String cmnGrpCd;
        String cmnCd;
        String cmnCdNmEng;
        String cmnCdNmKrn;
        String cmnCdDscrpt;

        String searchType;
        String searchText;
        String startDt;
        String endDt;
    }

    /**
     * 공통코드 검색 form (다중)
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    class CommonCodeSearchMulti extends CommonForm {
        @NotNull
        List<Integer> cmnGrpNoList;
        String cmnCd;
        String cmnCdNmEng;
        String cmnCdNmKrn;
        String cmnCdDscrpt;
    }

    /**
     * 공통코드 등록 form
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    class CommonCode extends CommonForm {
        @NotNull
        Integer cmnGrpNo;
        @NotBlank
        String cmnGrpCd;
        @NotBlank
        String cmnCd;
        String cmnCdNmEng;
        @NotBlank
        String cmnCdNmKrn;
        String cmnCdDscrpt;
        String attrbtCd1;
        String attrbtCd2;
        String attrbtCd3;
        String attrbtCd4;
        String attrbtCd5;
        short cmnCdDsplyOrder;
        @NotBlank
        @Size(max = 1)
        String useYn;
    }

    /**
     * 공통코드 수정 form
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    class CommonCodeUpdate extends CommonForm {
        @NotNull
        String cmnCdNo;
        @NotBlank
        String cmnCd;
        String cmnCdNmEng;
        @NotBlank
        String cmnCdNmKrn;
        String cmnCdDscrpt;
        String attrbtCd1;
        String attrbtCd2;
        String attrbtCd3;
        String attrbtCd4;
        String attrbtCd5;
        short cmnCdDsplyOrder;
        @NotBlank
        @Size(max = 1)
        String useYn;
    }

    /**
     * 공통코드 리스트 form
     */
    @Data
    class CommonCodeNoList {
        List<SmComCode> cmnCdList;
    }

    /**
     * 공통코드 리스트 form
     */
    @Data
    class CommonCodeList {
        @NotNull
        List<String> cmnCdList;
    }

}
