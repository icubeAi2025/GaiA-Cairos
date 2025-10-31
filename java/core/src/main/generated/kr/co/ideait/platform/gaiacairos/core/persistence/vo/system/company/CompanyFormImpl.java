package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmCompany;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class CompanyFormImpl implements CompanyForm {

    @Override
    public CompanyMybatisParam.CompanyListInput toCompanyListInput(CompanyListGet companyListGet) {
        if ( companyListGet == null ) {
            return null;
        }

        CompanyMybatisParam.CompanyListInput companyListInput = new CompanyMybatisParam.CompanyListInput();

        companyListInput.setPageable( companyListGet.getPageable() );
        companyListInput.setPlatform( companyListGet.getPlatform() );
        companyListInput.setType( companyListGet.getType() );
        companyListInput.setColumn( companyListGet.getColumn() );
        companyListInput.setKeyword( companyListGet.getKeyword() );
        List<Map<String, String>> list = companyListGet.getCompGrpCdList();
        if ( list != null ) {
            companyListInput.setCompGrpCdList( new ArrayList<Map<String, String>>( list ) );
        }

        return companyListInput;
    }

    @Override
    public CompanyMybatisParam.UserCompanyListInput toUserCompanyListInput(UserCompanyListGet userCompanyListGet) {
        if ( userCompanyListGet == null ) {
            return null;
        }

        CompanyMybatisParam.UserCompanyListInput userCompanyListInput = new CompanyMybatisParam.UserCompanyListInput();

        userCompanyListInput.setSearchGroup( userCompanyListGet.getSearchGroup() );

        return userCompanyListInput;
    }

    @Override
    public SmCompany toSmCompany(Company company) {
        if ( company == null ) {
            return null;
        }

        SmCompany smCompany = new SmCompany();

        smCompany.setCorpNo( company.getCorpNo() );
        smCompany.setCompGrpCd( company.getCompGrpCd() );
        smCompany.setCompNm( company.getCompNm() );
        smCompany.setBsnsmnNo( company.getBsnsmnNo() );
        smCompany.setCorpCeo( company.getCorpCeo() );
        smCompany.setCompDscrpt( company.getCompDscrpt() );
        smCompany.setPstnNm( company.getPstnNm() );
        smCompany.setMngNm( company.getMngNm() );
        smCompany.setCompTelno( company.getCompTelno() );
        smCompany.setCompFaxno( company.getCompFaxno() );
        smCompany.setCompAdrs( company.getCompAdrs() );
        smCompany.setUseYn( company.getUseYn() );
        smCompany.setDltYn( company.getDltYn() );

        return smCompany;
    }

    @Override
    public List<SmCompany> toSmCompanyList(List<Company> companyList) {
        if ( companyList == null ) {
            return null;
        }

        List<SmCompany> list = new ArrayList<SmCompany>( companyList.size() );
        for ( Company company : companyList ) {
            list.add( toSmCompany( company ) );
        }

        return list;
    }

    @Override
    public void updateSmCompany(CompanyUpdate companyUpdate, SmCompany smCompany) {
        if ( companyUpdate == null ) {
            return;
        }

        smCompany.setCorpNo( companyUpdate.getCorpNo() );
        smCompany.setCompGrpCd( companyUpdate.getCompGrpCd() );
        smCompany.setCompNm( companyUpdate.getCompNm() );
        smCompany.setBsnsmnNo( companyUpdate.getBsnsmnNo() );
        smCompany.setCorpCeo( companyUpdate.getCorpCeo() );
        smCompany.setCompDscrpt( companyUpdate.getCompDscrpt() );
        smCompany.setPstnNm( companyUpdate.getPstnNm() );
        smCompany.setMngNm( companyUpdate.getMngNm() );
        smCompany.setCompTelno( companyUpdate.getCompTelno() );
        smCompany.setCompFaxno( companyUpdate.getCompFaxno() );
        smCompany.setCompAdrs( companyUpdate.getCompAdrs() );
        smCompany.setUseYn( companyUpdate.getUseYn() );
    }
}
