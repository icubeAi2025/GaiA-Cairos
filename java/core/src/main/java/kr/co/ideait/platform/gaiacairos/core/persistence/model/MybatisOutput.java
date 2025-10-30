package kr.co.ideait.platform.gaiacairos.core.persistence.model;

import java.util.HashMap;

import org.apache.ibatis.type.Alias;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Alias("output")
public class MybatisOutput extends HashMap<String, Object> {

}
