package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project;

import org.apache.ibatis.type.Alias;

import lombok.Data;

public class ProjectMybatisParam {

        @Data
        @Alias("projectInstallInput")
        public static class ProjectInstallInput {
            String platformType;
            String openPstatsGroupCode;
        }

        @Data
        @Alias("projectInstallOutput")
        public static class ProjectInstallOutput {
            String plcReqNo;
            String plcNm;
            String openPstatsNm;
            String openPstats;

        }
}
