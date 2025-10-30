package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.company;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmCompany;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import lombok.ToString;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = ComponentModel.SPRING)
public interface CompanyForm {

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

    CompanyMybatisParam.CompanyListInput toCompanyListInput(CompanyListGet companyListGet);

    CompanyMybatisParam.UserCompanyListInput toUserCompanyListInput(UserCompanyListGet userCompanyListGet);

    SmCompany toSmCompany(Company company);

    List<SmCompany> toSmCompanyList(List<Company> companyList);

    // List<SmCompany> toUpdateSmCompanyList(List<CompanyUpdate> companyList);

    void updateSmCompany(CompanyUpdate companyUpdate, @MappingTarget SmCompany smCompany);

    @Data
    class Test {
        @NotBlank(message = "아이디는 필수 입력 값입니다.")
        String name;
        @Positive(message = "아이디는 양수여야 합니다.")
        int id;
    }

    /**
     * 회사 등록 폼
     */
    @Data
    class Company extends CommonForm {
        @NotNull(message = "업체번호는 필수 입력 값입니다.")
        @Size(max = 15)
        String corpNo;

        @NotBlank(message = "회사그룹은 필수 입력 값입니다.")
        String compGrpCd;

        @NotBlank(message = "회사명은 필수 입력 값입니다.")
        @Size(max = 100)
        String compNm;

        @Size(max = 10)
        @NotBlank(message = "사업자번호는 필수 입력 값입니다.")
        String bsnsmnNo;

        @Size(max = 20)
        @NotBlank(message = "대표명은 필수 입력 값입니다.")
        String corpCeo;

        @Size(max = 200)
        String compDscrpt;

        @Size(max = 100)
        String pstnNm;

        @Size(max = 20)
        String mngNm;

        @Size(max = 15)
        String compTelno;

        @Size(max = 15)
        String compFaxno;

        @Size(max = 200)
        String compAdrs;

        @NotBlank(message = "사용여부는 필수 입력 값입니다.")
        @Size(max = 1)
        String useYn;

        String dltYn;
    }

    @Data
    class CompanyUpdate extends CommonForm {
        @NotBlank(message = "업체번호는 필수 입력 값입니다.")
        String corpNo;

        @NotBlank(message = "회사그룹은 필수 입력 값입니다.")
        String compGrpCd;

        @NotBlank(message = "회사명은 필수 입력 값입니다.")
        @Size(max = 100)
        String compNm;

        @Size(max = 10)
        @NotBlank(message = "사업자번호는 필수 입력 값입니다.")
        String bsnsmnNo;

        @Size(max = 20)
        @NotBlank(message = "대표명은 필수 입력 값입니다.")
        String corpCeo;

        String compDscrpt;

        String pstnNm;

        String mngNm;

        String compTelno;

        String compFaxno;

        String compAdrs;

        @NotBlank(message = "사용여부는 필수 입력 값입니다.")
        @Size(max = 1)
        String useYn;
    }

    /**
     * 회사 검색 폼
     */
    @Data
    @ToString
    class CompanyListGet extends CommonForm {
        String platform;
        String type;
        String column;
        String keyword;

        List<Map<String, String>> compGrpCdList;
    }

    /**
     * 회사 검색 폼
     */
    @Data
    @ToString
    class UserCompanyListGet {
        String searchGroup;
    }

    /**
     * 회사 업체번호 리스트
     */
    @Data
    class CorpNoList {
        List<String> corpNoList;
    }

    /**
     * 회사 그룹코드 리스트 form
     */
    @Data
    class CompGrpCdList {
        @NotNull
        List<String> compGrpCdList;
    }

}
