package xw.demo.shiro.service;

import xw.demo.shiro.entity.UserInfo;

public interface UserInfoService {
    public UserInfo findByUsername(String username);
}
