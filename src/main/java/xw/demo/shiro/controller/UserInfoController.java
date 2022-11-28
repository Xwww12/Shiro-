package xw.demo.shiro.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import xw.demo.shiro.entity.UserInfo;
import xw.demo.shiro.service.UserInfoService;

import javax.annotation.Resource;

@RestController
public class UserInfoController {

    @Resource
    UserInfoService userInfoService;

    /**
     * 按username从数据库中取出用户信息
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/userList")
    // shiro权限管理，有这个权限才能调动这个Api
    @RequiresPermissions("userInfo:view")
    public UserInfo findUserInfoByUsername(@RequestParam String username) {
        return userInfoService.findByUsername(username);
    }

    /**
     * 添加用户成功信息
     * @return
     */
    @PostMapping("/userAdd")
    @RequiresPermissions("userInfo:add")
    public String addUserInfo() {
        return "addUserInfo success";
    }

    @DeleteMapping("userDelete")
    @RequiresPermissions("userInfo:delete")
    public String deleteUserInfo() {
        return "deleteUserInfo success";
    }
}
