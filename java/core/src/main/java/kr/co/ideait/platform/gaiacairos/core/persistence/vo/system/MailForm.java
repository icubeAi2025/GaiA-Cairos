package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnUseRequest;
import lombok.Data;

@Mapper(componentModel = ComponentModel.SPRING)
public interface MailForm {

    CnUseRequest toUseRequest(useRequest uRequest);

    /**
     * 사용요청
     */
    @Data
    public class useRequest {
        String pjtNo;
        String pjtNm;
        String cntrctNo;
        String cntrctNm;
        String loginId;   
        String usrNm;   
        String position;
        String content;
    }
}
