package kr.co.ideait.platform.gaiacairos.core.persistence.jpa;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import org.apache.commons.lang3.StringUtils;

public interface JpaLogicalDeleteable<T> {

	@SuppressWarnings("unchecked")
	default void updateDelete(T entity) {
		Class<T> entityClass = (Class<T>) entity.getClass();
		invokeSet(entityClass, entity, "setDltYn", String.class, "Y");
		invokeSet(entityClass, entity, "setDltId", String.class, UserAuth.get(true).getUsrId());
		invokeSet(entityClass, entity, "setDltDt", LocalDateTime.class, LocalDateTime.now());

		try {
			Class<?> thisClass = this.getClass();
			Method saveMethod = thisClass.getMethod("save", Object.class);
			saveMethod.invoke(this, entity);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new GaiaBizException(ErrorType.ETC, "delete fail",e);
		}
    }

	default void updateDelete(T entity, String userId) {
		Class<T> entityClass = (Class<T>) entity.getClass();
		invokeSet(entityClass, entity, "setDltYn", String.class, "Y");
		invokeSet(entityClass, entity, "setChgId", String.class, userId);
		invokeSet(entityClass, entity, "setChgDt", LocalDateTime.class, LocalDateTime.now());
		invokeSet(entityClass, entity, "setDltId", String.class, userId);
		invokeSet(entityClass, entity, "setDltDt", LocalDateTime.class, LocalDateTime.now());

		try {
			Class<?> thisClass = this.getClass();
			Method saveMethod = thisClass.getMethod("save", Object.class);
			saveMethod.invoke(this, entity);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new GaiaBizException(ErrorType.ETC, "delete fail",e);
		}
	}

	default <P> Object invokeGet(Class<T> entityClass, T entity, String method) {
		Object returnValue = null;

		try {
			Method mwthod = entityClass.getMethod(method);
			returnValue = (T)mwthod.invoke(entity);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new GaiaBizException(ErrorType.ETC, "set delete info fail");
		}


		return returnValue;
	}

	default <P> void invokeSet(Class<T> entityClass, T entity, String method, Class<P> paramClass, P param) {
		try {
			Method mwthod = entityClass.getMethod(method, paramClass);
			mwthod.invoke(entity, param);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new GaiaBizException(ErrorType.ETC, "set delete info fail");
		}
	}
}
