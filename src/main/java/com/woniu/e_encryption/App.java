package com.woniu.e_encryption;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.realm.jdbc.JdbcRealm.SaltStyle;
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
		realm.setAuthenticationQuery("select password , salt from users where username = ?");
		// 覆盖根据身份获取用户角色的sql,该sql执行时机是: subject.hasRole("admin");
		realm.setUserRolesQuery("SELECT rname FROM users_roles ur JOIN users u ON ur.uid = u.uid JOIN roles r ON ur.rid = r.rid WHERE username = ?");
		// 覆盖根据角色获取用户权限的sql
		realm.setPermissionsQuery("SELECT pname FROM roles_permissions rp JOIN roles r ON rp.rid = r.rid JOIN permissions p ON rp.pid = p.pid WHERE rname = ?");
		// 注意，shiro默认情况下，只能根据身份来获取角色，
		// 当要判断用户是否拥有某个全显示，shiro不能那么“智能地”先根据身份获取角色，在根据角色获取权限。
		// 为了让shiro能够根据身份 间接地获取到 权限，需要添加以下代码：
		realm.setPermissionsLookupEnabled(true);
		

		// 把加密需要的 算法、盐、迭代次数，告诉shiro，确切地说，就是告诉realm
		// 该对象只能设置2个信息：算法 迭代次数
		HashedCredentialsMatcher hcm = new HashedCredentialsMatcher();
		//设置加密的算法
		hcm.setHashAlgorithmName("md5");
		//设置加密迭代的次数
		hcm.setHashIterations(1024);
		realm.setCredentialsMatcher(hcm);
		
		// 还少一个盐，怎么办？ 
		// 1. 给users表中，添加一个列，专门存放盐，也就说，盐是存放在数据库表中的！而不是这里配置的！
		/*
		 ALTER TABLE users
		 ADD COLUMN salt VARCHAR(20);
		 */
		// 2. 修改认证的sql语句
		// select password, salt from users where username = ?
		// 3. 告诉shiro，在认证sql语句中，查询出的结果中，包含盐。
		//		否则就算我们的认证sql语句确实把盐查出来了，shiro也不知道！
		realm.setSaltStyle(SaltStyle.COLUMN);
		
		securityManager.setRealm(realm);
		SecurityUtils.setSecurityManager(securityManager);
		Subject subject = SecurityUtils.getSubject();
		
		// 创建一个令牌
		UsernamePasswordToken token = new UsernamePasswordToken("foo","123");
		try {
			subject.login(token);//认证
			System.out.println("认证成功");
		} catch (Exception e) {
			System.out.println("认证失败:"+e);
		}
		System.out.println("当前主体的身份: "+subject.getPrincipal());
		//授权
		subject.logout();
		
	}
	
}
