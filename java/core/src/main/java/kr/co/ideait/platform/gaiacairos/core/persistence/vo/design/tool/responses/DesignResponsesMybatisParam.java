package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.responses;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.type.Alias;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtAttachments;
import lombok.Data;

public interface DesignResponsesMybatisParam {

    @Data
    @Alias("designResponsesInput")
    public class DesignResponsesInput {
        String resSeq;
        String dsgnNo;
    }

    @Data
    @Alias("designResponsesOutput")
    public class DesignResponsesOutput {
        String dsgnNo;
        String rplyCd;
        String rplyCntnts;
        Integer atchFileNo;
        Integer dwgNo;
        String rplyYn;

        List<DtAttachments> files;
    }

}
