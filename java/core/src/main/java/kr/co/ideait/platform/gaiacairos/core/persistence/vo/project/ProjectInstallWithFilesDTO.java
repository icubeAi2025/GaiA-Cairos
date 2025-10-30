package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project;

import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.ProjectDto.CnaAttachMent;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.ProjectDto.ProjectInstall;
import lombok.Data;

import java.util.List;

@Data
public class ProjectInstallWithFilesDTO {
    private ProjectInstall projectInstall;
    private List<CnaAttachMent> attachments;
}
