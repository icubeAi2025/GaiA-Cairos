package kr.co.ideait.platform.gaiacairos.core.persistence.model;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class GridResult {

    private boolean result;
    private GridData data;

    public GridResult(boolean result, GridData data) {
        this.result = result;
        this.data = data;
    }

    public static GridResult ok(Page<?> page) {
        GridData gridData = new GridData(page.getContent(), new Pagination(page.getNumber() + 1, page.getTotalElements()));
        return new GridResult(true, gridData);
    }

    //페이징 처리가 필요 없는 데이터 tui grid 응답 형태로 변환.
    public static GridResult ok(List<?> contents) {
        GridData gridData = new GridData(contents); //total 부분은 수정해야함.
        return new GridResult(true, gridData);
    }

    public static GridResult nok(ErrorType errorType) {
        return new GridResult(false, null);
    }

    @Data
    @NoArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class GridData {
        private Object contents;
        private Pagination pagination;

        public GridData(Object contents, Pagination pagination) {
            this.contents = contents;
            this.pagination = pagination;
        }

        public GridData(Object contents) {
            this.contents = contents;
        }
    }

    @Data
    @NoArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class Pagination {
        private int page;
        private long totalCount;

        public Pagination(int page, long totalCount) {
            this.page = page;
            this.totalCount = totalCount;
        }
    }
}