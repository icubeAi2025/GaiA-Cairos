package kr.co.ideait.platform.gaiacairos.core.base;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AbstractWbsGenService extends AbstractBase {
    @Autowired
    @Qualifier("wbsGenSqlSessionTemplate")
    protected SqlSessionTemplate mybatisSession;

    @Autowired
    @Qualifier("sqlSessionTemplate")
    protected SqlSessionTemplate gaiaCairosMybatisSession;

    @Autowired
    @Qualifier("pccsSqlSessionTemplate")
    protected SqlSessionTemplate oraMybatisSession;
}
