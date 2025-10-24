package kr.co.ideait.platform.gaiacairos.core.persistence.mybatis.system;

import kr.co.ideait.platform.gaiacairos.core.persistence.vo.portal.PortalMybatisParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface UserMapper {
    Map selectLoginUser(PortalMybatisParam.UserLoginInput param);
}
