package test;

import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.junit.Test;

public class AuthenticationTest4 {

    @Test
    public void testAuthentication() {
        String password = "123456";

        // 盐值（随机数）
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        // 加密次数
        int times = 2;
        // 加密方式
        String alogrithmName = "md5";

        SimpleHash encodePassword = new SimpleHash(alogrithmName, password, salt, times);
        System.out.println("原密码：" + password);
        System.out.println("加密后：" + encodePassword);
    }
}
