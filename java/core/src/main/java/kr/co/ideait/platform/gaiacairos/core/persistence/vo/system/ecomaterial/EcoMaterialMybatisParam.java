package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.ecomaterial;

import org.apache.ibatis.type.Alias;

import lombok.Data;

public interface EcoMaterialMybatisParam {
    @Data
    @Alias("ecoMaterialListInput")
    public class EcoMaterialListInput {
        String cntrctNo;
        String searchTerm;
        String searchText;
    }

    @Data
    @Alias("ecoMaterialListOutput")
    public class EcoMaterialListOutput {
        String ecoId;
        String pjtNo;
        String pjtNm;
        String cntrctNo;
        String cntrctNm;
        String gnrlexpnsCd;
        String rsceNm;
        String specNm;
        String unit;
        String ecoTpCd;
        String preCert;
        String finalCert;
        String makrNm;
        String certRsn;
        String rmrk;
        String dltYn;

        String ecoTpCdNm;

        String rsceCd;
        String dtlCnsttyNm;
        String govsplyMtrlYn;
    }
}
