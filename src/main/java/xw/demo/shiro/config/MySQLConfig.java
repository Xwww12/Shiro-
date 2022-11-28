package xw.demo.shiro.config;

import org.hibernate.dialect.MySQL5Dialect;

public class MySQLConfig extends MySQL5Dialect {
    @Override
    public String getTableTypeString() {
        // 让 Hibernate 默认创建 InnoDB 引擎并默认使用 utf-8 编码
        return "ENGINE=InnoDB DEFAULT CHARSET=utf8";
    }
}
