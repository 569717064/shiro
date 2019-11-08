package com.woniu.b_realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

//自定义Realm，自定以的Realm必须继承AuthorizingRealm
public class MyRealm extends AuthorizingRealm {
	// 认证， 该方法的执行时机是，subject.login()方法调用时
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		System.out.println("MyRealm.doGetAuthenticationInfo() 认证");
		// 获取信令中封装的账户
		Object principal = token.getPrincipal();
		// 获取信令中封装的密码
		String credentials = new String((char[])token.getCredentials());
		System.out.println("账号："+principal+" 密码："+credentials);
		// 拿着用户传来的账户，进行比对！ 应该与数据库中的数据比较，这里就应该连库，但是为了简单，这里把账户先写死！
		if (!"foo".equals(principal)) {
			// 如果doGetAuthenticationInfo方法返回值为null，则表示账户不正确！
			return null;
		}
		// 以下的123密码，不是用户传进来的， 而可以理解为从数据库中读取出来的！
		// 这里只需要把数据库的密码返回给上层方法即可，上层方法就会自动进行密码的比对！
		return new SimpleAuthenticationInfo(principal, "123", "MyRealm");
	}
	
	// 授权
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		System.out.println("MyRealm.doGetAuthorizationInfo() 授权");
		// 获取主体多个身份中的其中一个： 主要身份，所谓的主要身份，
		//就是当主体确实有多个身份时，排在第一个的身份就是“主要身份”。
		Object primaryPrincipal = principals.getPrimaryPrincipal();
		
		SimpleAuthorizationInfo sa = new SimpleAuthorizationInfo();
		if ("foo".equals(primaryPrincipal)) {
			sa.addRole("admin");
			sa.addStringPermission("user:save");
		}
		return sa;
	}


}
