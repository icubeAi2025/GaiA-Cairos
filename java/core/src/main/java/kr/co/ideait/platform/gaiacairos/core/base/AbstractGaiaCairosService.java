package kr.co.ideait.platform.gaiacairos.core.base;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AbstractGaiaCairosService extends AbstractBase {
    @Autowired
    @Qualifier("sqlSessionTemplate")
    protected SqlSessionTemplate mybatisSession;

}
