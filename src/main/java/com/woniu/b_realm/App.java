package com.woniu.b_realm;

import static org.junit.Assert.*;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

public class App {
	
	@Test
	public void test1() throws Exception {
		//创建shiro的安全管理器
		DefaultSecurityManager securityManager = new DefaultSecurityManager();
		MyRealm myRealm = new MyRealm();
		// 把自定义的realm对象，设置给安全管理器！
		securityManager.setRealm(myRealm);
		// 把安全管理器设置为全局
		SecurityUtils.setSecurityManager(securityManager);
		// 获取当前系统登录的主体
		Subject subject = SecurityUtils.getSubject();
		// 创建一个令牌
		UsernamePasswordToken token = new UsernamePasswordToken("foo","123");
		
		try {
			//认证
			subject.login(token);
			System.out.println("认证成功");
		} catch (Exception e) {
			System.out.println("认证失败"+e);
		}
		
		//认证通过才有身份
		System.out.println("当前主体的身份："+subject.getPrincipal());
		// 授权， 注意，授权时，会把认证成功后所得到的身份再传入给 授权方法：
		System.out.println("admin角色：" + subject.hasRole("admin"));
		System.out.println("guest角色：" + subject.hasRole("guest"));
		System.out.println("user:save " + subject.isPermitted("user:save"));
			
		subject.logout();
	}
}
