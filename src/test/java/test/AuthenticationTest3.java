package test;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.junit.Test;
import test.realm.MyRealm;

public class AuthenticationTest3 {

    MyRealm myRealm = new MyRealm();

    @Test
    public void testAuthentication() {
        // 1.构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(myRealm);

        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();   // 获取当前主体

        // 2.主体提交认证请求
        UsernamePasswordToken token = new UsernamePasswordToken("user", "123456");
        subject.login(token);   // 登录

        subject.checkRoles("admin", "user");

        subject.checkPermission("user:add");
    }
}
