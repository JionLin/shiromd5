package com.jiaolin.shiro.config;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.LinkedHashMap;

/**
 * @program: shiro
 * @description: shiro配置
 * @author: Join
 * @create: 2018-12-10 23:22
 **/
@Configuration
public class ShiroConfig {

    /**
     * @Description: DefaultFilter 默认拦截器对应的类
     * anon(AnonymousFilter.class), 匿名访问
     * authc(FormAuthenticationFilter.class), 表示需要认证才能使用
     * authcBasic(BasicHttpAuthenticationFilter.class),
     * logout(LogoutFilter.class), 登出
     * noSessionCreation(NoSessionCreationFilter.class),
     * perms(PermissionsAuthorizationFilter.class), 权限
     * /admins/user/**=perms[user:add:*],perms参数可以写多个，多个时必须加上引号，
     * 并且参数之间用逗号分割，例如/admins/user/**=perms["user:add:*,user:modify:*"]，
     * 当有多个参数时必须每个参数都通过才通过，想当于isPermitedAll()方法。
     * port(PortFilter.class), port 请求的端口号
     * rest(HttpMethodPermissionFilter.class),
     * roles(RolesAuthorizationFilter.class), 角色
     * roles：例子/admins/user/**=roles[admin],参数可以写多个，多个时必须加上引号，
     * 并且参数之间用逗号分割，当有多个参数时，例如/admins/user/**=roles["admin,guest"],
     * 每个参数通过才算通过，相当于hasAllRoles()方法。
     * ssl(SslFilter.class),
     * user(UserFilter.class); 需要用户登录才能 user：例如/admins/user/**=user没有参数
     * 表示必须存在用户，当登入操作时不做检查
     * @Param: [securityManager]
     * @return: org.apache.shiro.spring.web.ShiroFilterFactoryBean
     * @Author: Join
     * @Date: 2018/12/11
     */

 /**
     * @Description: 密码匹配器
     * @Param: []
     * @return: com.jiaolin.shiro.config.CredentialMatcher
     * @Author: Join
     * @Date: 2018/12/11
     */
//    @Bean("credentialMatcher")
//    public CredentialMatcher credentialMatcher() {
//        return new CredentialMatcher();
//    }



    /**
     * 密码校验规则HashedCredentialsMatcher
     * 这个类是为了对密码进行编码的 ,
     * 防止密码在数据库里明码保存 , 当然在登陆认证的时候 ,
     * 这个类也负责对form里输入的密码进行编码
     * 处理认证匹配处理器：如果自定义需要实现继承HashedCredentialsMatcher
     */
    /**
    * @Description: 下面为添加的
    * @Param: []
    * @return: HashedCredentialsMatcher
    * @Author: Join
    * @Date: 2018/12/13
    */
    @Bean("hashedCredentialsMatcher")
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        //指定加密方式为MD5
        credentialsMatcher.setHashAlgorithmName("MD5");
        //加密次数
        credentialsMatcher.setHashIterations(1024);
        credentialsMatcher.setStoredCredentialsHexEncoded(true);
        return credentialsMatcher;
    }

    @Bean("authRealm")
    @DependsOn("lifecycleBeanPostProcessor")//可选
    public AuthRealm authRealm(@Qualifier("hashedCredentialsMatcher") HashedCredentialsMatcher matcher) {
        AuthRealm authRealm = new AuthRealm();
        authRealm.setAuthorizationCachingEnabled(false);
        authRealm.setCredentialsMatcher(matcher);
        return authRealm;
    }


    /**
     * 定义安全管理器securityManager,注入自定义的realm
     * @param authRealm
     * @return
     */
    @Bean("securityManager")
    public SecurityManager securityManager(@Qualifier("authRealm") AuthRealm authRealm) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(authRealm);
        return manager;
    }


    /**
     * 定义shiroFilter过滤器并注入securityManager
     * @param securityManager
     * @return
     */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(@Qualifier("securityManager") SecurityManager securityManager) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager);
        bean.setLoginUrl("/login");
        bean.setSuccessUrl("/index");
        bean.setUnauthorizedUrl("/unauthorized");
        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/index", "authc");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/loginUser", "anon");
        //角色为admin的用户才能访问admin网页
        filterChainDefinitionMap.put("/admin", "roles[admin]");
        //权限为edit的角色才能访问edit页面
        filterChainDefinitionMap.put("/edit", "perms[edit]");
        filterChainDefinitionMap.put("/druid/**", "anon");
        //另外的页面需要用户进行登录才能访问
        filterChainDefinitionMap.put("/**", "user");
        bean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return bean;
    }

    /**
     * Spring的一个bean , 由Advisor决定对哪些类的方法进行AOP代理 .
     * @return
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

    /**
     * 配置shiro跟spring的关联
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("securityManager") SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    /**
     * lifecycleBeanPostProcessor是负责生命周期的 , 初始化和销毁的类
     * (可选)
     */
    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    public static void main(String[] args) {
        String hashAlgorithName = "MD5";
        String password = "123";
        int hashIterations = 1024;//加密次数
        ByteSource credentialsSalt = ByteSource.Util.bytes("admin");
        Object obj = new SimpleHash(hashAlgorithName, password, credentialsSalt, hashIterations);
        System.out.println(obj);
    }
}