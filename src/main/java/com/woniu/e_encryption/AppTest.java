package com.woniu.e_encryption;

import static org.junit.Assert.*;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.junit.Test;

public class AppTest {
	@Test
	public void test1() throws Exception {
		/*
		 什么是加密：
		 明文 --> 密钥 --> 密文
			123 --> abc -> xyz
		解密：
			xyz --> abc --> 123
		 加密方式，总共分为3大类：
		 1. 对称加密
		 		加密使用的密钥，和解密使用的密钥是同一个密钥
		 2. 非对称加密
		 		加密使用的密钥，和解密使用的密钥不是同一个密钥
		 3. 不可逆加密
		 		只能使用加密明文，得出的密文无法解密！！
		*/
		// 我们要学习的是MD5加密， 也就是不可逆加密。
		// MD5加密的原理，就是使用hash哈希算法 
		String str = "123";
		String salt = "abc";
		SimpleHash sh = new SimpleHash("md5",str,salt,1024);
		String hex = sh.toHex();
		System.out.println(hex);
		
	}
}
