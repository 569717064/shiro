package com.woniu.d_jdbcrealm;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

import com.alibaba.druid.pool.DruidDataSource;

public class App {
	
	@Test
	public void test1() throws Exception {
		//创建shiro的安全管理器
		DefaultSecurityManager securityManager = new DefaultSecurityManager();
		//定义数据源
		DruidDataSource ds = new DruidDataSource();
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUrl("jdbc:mysql://localhost:3306/shiro");
		ds.setUsername("root");
		ds.setPassword("root");
		
		JdbcRealm realm = new JdbcRealm();
		// 毕竟JdbcRealm连接的是数据库，所以必须设置数据源
		realm.setDataSource(ds);
		
		// 覆盖JdbcRealm的sql语句
		// 覆盖认证sql,该sql执行时机是: subject.login(token);
		realm.setAuthenticationQuery("select password from users where username = ?");
		// 覆盖根据身份获取用户角色的sql,该sql执行时机是: subject.hasRole("admin");
		realm.setUserRolesQuery("SELECT rname FROM users_roles ur JOIN users u ON ur.uid = u.uid JOIN roles r ON ur.rid = r.rid WHERE username = ?");
		// 覆盖根据角色获取用户权限的sql
		realm.setPermissionsQuery("SELECT pname FROM roles_permissions rp JOIN roles r ON rp.rid = r.rid JOIN permissions p ON rp.pid = p.pid WHERE rname = ?");
		// 注意，shiro默认情况下，只能根据身份来获取角色，
		// 当要判断用户是否拥有某个全显示，shiro不能那么“智能地”先根据身份获取角色，在根据角色获取权限。
		// 为了让shiro能够根据身份 间接地获取到 权限，需要添加以下代码：
		realm.setPermissionsLookupEnabled(true);
		
		securityManager.setRealm(realm);
		SecurityUtils.setSecurityManager(securityManager);
		Subject subject = SecurityUtils.getSubject();
		// 创建一个令牌
		UsernamePasswordToken token = new UsernamePasswordToken("foo","123");
		try {
			/**
			此时，当我们认证时，shiro仍然会调用Realm的doGetAuthenticationInfo（这和以前的流程是一样的）
			值得一提的是，在JdbcRealm的doGetAuthenticationInfo方法中，是通过sql语句来完成认证的！
			现在的认证对应的sql句子是：select password from users where username = ?
			可以看出该sql中有一个占位符，shiro框架会自动把token中的账号，填入？中。如果查出的结果集，一行都没有，证明账户错误！
			如果查出的结果集，有行，至少说明账户是对的，至于密码是否正确，还要再次比对。
			 */
			subject.login(token);//认证
			System.out.println("认证成功");
		} catch (Exception e) {
			System.out.println("认证失败:"+e);
		}
		System.out.println("当前主体的身份: "+subject.getPrincipal());
		//授权
		System.out.println("admin角色 "+subject.hasRole("admin"));
		System.out.println("guest角色 "+subject.hasRole("guest"));
		System.out.println("user:save "+subject.isPermitted("user:save"));
		System.out.println("user:delete "+subject.isPermitted("user:delete"));
		System.out.println("user:update "+subject.isPermitted("user:update"));
		System.out.println("user:find "+subject.isPermitted("user:find"));
		subject.logout();
	}
}
