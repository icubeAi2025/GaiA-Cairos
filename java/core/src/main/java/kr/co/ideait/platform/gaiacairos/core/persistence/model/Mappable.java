package kr.co.ideait.platform.gaiacairos.core.persistence.model;

import java.util.function.Function;

public interface Mappable {

    @SuppressWarnings("unchecked")
    default <T, R> R map(Function<T, R> mapper) {
        return mapper.apply((T) this);
    }
}
