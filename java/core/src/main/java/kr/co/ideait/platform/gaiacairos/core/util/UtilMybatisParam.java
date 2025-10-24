package kr.co.ideait.platform.gaiacairos.core.util;

import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;

import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;

@Mapper(config = GlobalMapperConfig.class)
public interface UtilMybatisParam {
	
	@Data
	@Alias("comCodeSelectInput")
	public class ComCodeSelectInput extends MybatisPageable {
		String cmnGrpCd;
        String orderByCol;
        String orderByType;
	}
	
	@Data
	@Alias("makeDatatInput")
	public class MakeDatatInput extends MybatisPageable {
		String col1;
        String col2;
		String tName;
        String addSql;
		String orderByCol;
        String orderByType;
	}	
}
