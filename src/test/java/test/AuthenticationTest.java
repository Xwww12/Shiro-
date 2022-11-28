package test;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;

public class AuthenticationTest {

    SimpleAccountRealm simpleAccountRealm =  new SimpleAccountRealm();

    @Before
    public void addUser() {
        // 往Realm域中添加用户信息
        simpleAccountRealm.addAccount("user", "123456");
    }

    @Test
    public void testAuthentication() {
        // 1.构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(simpleAccountRealm);

        SecurityUtils.setSecurityManager(defaultSecurityManager);   // 设置SecurityManager环境
        Subject subject = SecurityUtils.getSubject();   // 获取当前主体

        // 2.主体提交认证请求
        UsernamePasswordToken token = new UsernamePasswordToken("user", "123456");
        subject.login(token);   // 登录

        System.out.println("isAuthenticated:" + subject.isAuthenticated());

        subject.logout();

        System.out.println("isAuthenticated:" + subject.isAuthenticated());
    }
}
