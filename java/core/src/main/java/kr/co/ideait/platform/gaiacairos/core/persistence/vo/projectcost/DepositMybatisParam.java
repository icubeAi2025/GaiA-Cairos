package kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost;

import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;

@Mapper(componentModel = ComponentModel.SPRING)
public interface DepositMybatisParam {

	@Data
	@Alias("depositformTypeSelectInput")
	public class DepositFormTypeSelectInput extends MybatisPageable {
		String cntrctNo;
		Long payprceSno;
		Long ppaymnySno;
//		String cntrctId;
//		String chgId;
//		String id;
//		String cd;
//		String type;
//		Integer cnsttySn;
//		List<Integer> cnsttySnList;
//		String searchText;
//		String cntrctChgId;
	}
	
}
