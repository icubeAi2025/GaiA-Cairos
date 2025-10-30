package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.apilog;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

public interface ApiLogMybatisParam {

    @Data
    @Alias("apiLogListInput")
    @EqualsAndHashCode(callSuper = true)
    public class ApiLogListInput extends MybatisPageable {
        String apiSource;
        String target;
        String apiType;
        String apiId;
        String startDt;
        String endDt;
        String lang;
    }


    @Data
    @Alias("apiLogOutput")
    @EqualsAndHashCode(callSuper = true)
    public class ApiLogOutput extends MybatisPageable {
        Long apiLogNo;
        String sourceSystemCode;
        String targetSystemCode;
        String apiType;
        String serviceType;
        String apiId;
        String resultCode;
        String rgstDt;
    }
}