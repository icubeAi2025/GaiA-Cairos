package kr.co.ideait.platform.gaiacairos.core.persistence.jpa;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;

import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JpaSession implements InitializingBean {

	@Autowired
	List<JpaRepository<?, ?>> jpaRepositories;

	Map<Class<?>, JpaRepository<?, ?>> jpaRepositoriesMap = new HashMap<Class<?>, JpaRepository<?, ?>>();

	/**
	 * JPA 사용은 아래 기능으로 제한합니다.
	 */
	public <E> long count(Class<E> entityClass) {
		return jpa(entityClass).count();
	}

	public <E> long count(Class<E> entityClass, Example<E> example) {
		return jpa(entityClass).count(example);
	}

	public <E> void delete(E entity) {
		jpa(entity).delete(entity);
	}

	public <E, ID> void delete(Class<E> entityClass, ID id) {
		// E entity = findByIdOrNull(entityClass, id);
		JpaRepository<E, ID> jpa = jpa(entityClass, id);
		E entity = jpa.findById(id).orElse(null);
		if (entity != null) {
			invokeSave(jpa, entityClass, entity, "setDltYn", String.class, "Y");
			invokeSave(jpa, entityClass, entity, "setDltId", String.class, "idformauth");
			invokeSave(jpa, entityClass, entity, "setDltDt", LocalDateTime.class, LocalDateTime.now());
			jpa.save(entity);
		}
	}

	private <E, P, ID> void invokeSave(JpaRepository<E, ID> jpa, Class<E> entityClass, E entity, String method,
			Class<P> paramClass, P param) {
		try {
			Method mwthod = entityClass.getMethod(method, paramClass);
			mwthod.invoke(entity, param);
		} catch (NoSuchMethodException e) {
			log.debug("not required");
		} catch (Exception e) {
			throw new GaiaBizException(ErrorType.ETC, "set delete info fail");
		}
	}

	public <E, ID> void deleteReal(Class<E> entityClass, ID id) {
		jpa(entityClass, id).deleteById(id);
	}

	public <E> boolean exists(Class<E> entityClass, Example<E> example) {
		return jpa(entityClass).exists(example);
	}

	public <E> List<E> findAll(Class<E> entityClass) {
		return jpa(entityClass).findAll();
	}

	public <E> Page<E> findAll(Class<E> entityClass, Pageable pageable) {
		return jpa(entityClass).findAll(pageable);
	}

	public <E> List<E> findAll(Class<E> entityClass, Example<E> example) {
		return jpa(entityClass).findAll(example);
	}

	public <E> List<E> findAll(Class<E> entityClass, Example<E> example, Sort sort) {
		return jpa(entityClass).findAll(example, sort);
	}

	public <E> Page<E> findAll(Class<E> entityClass, Example<E> example, Pageable pageable) {
		return jpa(entityClass).findAll(example, pageable);
	}

	public <E, R> R findAll(Class<E> entityClass, Example<E> example,
			Function<FluentQuery.FetchableFluentQuery<E>, R> queryFunction) {
		return jpa(entityClass).findBy(example, queryFunction);
	}

	public <E, ID> E findByIdOrNull(Class<E> entityClass, ID id) {
		return jpa(entityClass, id).findById(id).orElse(null);
	}

	public <E> E findOneOrNull(Class<E> entityClass, Example<E> example) {
		return jpa(entityClass).findOne(example).orElse(null);
	}

	public <E> E save(E entity) {
		return jpa(entity).save(entity);
	}

	public <E> E saveAndFlush(E entity) {
		return jpa(entity).saveAndFlush(entity);
	}

	public <E> List<E> saveAll(List<E> entities) {
		if (entities != null && entities.size() > 0) {
			return jpa(entities.getFirst()).saveAll(entities);
		} else {
			return new ArrayList<E>();
		}
	}

	public <E> List<E> saveAllAndFlush(List<E> entities) {
		if (entities != null && entities.size() > 0) {
			return jpa(entities.getFirst()).saveAllAndFlush(entities);
		} else {
			return new ArrayList<E>();
		}
	}

	/**
	 * specification을 이용한 JPA 사용은 아래 기능으로 제한합니다.
	 */
	public <E> long count(Class<E> entityClass, Specification<E> spec) {
		return jpaspec(entityClass).count(spec);
	}

	public <E> long delete(Class<E> entityClass, Specification<E> spec) {
		return jpaspec(entityClass).delete(spec);
	}

	public <E> boolean exists(Class<E> entityClass, Specification<E> spec) {
		return jpaspec(entityClass).exists(spec);
	}

	public <E> E findOneOrNull(Class<E> entityClass, Specification<E> spec) {
		return jpaspec(entityClass).findOne(spec).orElse(null);
	}

	public <E> List<E> findAll(Class<E> entityClass, Specification<E> spec) {
		return jpaspec(entityClass).findAll(spec);
	}

	public <E> List<E> findAll(Class<E> entityClass, Specification<E> spec, Sort sort) {
		return jpaspec(entityClass).findAll(spec, sort);
	}

	public <E> Page<E> findAll(Class<E> entityClass, Specification<E> spec, Pageable pageable) {
		return jpaspec(entityClass).findAll(spec, pageable);
	}

	public <E, R> R findAll(Class<E> entityClass, Specification<E> spec,
			Function<FluentQuery.FetchableFluentQuery<E>, R> queryFunction) {
		return jpaspec(entityClass).findBy(spec, queryFunction);
	}

	public <E> JpaRepository<E, ?> jpa(Class<E> entityClass) {
		return findRepository(entityClass);
	}

	@SuppressWarnings("unchecked")
	public <E> JpaRepository<E, ?> jpa(E entity) {
		Class<E> entityClass = (Class<E>) entity.getClass();
		return findRepository(entityClass);
	}

	public <E, ID> JpaRepository<E, ID> jpa(Class<E> entityClass, ID id) {
		return findRepository(entityClass);
	}

	@SuppressWarnings("unchecked")
	public <E> JpaSpecificationExecutor<E> jpaspec(Class<E> entityClass) {
		return (JpaSpecificationExecutor<E>) findRepository(entityClass);
	}

	@Override
	public void afterPropertiesSet() {
		for (JpaRepository<?, ?> jpaRepository : jpaRepositories) {
			if (JpaSpecificationExecutor.class.isInstance(jpaRepository)) {
				jpaRepositoriesMap.put(getRepositoryEntityClass(jpaRepository), jpaRepository);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <E, ID> JpaRepository<E, ID> findRepository(Class<E> entityClass) {
		if (jpaRepositoriesMap.containsKey(entityClass)) {
			return (JpaRepository<E, ID>) jpaRepositoriesMap.get(entityClass);
		} else {
			throw new GaiaBizException(ErrorType.ETC, "not exist jpa repository for [" + entityClass + "]");
		}
	}

	private Class<?> getRepositoryEntityClass(JpaRepository<?, ?> repository) {
		Type[] genericInterfaces = repository.getClass().getInterfaces()[0].getGenericInterfaces();
		for (Type genericInterface : genericInterfaces) {
			if (genericInterface instanceof ParameterizedType parameterizedType) {
				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				return (Class<?>) actualTypeArguments[0];
			}
		}
		throw new IllegalArgumentException("Unable to determine entity class");
	}
}
