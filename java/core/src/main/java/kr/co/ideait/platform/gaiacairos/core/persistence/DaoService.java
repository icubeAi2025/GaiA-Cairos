package kr.co.ideait.platform.gaiacairos.core.persistence;

import java.util.List;
import java.util.Map;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;

@Service
@Deprecated
public class DaoService {

	@Autowired @Qualifier("sqlSessionTemplate")
	SqlSessionTemplate mybatisSession;

	@Autowired
	JpaSession jpaSession;

	/**
	 * Mybatis
	 */
	public SqlSessionTemplate mybatis() {
		return mybatisSession;
	}

	/**
	 * JPA
	 */
	@Deprecated
	public JpaSession jpa() {
		return jpaSession;
	}

	public <E, ID> JpaRepository<E, ID> jpa(Class<E> entityClass) {
		return jpaSession.jpa(entityClass, null);
	}

	// 하단 method는 삭제예정입니다.
	// =======================================================================

	@Autowired
	@Deprecated
	List<JpaRepository<?, ?>> jpaRepositories;

	@Deprecated
	Map<Class<?>, JpaRepository<?, ?>> jpaRepositoriesMap;

	@Deprecated
	public <E> List<E> selectList(String queryId, Object parameterObject) {
		return parameterObject == null ? mybatisSession.selectList(queryId)
				: mybatisSession.selectList(queryId, parameterObject);
	}

	@Deprecated
	public <T> T selectOne(String queryId, Object parameterObject) {
		return parameterObject == null ? mybatisSession.selectOne(queryId)
				: mybatisSession.selectOne(queryId, parameterObject);
	}

	// public <T> T selectOneValue(String queryId, Object parameterObject) {
	// return mybatisSeesion.selectOneValue(queryId, parameterObject);
	// }

	@Deprecated
	public int insert(String queryId, Object parameterObject) {
		return mybatisSession.insert(queryId, parameterObject);
	}

	@Deprecated
	public int update(String queryId, Object parameterObject) {
		return mybatisSession.update(queryId, parameterObject);
	}

	@Deprecated
	public int delete(String queryId, Object parameterObject) {
		return mybatisSession.delete(queryId, parameterObject);
	}

	// public void executeProcedure(String queryId, Object parameterObject) {
	// mybatisSeesion.executeProcedure(queryId, parameterObject);
	// }

	/**
	 * JPA
	 */
	@Deprecated
	public <E> List<E> findAll(Class<E> entityClass) {
		return findRepository(entityClass).findAll();
	}

	@Deprecated
	public <E, ID> E findById(Class<E> entityClass, ID id) {
		return findRepository(entityClass).findById(id).orElse(null);
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public <E, ID> E findOne(Class<E> entityClass, Specification<E> spec) {
		JpaSpecificationExecutor<E> executor = (JpaSpecificationExecutor<E>) findRepository(entityClass);
		return executor.findOne(spec).orElse(null);
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public <E> List<E> findList(Class<E> entityClass, Specification<E> spec) {
		JpaSpecificationExecutor<E> executor = (JpaSpecificationExecutor<E>) findRepository(entityClass);
		return executor.findAll(spec);
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public <E> Page<E> findPage(Class<E> entityClass, Specification<E> spec, Pageable pageable) {
		JpaSpecificationExecutor<E> executor = (JpaSpecificationExecutor<E>) findRepository(entityClass);
		return executor.findAll(spec, pageable);
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public <E> E create(E entity) {
		Class<E> entityClass = (Class<E>) entity.getClass();
		return findRepository(entityClass).save(entity);
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public <E> E update(E entity) {
		Class<E> entityClass = (Class<E>) entity.getClass();
		return findRepository(entityClass).save(entity);
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public <E> E delete(E entity) {
		Class<E> entityClass = (Class<E>) entity.getClass();
		return findRepository(entityClass).save(entity);
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public <E> void tableDelete(E entity) {
		Class<E> entityClass = (Class<E>) entity.getClass();
		findRepository(entityClass).delete(entity);
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	private <E, ID> JpaRepository<E, ID> findRepository(Class<E> entityClass) {
		if (jpaRepositoriesMap.containsKey(entityClass)) {
			return (JpaRepository<E, ID>) jpaRepositoriesMap.get(entityClass);
		} else {
			throw new GaiaBizException(ErrorType.ETC, "not exist jpa repository for [" + entityClass + "]");
		}
	}
}
