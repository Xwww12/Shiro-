# Shiro

 

原文[Shiro安全框架【快速入门】就这一篇！ - 知乎 (zhihu.com)](https://zhuanlan.zhihu.com/p/54176956)的笔记

### 做什么的

> 开源安全框架，处理身份验证、授权、加密和会话管理
>
> * 认证：识别用户身份（登录）
> * 授权：给用户某些操作的权限（赋予角色）
> * 加密：对数据源使用加密算法
> * 会话管理：特定于用户的会话管理

 

 

### 整体架构

> 分为Subject,SecurityManager和 Realm三层

![](/img/quick_img/Shior架构.png)

 

 

### 使用

#### Shiro认证过程

> 验证用户身份

![](/img/quick_img/Shior认证过程.png)

测试类

```java
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
```

 

 

#### Shiro授权过程

> 赋予/验证用户角色

```java
public class AuthenticationTest2 {

    SimpleAccountRealm simpleAccountRealm =  new SimpleAccountRealm();

    @Before
    public void addUser() {
        // 往Realm域中添加用户信息
        // 使其具备admin和user两个角色
        simpleAccountRealm.addAccount("user", "123456", "admin", "user");
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

        // 验证用户是否有相应的角色，没有则报错
        subject.checkRoles("admin", "user");

        // subject.checkRoles("admin", "user", "xxx"); // UnauthorizedException

    }
}
```

 

 



#### 自定义Realm

> realm类似MVC的repository层

自定义Realm

```java
public class MyRealm extends AuthorizingRealm {

    /**
     * 模拟数据库数据
     */
    Map<String, String> userMap = new HashMap<>(16);

    {
        userMap.put("user", "123456");
        super.setName("myRealm"); // 设置自定义Realm的名称
    }

    /**
     * 授权
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String userName = (String) principalCollection.getPrimaryPrincipal();

        // 从数据库中获取角色和权限数据
        Set<String> roles = getRolesByUserName(userName);
        Set<String> permissions = getPermissionsByUserName(userName);

        // 添加到授权信息中并返回
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setStringPermissions(permissions);
        simpleAuthorizationInfo.setRoles(roles);

        return simpleAuthorizationInfo;
    }

    /**
     * 认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String username = (String) authenticationToken.getPrincipal();

        String password = getPasswordByUserName(username);
        if (password == null) {
            return null;
        }

        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo("user", password, "myRealm");

        return simpleAuthenticationInfo;
    }


    /**
     * 根据用户名获取权限信息
     * @return
     */
    private Set<String> getPermissionsByUserName(String username) {
        // 模拟查询数据库并返回
        HashSet<String> permission = new HashSet<>();
        permission.add("user:delete");
        permission.add("user:add");
        return permission;
    }

    /**
     * 获取角色数据
     *
     * @param userName
     * @return
     */
    private Set<String> getRolesByUserName(String userName) {
        // 模拟查询数据库并返回
        Set<String> roles = new HashSet<>();
        roles.add("admin");
        roles.add("user");
        return roles;
    }

    /**
     * 获取用户密码
     * @param userName
     * @return
     */
    private String getPasswordByUserName(String userName) {
        // 模拟获取用户密码/凭证
        return userMap.get(userName);
    }
}

```

测试

```java
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

```

 

 

#### Shiro加密

> 使用MD5加密可以使数据库中的密码不是明文保存，数据库泄露的损失会减小
>
> 但可以通过用一些简单常用的密码来撞库，从而反推原密码

解决方式：

**加盐：**在原始密码上加上随机数，再进行MD5加密。需要把随机数也存到数据库中，以便之后进行验证

**多次加密**：多次加密MD5，从而让攻击者不知道加密的次数

 

Shiro实现

```java
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
```

