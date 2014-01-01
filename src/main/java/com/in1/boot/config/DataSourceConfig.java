package com.in1.boot.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.TomcatDataSourceConfiguration;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;


@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)//equivalent of <tx:annotation-driven mode="aspectj" transaction-manager="transactionManager"/>, it will look up any PlatformTransactionManager bean
@Configuration
public class DataSourceConfig extends TomcatDataSourceConfiguration implements TransactionManagementConfigurer  {
    
    
    public static String JPA_PERSISTENCE_NAME = "persistenceUnit";
    public static String JPA_PERSISTENCE_LOCATION = "/META-INF/hibernate/persistence.xml";
	
    @Override
    public DataSource dataSource() {
    	return jpaDataSource();
    }
    
    public PlatformTransactionManager annotationDrivenTransactionManager() {
    	return jpaTransactionManager();
    }

    //@Bean
    //public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor(){
    //    return new PersistenceExceptionTranslationPostProcessor();
    //}
    /*
     * Required
     */
    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

	/**
	 * Bean that allows to get data base connection.
	 * 
	 * @return
	 */
	@Bean(name="jpaDataSource")
	public DataSource jpaDataSource() {
		org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
		dataSource.setDriverClassName(getDriverClassName());
		dataSource.setUrl(getUrl());
		dataSource.setUsername(getUsername());
		dataSource.setPassword(getPassword());
		dataSource.setMaxActive(30);
		dataSource.setMaxWait(60 * 1000);

		return dataSource;
	}

    /*
     * Only for JPA.
     */
    @Bean(name = {"transactionManager","txMgr"})
    public JpaTransactionManager jpaTransactionManager() {

        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(jpaEmf());
        return tm;

    }      

    @Bean(name = "entityManagerFactory")
    public EntityManagerFactory jpaEmf() {


        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(jpaDataSource());
        emf.setPersistenceUnitName(JPA_PERSISTENCE_NAME);
        emf.setPersistenceXmlLocation(JPA_PERSISTENCE_LOCATION); 
        emf.afterPropertiesSet();
        return emf.getObject();


    }



}