package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * UserMapper 接口
 * 作用：专门和数据库打交道，实现用户数据的增删改查
 */
@Mapper // 这是MyBatis的注解，告诉Spring这是一个Mapper接口，会自动扫描
public interface UserMapper extends BaseMapper<User> {
    // 继承了BaseMapper<User>之后，自动拥有这些方法：
    // insert(User user)       → 插入用户
    // updateById(User user)   → 根据ID更新用户
    // deleteById(Long id)     → 根据ID删除用户
    // selectById(Long id)     → 根据ID查询用户
    // selectList()            → 查询所有用户
    // 不用自己写一行SQL！
}