package kr.co.ideait.platform.gaiacairos.web.config;

import com.ibatis.common.resources.Resources;
import kr.co.ideait.iframework.mybatis.MybatisLoggingInterceptor;
import kr.co.ideait.platform.gaiacairos.core.config.mybatis.LocalDateTimeTypeHandler;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.type.Alias;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.time.LocalDateTime;

@Configuration
@MapperScan(basePackages = {
        MybatisConfig.DB_PACKAGE
}, sqlSessionFactoryRef = "sqlSessionFactory", lazyInitialization = "false")
public class MybatisConfig {

    @Value("classpath*:/mybatis/mappers/**/*.sqlx")
    private Resource[] mapperLocations;

    public final static String DB_PACKAGE = "kr.co.ideait.platform.gaiacairos.core.persistence.mybatis.**";

    @Bean(name="sqlSessionTemplate")
    public SqlSessionTemplate mybatisSession(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setPlugins(new MybatisLoggingInterceptor());
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(mapperLocations);
        sessionFactory.setConfiguration(getConfiguration());
        return new SqlSessionTemplate(sessionFactory.getObject());
    }


    @Bean(name="icsSqlSessionTemplate")
    public SqlSessionTemplate icsmybatisSession(@Qualifier("icsDataSource") DataSource icsDataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setPlugins(new MybatisLoggingInterceptor());
        sessionFactory.setDataSource(icsDataSource);
        sessionFactory.setMapperLocations(mapperLocations);
        sessionFactory.setConfiguration(getConfiguration());
        return new SqlSessionTemplate(sessionFactory.getObject());
    }

    @Bean(name="pccsSqlSessionTemplate")
    public SqlSessionTemplate pccsmybatisSession(@Qualifier("pccsDataSource") DataSource pccsDataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setPlugins(new MybatisLoggingInterceptor());
        sessionFactory.setDataSource(pccsDataSource);
        sessionFactory.setMapperLocations(mapperLocations);
        sessionFactory.setConfiguration(getConfiguration());
        return new SqlSessionTemplate(sessionFactory.getObject());
    }

    public org.apache.ibatis.session.Configuration getConfiguration() throws ClassNotFoundException, IOException {
        org.apache.ibatis.session.Configuration config = new org.apache.ibatis.session.Configuration();
        config.setLazyLoadingEnabled(true);
        config.setDefaultFetchSize(50);
        config.setDefaultExecutorType(ExecutorType.REUSE);
        config.setJdbcTypeForNull(JdbcType.VARCHAR);
        config.setCallSettersOnNulls(true);
//        config.setLogImpl(Slf4jImpl.class);
        config.setMapUnderscoreToCamelCase(true);
        config.getTypeHandlerRegistry().register(LocalDateTime.class,LocalDateTimeTypeHandler.class);

        String path = "kr.co.ideait.platform.gaiacairos".replace('.', '/');
        PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
        CachingMetadataReaderFactory meta = new CachingMetadataReaderFactory();
        Resource[] resources = pathResolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + path + "/**/*.class");

        for (Resource resource : resources) {
            ClassMetadata classMetadata = meta.getMetadataReader(resource).getClassMetadata();
            Class<?> clazz = Resources.classForName(classMetadata.getClassName());
            if (clazz.isAnnotationPresent(Alias.class)) {
                Alias alias = clazz.getAnnotation(Alias.class);
                config.getTypeAliasRegistry().registerAlias(alias.value(), clazz);
            }
        }

        return config;
    }

    /*
     *********** 참고
     *
     * <settings>
     * <setting name="lazyLoadingEnabled" value="true" />
     * <setting name="defaultFetchSize" value="50" />
     * <setting name="defaultExecutorType" value="REUSE" />
     * <setting name="jdbcTypeForNull" value="VARCHAR" />
     * <!-- <setting name="jdbcTypeForNull" value="NULL" /> -->
     * <setting name="callSettersOnNulls" value="true" />
     * <!-- <setting name="autoMappingBehavior" value="FULL"/> -->
     * <!-- <setting name="mapUnderscoreToCamelCase" value="false"/> -->
     * </settings>
     *
     * <typeAliases>
     * <typeAlias alias="omap" type="java.util.LinkedHashMap" />
     * <!-- <typeAlias alias="resultmap" type="iframework.LowerKeyMap" /> -->
     * <!-- <typeAlias alias="resultmap" type="iframework.mybatis.MyResultMap" />
     * -->
     * <typeAlias alias="camelmap" type="iframework.CamelKeyMap" />
     * </typeAliases>
     *
     * <!-- data_default 이거 안하면 오류남(X) 하나 안하나 똑같네 -->
     * <typeHandlers>
     * <!-- sql parameter set 할때 empty string 을 null 로 set 하게 하자. -->
     * <typeHandler javaType="string"
     * handler="iframework.mybatis.EmptyStringToNullParameterTypeHandler" />
     * </typeHandlers>
     *
     */

}
