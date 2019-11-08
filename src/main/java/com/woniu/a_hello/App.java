package com.woniu.a_hello;

import static org.junit.Assert.*;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

public class App {
	
	@Test
	public void test1() throws Exception {
		//创建shiro中的安全管理器
		DefaultSecurityManager securityManager = new DefaultSecurityManager();
		// 使用IniRealm对象类加载配置文件
		IniRealm realm = new IniRealm("classpath:com/woniu/a_hello/shiro.ini");
		
		// 把加载了配置信息的realm对象，设置给安全管理器
		// 也就是说，安全管理器，已经"知道了"配置文件中的信息： foo=123 bar=456
		securityManager.setRealm(realm);
		
		// 把安全管理器，设置为全局的， 以后程序的任何地方都能畅通无阻地使用安全管理器，毕竟安全管理器被设置为全局的了！
		// 也就可以认证，在程序的任何地方都可以读取到配置文件中的信息
		// 在实际的web开发环境中，该行代码不用写，是由框架自动完成。这里是快速起步例子，所以要自己写出来！
		SecurityUtils.setSecurityManager(securityManager);
		
		// 获取要登录当前系统的主体（用户)
		// 要登录当前系统的主体，未必是人，也有可能是一个机器人程序，只要是与当前系统交换的主体就行。
		Subject subject = SecurityUtils.getSubject();
		
		// 创建一个令牌，其中封装了用户提供账户和密码
		UsernamePasswordToken token = new UsernamePasswordToken("foo","123");
		
		// 让主体拿着令牌，去进行认证！
		// 注意，
		//	认证通过，则程序不会抛出任何异常， 
		//	一旦认证失败，就会抛出异常
		//		UnknownAccountException: 表示账户错误
		// 		IncorrectCredentialsException: 密码错误
		subject.login(token);
		
		// 判断用户是否通过认证:
		System.out.println("是否通过认证："+subject.isAuthenticated());
		// 授权
		// 在用户通过认证之后，就可以判断用户是否具有某个角色
		System.out.println("admin:"+subject.hasRole("admin"));
		System.out.println("guest:"+subject.hasRole("guest"));
		
		// 在用户通过认证之后，就可以判断用户是否具有某个权限
		System.out.println("user:save "+subject.isPermitted("user:save"));
		System.out.println("user:delete "+subject.isPermitted("user:delete"));
		System.out.println("user:update "+subject.isPermitted("user:update"));
		System.out.println("user:find "+subject.isPermitted("user:find"));
		
		// 退出
		subject.logout();
		System.out.println("退出后看是否通过认证："+subject.isAuthenticated());
		
	}
}
