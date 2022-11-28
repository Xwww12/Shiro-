package xw.demo.shiro.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import xw.demo.shiro.entity.SysPermission;
import xw.demo.shiro.entity.SysRole;
import xw.demo.shiro.entity.UserInfo;
import xw.demo.shiro.service.UserInfoService;

import javax.annotation.Resource;

// 自定义Realm
public class MyShiroRealm extends AuthorizingRealm {
    @Resource
    private UserInfoService userInfoService;


    /**
     * 获取权限
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 1.获取用户信息
        UserInfo userInfo = (UserInfo) principalCollection.getPrimaryPrincipal();

        // 2.从用户信息中获取用户角色
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        for (SysRole role : userInfo.getRoles()) {
            simpleAuthorizationInfo.addRole(role.getName());
            //3. 从用户的角色中获取权限
            for (SysPermission permission : role.getPermissions()) {
                simpleAuthorizationInfo.addStringPermission(permission.getName());
            }
        }

        return simpleAuthorizationInfo;
    }

    /**
     * 获取身份验证信息
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 1.获取用户名
        String username = (String) authenticationToken.getPrincipal();
        System.out.println(authenticationToken.getPrincipal());

        // 2.查询用户信息
        UserInfo userInfo = userInfoService.findByUsername(username);
        if (userInfo == null) {
            return null;
        }

        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(
                userInfo,   // 用户信息
                userInfo.getPassword(), // 密码
                ByteSource.Util.bytes(userInfo.getSalt()),  // 盐值
                getName()   // realm名
        );

        return simpleAuthenticationInfo;
    }
}
