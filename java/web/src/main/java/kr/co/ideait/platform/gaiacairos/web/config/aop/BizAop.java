package kr.co.ideait.platform.gaiacairos.web.config.aop;

import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.platform.gaiacairos.core.annotation.RequiredProjectSelect;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;

@Aspect
@Component
public class BizAop {

    // 프로젝트나 계약을 선택하지 않은경우는 예외를 발생시킨다.
    @Before("@annotation(kr.co.ideait.platform.gaiacairos.core.annotation.RequiredProjectSelect)")
    public void beforeRequiredSelect(JoinPoint jp) {
        if (!UserAuth.get(true).isSelected()) {
            throw new GaiaBizException(ErrorType.NOT_SELECTED);
        }

        MethodSignature signature = (MethodSignature) jp.getSignature();
        RequiredProjectSelect requiredProjectSelect = signature.getMethod().getAnnotation(RequiredProjectSelect.class);
        if (requiredProjectSelect.superChangeable()) {
            beforeSuperChangeable(jp);
        }
    }

    // 특정 파라미터에 값이 있고 관리자이면 해당 값으로 권한변경한다.
    @SuppressWarnings("null")
    @Before("@annotation(kr.co.ideait.platform.gaiacairos.core.annotation.SuperChangeable)")
    public void beforeSuperChangeable(JoinPoint jp) {
        Object[] args = jp.getArgs();
        boolean hasCommonForm = false;
        for (Object arg : args) {
            if (arg instanceof CommonForm form) {
                form.adminSuperChange();
                hasCommonForm = true;
                break;
            }
        }
        if (!hasCommonForm) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (servletRequestAttributes != null) {
                HttpServletRequest request = servletRequestAttributes.getRequest();
                String superProjectNo = request.getParameter("superProjectNo");
                String superContractNo = request.getParameter("superContractNo");
                UserAuth.get(true).superChange(superProjectNo, superContractNo);
            }
        }
    }
}
