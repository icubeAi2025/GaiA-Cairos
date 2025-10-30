package kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost;

import java.util.List;

import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;

@Mapper(componentModel = ComponentModel.SPRING)
public interface PaymentMybatisParam {

	@Data
	@Alias("paymentformTypeSelectInput")
	public class PaymentFormTypeSelectInput extends MybatisPageable {
		String pjtNo;
		String cntrctNo;
		String cntrctId;
		String chgId;
		String id;
		String cd;
		String type;
		Integer cnsttySn;
		List<Integer> cnsttySnList;
		String searchText;
		Long payprceSno;
		String cntrctChgId;
	}

	@Data
	@Alias("paymentHistorySelectInput")
	public class PaymentHistorySelectInput extends MybatisPageable {
		String cntrctNo;
		Long payprceSno;
		String dailyReportDate;
	}

	@Data
	@Alias("paymentHistory")
	class PaymentHistory {
		// 내역순번. 유레카에서 생성한 내역순번(내역식별번호)
		String dtlsSn;

		// 금회기성수량
		String thtmAcomQty;
	}
}
