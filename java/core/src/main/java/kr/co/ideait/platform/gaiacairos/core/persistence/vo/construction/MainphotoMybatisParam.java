package kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction;

import java.util.List;

import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;

@Mapper(componentModel = ComponentModel.SPRING)
public interface MainphotoMybatisParam {

	@Data
	@Alias("mainphotoformTypeSelectInput")
	public class MainphotoFormTypeSelectInput extends MybatisPageable {
		String pjtNo;
		String cntrctNo;
		String cntrctId;
		String wbsCd;
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
	
}
