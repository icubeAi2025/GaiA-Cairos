package kr.co.ideait.platform.gaiacairos.core.persistence.model;

import lombok.Data;

@Data
public class Limit {
    private Integer limit;
    private Integer offset;

    public Limit(Integer limit, Integer offset) {
        this.limit = limit;
        this.offset = offset;
    }
}
