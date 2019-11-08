package com.woniu.c_realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

//自定义Realm，自定以的Realm必须继承AuthorizingRealm
public class MyRealm2 extends AuthorizingRealm {
	// 认证， 该方法的执行时机是，subject.login()方法调用时
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		System.out.println("MyRealm.doGetAuthenticationInfo() 认证2");
		Object principal = token.getPrincipal();
		String credentials = new String((char[])token.getCredentials());
		if (!"foo".equals(principal)) {
			return null;
		}
		return new SimpleAuthenticationInfo(principal,"123","MyRealm2");
	}
	// 授权
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		System.out.println("MyRealm.doGetAuthorizationInfo() 授权2");
		// 获取主体多个身份中的其中一个： 主要身份，所谓的主要身份，
		// 就是当主体确实有多个身份时，排在第一个的身份就是“主要身份”。
		Object primaryPrincipal = principals.getPrimaryPrincipal();
		SimpleAuthorizationInfo sa = new SimpleAuthorizationInfo();
		if ("bar".equals(primaryPrincipal)) {
			sa.addRole("guest");
			sa.addStringPermission("user:find");
		}
		return sa;
	}


}
