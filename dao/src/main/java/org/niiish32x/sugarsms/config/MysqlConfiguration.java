package org.niiish32x.sugarsms.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.session.LocalCacheScope;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * MysqlConfiguration
 *
 * @author shenghao ni
 * @date 2024.12.16 19:50
 */
@Configuration
@MapperScan(basePackages = {"org.niiish32x.sugarsms.**.mapper"}, sqlSessionFactoryRef = "sessionFactory")
public class MysqlConfiguration {

//    @Bean
//    public OProperties mysqlProps() {
//        return new OProperties("mysql");
//    }
//
//    @Bean("mysql")
//    @Resource
//    @ConditionalOnMissingBean
//    public DataSource dataSource(OProperties mysqlProps) throws SQLException {
//        DruidDataSource mysql = new DruidDataSource();
//        mysql.setConnectProperties(mysqlProps.properties());
//        mysql.init();
//        return mysql;
//    }
//
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    @Resource
    public MybatisSqlSessionFactoryBean sessionFactory(DataSource dataSource, MybatisPlusInterceptor mybatisPlusInterceptor) {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        // 关闭全部缓存机制 (保证数据一致性)
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setCacheEnabled(false);
        configuration.setLocalCacheScope(LocalCacheScope.STATEMENT);
        // 关闭表字段名的非驼峰映射
        configuration.setMapUnderscoreToCamelCase(false);
        if (System.getProperty("sql.print", "0").equals("1")) {
            configuration.setLogImpl(StdOutImpl.class);
        }
        factory.setConfiguration(configuration);
        factory.setPlugins(mybatisPlusInterceptor);
        return factory;
    }
}