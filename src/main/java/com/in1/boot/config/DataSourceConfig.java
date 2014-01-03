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
    
    
    public PlatformTransactionManager annotationDrivenTransactionManager() {
    	return jpaTransactionManager();
    }    
    
    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    } 
    
	@Bean(name="jpaDataSource")
	public DataSource jpaDataSource() {
		return this.dataSource();
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