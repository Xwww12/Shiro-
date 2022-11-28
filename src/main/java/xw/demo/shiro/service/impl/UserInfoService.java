package xw.demo.shiro.service.impl;

import org.springframework.stereotype.Service;
import xw.demo.shiro.dao.UserInfoDao;
import xw.demo.shiro.entity.UserInfo;

import javax.annotation.Resource;

@Service
public class UserInfoService implements xw.demo.shiro.service.UserInfoService {

    // 默认安照名称进行装配
    @Resource
    private UserInfoDao userInfoDao;

    @Override
    public UserInfo findByUsername(String username) {
        return userInfoDao.findByUsername(username);
    }
}
