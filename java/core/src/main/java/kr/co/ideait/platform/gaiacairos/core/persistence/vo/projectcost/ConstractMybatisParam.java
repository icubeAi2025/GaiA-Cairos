package kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost;

import java.util.List;

import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;

@Mapper(componentModel = ComponentModel.SPRING)
public interface ConstractMybatisParam {

	@Data
	@Alias("constractformTypetSelectInput")
	public class ConstractFormTypeSelectInput extends MybatisPageable {
		String cntrctNo;
		String cntrctId;
		String chgId;
		Integer cnsttySn;
		List<Integer> cnsttySnList;
		String searchText;
	}
	
}
