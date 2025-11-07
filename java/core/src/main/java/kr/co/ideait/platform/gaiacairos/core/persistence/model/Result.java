package kr.co.ideait.platform.gaiacairos.core.persistence.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@JsonInclude(Include.NON_NULL)
public class Result implements Serializable {

    boolean ok = true;
    int code = 0;
    String message = "success";
    Long iat = new Date().getTime();
    Map<String, Object> details;

    public Result() {
    }

    public Result(Map<String, ?> details) {
        this.details = new HashMap<>(details);
    }

    public Result(boolean ok, int code, String message) {
        this.ok = ok;
        this.code = code;
        this.message = message;
    }

    public static Result ok() {
        return new Result();
    }

    public static Result ok(Map<String, ?> details) {
        return new Result(details);
    }

    public static Result nok(ErrorType errorType) {
        return new Result(false, errorType.getCode(), errorType.getMessage());
    }

    public static Result nok(ErrorType errorType, String message) {
        return new Result(false, errorType.getCode(), message);
    }

    public Result put(String k, Object v) {
        if (this.details == null) {
            this.details = new HashMap<>();
        }
        this.details.put(k, v);
        return this;
    }

    public Result put(String k, Page<?> v) {
        if (this.details == null) {
            this.details = new HashMap<>();
        }
        this.details.put(k, v);
        return this;
    }
}
