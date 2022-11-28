package xw.demo.shiro.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xw.demo.shiro.entity.UserInfo;

public interface UserInfoDao extends JpaRepository<UserInfo, Long> {

    // 自定义方法，解析方法名自动实现
    public UserInfo findByUsername(String username);
}
