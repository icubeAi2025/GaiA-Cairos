package kr.co.ideait.platform.gaiacairos.core.persistence.model;

import org.springframework.data.domain.Pageable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MybatisPageable extends CommonForm {
    Long total;
    Pageable pageable;
}
