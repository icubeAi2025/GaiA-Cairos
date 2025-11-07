package kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.wbs;

import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;

import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import lombok.Data;

@Mapper(config = GlobalMapperConfig.class)
public interface WbsMybatisParam {

    @Data
    @Alias("wbsOutput")
    public class WbsOutput {

        String cntrctChgId;
        String revisionId;
        String wbsCd;
        String wbsPath;
        String wbsNm;
        String upWbsCd;
        Integer wbsLevel;
        String earlyStart;
        String earlyFinish;
        String actualStart;
        String actualFinish;
        String rmrk;
        String dltYn;

    }

    @Data
    @Alias("wbsListInput")
    public class WbsListInput {

        String listType;
        String searchText;
        String cntrctChgId;
        String upWbsCd;

    }
}
