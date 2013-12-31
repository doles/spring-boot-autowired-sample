package com.in1.boot.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.ui.context.support.ResourceBundleThemeSource;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.theme.CookieThemeResolver;
import org.springframework.web.servlet.theme.ThemeChangeInterceptor;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles2.TilesConfigurer;
import org.springframework.web.servlet.view.tiles2.TilesView;
import org.thymeleaf.extras.springsecurity3.dialect.SpringSecurityDialect;
import org.thymeleaf.extras.tiles2.dialect.TilesDialect;
import org.thymeleaf.extras.tiles2.spring.web.configurer.ThymeleafTilesConfigurer;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import com.in1.boot.IntegrationsImcApplication;


@EnableWebMvc
@Configuration  
public class RootMvcConfiguration extends WebMvcConfigurerAdapter implements Ordered {

    final Logger logger = LoggerFactory.getLogger(getClass());
    
	@Override
	public int getOrder() {		
		return IntegrationsImcApplication.ROOT_MVC_CONFIG_ORDER;
	}

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        /*
         * Server static resources from the src/main/resources/webapp/static
         * directory. Perhaps rename resources to static.
         */
        registry.addResourceHandler("/static/**").addResourceLocations(
                "/static/");

        /*
         * Favicon mapping.
         */
        registry.addResourceHandler("/favicon.ico").addResourceLocations(
                "/static/images/favicon.ico");

    }
    
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
      
      registry.addInterceptor(new ThemeChangeInterceptor());
      
      LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
      localeChangeInterceptor.setParamName("lang");      
      registry.addInterceptor(localeChangeInterceptor);
   }
    
    
    //<mvc:default-servlet-handler/>
    //@Override
    //public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    //  configurer.enable("dispatcher-servlet");
    //}
    
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
     
      registry.addViewController("/").setViewName("index");
      registry.addViewController("/login").setViewName("login");     
      registry.addViewController("/uncaughtException").setViewName("uncaughtException");
      registry.addViewController("/resourceNotFound").setViewName("resourceNotFound");
      registry.addViewController("/dataAccessFailure").setViewName("dataAccessFailure");
    }
   
    
    
    
    //This bean resolves specific types of exceptions to corresponding logical - view names for error views. 
    //The default behaviour of DispatcherServlet - is to propagate all exceptions to the servlet container: 
    //this will happen - here with all other types of exceptions.
    @Bean
    public SimpleMappingExceptionResolver simpleMappingExceptionResolver(){
    	
    	Properties mappings = new Properties();
    	mappings.put(".DataAccessException", "dataAccessFailure");
    	mappings.put(".NoSuchRequestHandlingMethodException", "resourceNotFound");
    	mappings.put(".TypeMismatchException", "resourceNotFound");
    	mappings.put(".MissingServletRequestParameterException", "resourceNotFound");
    	
    	SimpleMappingExceptionResolver simpleMappingExceptionResolver = new SimpleMappingExceptionResolver();
    	simpleMappingExceptionResolver.setExceptionMappings(mappings);
    	
    	simpleMappingExceptionResolver.setDefaultErrorView("uncaughtException");
    	
    	return simpleMappingExceptionResolver;
    	
    }
    
    
    //LOCALE
    
    
    
    @Bean
    public MessageSource messageSource(){
    	
    	ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    	messageSource.setBasenames("classpath:META-INF/i18n/messages","classpath:META-INF/i18n/application");
    	messageSource.setFallbackToSystemLocale(false);
    	return messageSource;
    }
    
    //store preferred language configuration in a cookie 
    @Bean
    public CookieLocaleResolver localeResolver(){
    	
    	CookieLocaleResolver localeResolver =  new CookieLocaleResolver();
    	localeResolver.setCookieName("locale");
    	
    	return localeResolver;
    }
    
    //THEME
    
    //resolves localized <theme_name>.properties files in the classpath to allow for theme support
    @Bean
    public ResourceBundleThemeSource themeSource(){
    	
    	ResourceBundleThemeSource themeSource = new ResourceBundleThemeSource(); 
    	return themeSource;
    }
    
    //store preferred theme configuration in a cookie
    @Bean
    public CookieThemeResolver themeResolver(){
    	
    	CookieThemeResolver themeResolver = new CookieThemeResolver();
    	themeResolver.setCookieName("imc-theme");
    	themeResolver.setDefaultThemeName("standard");
    	
    	return themeResolver;
    	
    }
    
    
    //allows for integration of file upload functionality
    
    @Bean
    public CommonsMultipartResolver multipartResolver(){
    	
    	CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
    	return multipartResolver;
    	
    }
    
    //THEYMELEAF
    @Bean 
    public ServletContextTemplateResolver templateResolver() {
            ServletContextTemplateResolver resolver = new ServletContextTemplateResolver();
            resolver.setPrefix("/WEB-INF/");
            resolver.setSuffix(".html");
            resolver.setTemplateMode("HTML5");
            //resolver.setOrder(2);
            resolver.setCacheable(false);            
            return resolver;
    }
    
    @Bean 
    public SpringTemplateEngine templateEngine() throws Exception {
            SpringTemplateEngine engine = new SpringTemplateEngine();
            engine.setTemplateResolver(templateResolver());
            engine.setTemplateEngineMessageSource(messageSource());
            engine.addDialect(new TilesDialect());            
            engine.addDialect(new SpringSecurityDialect());            
            engine.afterPropertiesSet();
            return engine;
    }
    
    //TILES
    
    /**
     * Uncomment to use tiles with thymeleaf instead jstl
     
    @Bean 
    public ThymeleafViewResolver tilesViewResolver() throws Exception{
   	
    	ThymeleafViewResolver tilesViewResolver = new ThymeleafViewResolver();
   		tilesViewResolver.setViewClass(ThymeleafTilesView.class);//for th 2.1
   		tilesViewResolver.setTemplateEngine(templateEngine());
   		return tilesViewResolver;
   }
   
   **/
    
    /**
     * Old for JSTL - comment out  when using thymeleaf
     */
     @Bean 
     public UrlBasedViewResolver tilesViewResolver(){
    	
    	UrlBasedViewResolver tilesViewResolver = new UrlBasedViewResolver();
    	tilesViewResolver.setViewClass(TilesView.class);
    	return tilesViewResolver;
    }
    
    
    
    @Bean
    public TilesConfigurer tilesConfigurer(){ 
    	
    	String[] definitions = new String[] {
    			"/WEB-INF/layouts/layouts.xml",
    			"/WEB-INF/views/**/views.xml" /*Scans views directory for Tiles configurations */
    			};
    	
    	ThymeleafTilesConfigurer tilesConfigurer = new ThymeleafTilesConfigurer();
    	tilesConfigurer.setDefinitions(definitions);
    	//tilesConfigurer.afterPropertiesSet();
    	return tilesConfigurer;
    	
    }

}