package com.woniu.c_realm;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

public class App {
	
	@Test
	public void test1() throws Exception {
		//创建shiro中的安全管理器
		DefaultSecurityManager securityManager = new DefaultSecurityManager();
		MyRealm realm = new MyRealm();
		MyRealm2 realm2 = new MyRealm2();
		
		// shiro可以使用多个realm，进行认证和授权
		securityManager.setRealms(Arrays.asList(realm,realm2));
		//设置全局安全管理器
		SecurityUtils.setSecurityManager(securityManager);
		//获取当前系统登录的主体
		Subject subject = SecurityUtils.getSubject();
		//创建一个令牌
		UsernamePasswordToken token = new UsernamePasswordToken("foo","123");
		
		try {
			//认证
			// shiro在多realm的环境下，默认的认证策略是，只要有一个realm通过认证，则就算作用户通过了认证！
			// 且如果第一个realm已经通过认证了，后面的realm仍然要继续认证，这样是为了收集完整的身份！
			subject.login(token);
			System.out.println("认证成功");
		} catch (Exception e) {
			System.out.println("认证失败 "+e);
		}
		
		// 有多个realm时，身份也就有多个，以下的subject.getPrincipals(); 就是获取多个身份的
		PrincipalCollection principals = subject.getPrincipals();
		for (Object p : principals) {
			System.out.println("当前主体的身份:"+p);
		}
		System.out.println("=====================================================================");
		
		// 授权
		// 授权时，仍然会调用所有Realms的doGetAuthorizationInfo方法
		// 然后把每一个Realm的doGetAuthorizationInfo方法所有返回的AuthorizationInfo汇总在一起！
		// 也就是说，最终subject所拥有的角色和权限，是所有realm授权的并集
		System.out.println("admin角色 "+subject.hasRole("admin"));
		System.out.println("guest角色 "+subject.hasRole("guest"));
		
		System.out.println("admin "+subject.isPermitted("user:save"));
		subject.logout();
		
	}
}
