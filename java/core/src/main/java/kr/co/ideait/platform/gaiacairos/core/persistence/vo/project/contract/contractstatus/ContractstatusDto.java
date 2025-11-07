package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContract;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import lombok.Data;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface ContractstatusDto {

	Contract fromCnContractOutput(ContractstatusMybatisParam.ContractstatusOutput contractstatusOutput);

	Contract toContract(CnContract contract);

	List<Contract> toContractList(List<CnContract> list);

	@Data
	class Contract {
		String cntrctNo; // 계약번호
		String pjtNo; // 프로젝트번호
		String cntrctNm; // 공사계약명
		String mngCntrctNo; // 계약번호(관리계약번호)
		String cntrctType; // 계약종류
		String majorCnsttyCd; // 주공종코드
		String majorCnsttyNmKrn;
		String cntrctDate; // 계약일자
		String grntyDate; // 보증일
		String cbgnDate; // 착공일자
		String ccmpltDate; // 준공일자

		double conPrd; // 공사기간
		double cntrctCost; // 계약금액
		double grntyCost; // 보증금
		double vatRate; // 부가세율 -> 소수점3자리
		double dfrcmpnstRate; // 지체상금율 -> 소수점3자리

		String bsnsmnNo; // 사업자등록번호
		String corpNm; // 업체명(대표계약 회사명)
		String corpAdrs; // 업체주소
		String corpNo;
		String telNo; // 전화번호(사무실 번호)
		String faxNo; // 팩스번호
		String corpCeo; // 업체대표자
		String ofclNm; // 담당자이름
		String ofclId; // 담당자이름
		String latestCntrctChgDate; // 계약변경일자

		String thisCcmpltDate; // 금차준공일자
		double thisConPrd; // 금차공사기간
		double thisCntrctCost; // 금차계약금액
		String cntrctDivCd; // 계약구분 코드
	}
}
