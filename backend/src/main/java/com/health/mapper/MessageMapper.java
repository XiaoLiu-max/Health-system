package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    // 撤回专用：查询当前用户最新一条可撤回的私聊消息
    @Select("SELECT * FROM message WHERE from_uid = #{currentUserId} AND type = 2 AND is_recall = 0 ORDER BY create_time DESC LIMIT 1")
    Message selectLatestSelfChatMsg(@Param("currentUserId") Long currentUserId);

    @Select("SELECT COUNT(*) FROM message WHERE to_uid = #{toUid} AND is_read = 0")
    int countUnread(@Param("toUid") Long toUid);

}