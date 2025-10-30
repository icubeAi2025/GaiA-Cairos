package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.information;

import lombok.Data;

import java.util.List;

@Data
public class InformationWithFilesDTO {
    private InformationMybatisParam.InformationOutput informationOutput;
    private InformationDto.infoAttachMent attachment;
}
