package kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.resource;

import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ResourceForm {

    /*
     * @Null null만 허용한다.
     *
     * @NotNull 빈 문자열(""), 공백(" ")은 허용하되, Null은 허용하지 않음
     *
     * @NotEmpty 공백(" ")은 허용하되, Null과 빈 문자열("")은 허용하지 않음
     *
     * @NotBlank null, 빈 문자열(""), 공백(" ") 모두 허용하지 않는다.
     *
     * @Email 이메일 형식을 검사한다. 단, 빈 문자열("")의 경우엔 통과 시킨다. ( @Pattern을 통한 정규식 검사를 더 많이 사용
     *
     * @Pattern(regexp = ) 정규식 검사할 때 사용한다.
     *
     * @Size(min=, max=) 길이를 제한할 때 사용한다.
     *
     * @Max(value = ) value 이하의 값만 허용한다.
     *
     * @Min(value = ) value 이상의 값만 허용한다.
     *
     * @Positive 값을 양수로 제한한다.
     *
     * @PositiveOrZero 값을 양수와 0만 가능하도록 제한한다.
     *
     * @Negative 값을 음수로 제한한다.
     *
     * @NegativeOrZero 값을 음수와 0만 가능하도록 제한한다.
     *
     * @Future Now 보다 미래의 날짜, 시간이어야 한다.
     *
     * @FutureOrPresent Now 거나 미래의 날짜, 시간이어야 한다.
     *
     * @Past Now 보다 과거의 날짜, 시간이어야 한다.
     *
     * @PastFutureOrPresent Now 거나 과거의 날짜, 시간이어야 한다.
     */

    /**
     * 메인 폼
     */
    @Data
    public class ResourceMain {
        String pjtNo;
        String cntrctNo;
        String cntrctChgId;
        String currentMonth;
        String currentDay;
        String rsceCd;
        String rsceTpCd;
        String searchText;
    }
}
