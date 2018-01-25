package com.xinshan;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.NestedIOException;

import java.io.IOException;

/**
 * Created by xu on 15-9-7.
 */
public class MyBatisSqlSessionFactoryBean extends SqlSessionFactoryBean {
    @Override
    public SqlSessionFactory buildSqlSessionFactory() throws IOException{
        try {
            return super.buildSqlSessionFactory();
        } catch (NestedIOException e) {
            e.printStackTrace(); // XML 有错误时打印异常。
            throw new NestedIOException("Failed to parse mapping resource: ", e);
        } finally {
//            ErrorContext.instance().reset();
        }
    }
}
