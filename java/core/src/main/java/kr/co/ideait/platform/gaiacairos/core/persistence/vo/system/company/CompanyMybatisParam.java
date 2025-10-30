package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.company;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import kr.co.ideait.iframework.annotation.Description;
import org.apache.ibatis.type.Alias;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;
import lombok.EqualsAndHashCode;

public interface CompanyMybatisParam {

    @Data
    @Alias("companyListInput")
    @EqualsAndHashCode(callSuper = true)
    public class CompanyListInput extends MybatisPageable {
        String platform;
        String type;

        String column;
        String keyword;

        String lang;
        List<Map<String, String>> compGrpCdList;
    }

    @Data
    @Alias("userCompanyListInput")
    public class UserCompanyListInput {
        String searchGroup;
    }

    @Data
    @Alias("companyOutput")
    @EqualsAndHashCode(callSuper = true)
    public class CompanyOutput extends MybatisPageable {
        String corpNo;
        String compGrpCd;
        String cmnCdNmKrn;
        String cmnCdNmEng;
        String compGrpCdNm;
        String compNm;
        String compDscrpt;
        String pstnNm;
        String mngNm;
        String compTelno;
        String compFaxno;
        String compAdrs;
        String useYn;
        String corpCeo;
        String bsnsmnNo;
        // LocalDateTime chgDt;
        String chgDt;
    }

    @Data
    @Alias("userCompanyOutput")
    public class UserCompanyOutput {
        String corpNo;
        String compNm;
    }

    @Data
    @Alias("ideaCompanyOutput")
    public class IdeaCompanyOutput extends MybatisPageable {
        @Description(name = "업체번호", description = "")
        String corpNo;

        @Description(name = "사업자번호", description = "")
        String bizno;

        @Description(name = "법인번호", description = "")
        String corprtNo;

        @Description(name = "대표자 명", description = "")
        String ceoNm;

        @Description(name = "회사 명", description = "")
        String compNm;

        @Description(name = "회사 전화번호", description = "")
        String compTelno;

        @Description(name = "우편번호", description = "")
        String zipcd;

        @Description(name = "기본 주소", description = "")
        String baseAdrs;

        @Description(name = "상세 주소", description = "")
        String dtlAdrs;

        @Description(name = "통합첨부파일번호", description = "")
        String untyatchFileNo;

        @Description(name = "인증 코드", description = "")
        String certiCd;

        @Description(name = "업체 구분 코드", description = "")
        String corpDivCd;

        @Description(name = "전자세금계산서 주소", description = "")
        String eletxbilAdrs;

        @Description(name = "전자세금계산서 업종 명", description = "")
        String eletxbilIndtpNm;

        @Description(name = "전자세금계산서 업태 명", description = "")
        String eletxbilBzcndNm;

        @Description(name = "전자세금계산서 사업자번호", description = "")
        String eletxbilBizno;

        @Description(name = "전자세금계산서 회사 명", description = "")
        String eletxbilCompNm;

        @Description(name = "전자세금계산서 대표자 명", description = "")
        String eletxbilCeoNm;

        @Description(name = "세금계산서 이메일1", description = "")
        String eletxbilEmail1;

        @Description(name = "인증일자", description = "")
        String certiDate;

        @Description(name = "인증서종료일자", description = "")
        String certEndDate;

        @Description(name = "사스사용통제구분코드", description = "")
        String saasUseCnrlDivCd;

        @Description(name = "등록자ID", description = "")
        String rgstrId;

        @Description(name = "등록일시", description = "")
        LocalDateTime rgstDt;

        @Description(name = "수정자ID", description = "")
        String chgId;

        @Description(name = "수정일시", description = "")
        LocalDateTime chgDt;
    }

    @Data
    @Alias("pcesCompanyOutput")
    public class PcesCompanyOutput extends MybatisPageable {
        @Description(name = "업체번호", description = "")
        String corpNo;

        @Description(name = "기관코드", description = "")
        String insttCd;

        @Description(name = "기관명", description = "")
        String insttNm;

        @Description(name = "사업자번호", description = "")
        String bizno;

        @Description(name = "대표자명", description = "")
        String ceoNm;

        @Description(name = "전화번호", description = "")
        String telNo;

        @Description(name = "주소", description = "")
        String adrs;

        @Description(name = "ip", description = "")
        String ip;

        @Description(name = "업체구분코드", description = "")
        String corpDivCd;

        @Description(name = "order id", description = "")
        String orderOrgId;

        @Description(name = "등록자ID", description = "")
        String rgstrId;

        @Description(name = "등록일시", description = "")
        LocalDateTime rgstDt;

        @Description(name = "수정자ID", description = "")
        String chgId;

        @Description(name = "수정일시", description = "")
        LocalDateTime chgDt;

        String compNm;
    }
}
