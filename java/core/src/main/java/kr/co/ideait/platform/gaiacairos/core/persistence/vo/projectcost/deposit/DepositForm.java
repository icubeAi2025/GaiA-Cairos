package kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost.deposit;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwFrontMoney;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import lombok.ToString;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface DepositForm {
	
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
     * 기성 등록
     */
	CwFrontMoney DepositInsert(DepositInsert depositInsert);

	/**
     * 기성 삭제
     */
    @Data
    public class DepositList {
        List<CwFrontMoney> DepositList;
    }

/*
    @Data
    class PaymentUpdate {
		String cntrctNo;
        String payprceSno;
        String apprvlStats;
    }

    @Data
    class PaymentUpdateList {
        List<PaymentUpdate> paymentUpdateList;
    }
    */
	/**
     * 선급금 지급 수정
     */
    void toUpdateCwFrontMoney(DepositInsert deposit, @MappingTarget CwFrontMoney cwFrontMoney);
    
	/**
     * 내역서 메인 폼
     */
	@Data
    public class ProjectcostMain {
		String pjtNo;
		String cntrctNo;
        String cntrctId;
        String chgId;
    }
	
    /**
     * 코드, 품명, 규격 검색 폼
     */
    @Data
    @ToString
    public class ProjectcostListGet extends CommonForm{
        String cntrctId;
        String chgId;
		List<Integer> cnsttySnList;
        String searchText;
    }
	
    /**
     * 내역서 상세 폼
     */
    @Data
    public class ProjectcostDetailGet extends CommonForm{
        String cntrctNo;
        Long payprceSno;
        Long ppaymnySno;
    }

    @Data
    public class DepositInsert extends CommonForm {
        String cntrctNo;
        Long ppaymnySno;
        Long payprceSno;
        String ocrnceDate;
        String payType;

        Long ppaymnyAmt;
        Long ppaymnyCacltAmt;
        Long dfrcmpnstAmt;
        Long ppaymnyRemndrAmt;

        String rmrk;

        String dltYn;
        
        String apprvlStats;
        String apprvlReqId;
        LocalDateTime apprvlReqDt;
        String apprvlId;
        LocalDateTime apprvlDt;
        String searchText;

        Long diffAmt;
        String oriType;
        Long oriAmt;
    }


}
