package com.health.service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.health.entity.Remind;
import com.health.mapper.RemindMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RemindService {

    @Resource
    private RemindMapper remindMapper;

    @Resource
    private MessageServices messageServices;

    // 新增提醒（默认待提醒 status=0）
    public boolean addRemind(Remind remind) {
        remind.setStatus(0);
        return remindMapper.insert(remind) > 0;
    }

    // 查询全部（测试用）
    public List<Remind> findAll() {
        return remindMapper.selectList(null);
    }

    // 根据ID查询（测试用）
    public Remind getById(Long id) {
        return remindMapper.selectById(id);
    }

    // 修改提醒
    public boolean updateRemind(Remind remind) {
        return remindMapper.updateById(remind) > 0;
    }

    // 删除提醒（测试用）
    public boolean deleteById(Long id) {
        return remindMapper.deleteById(id) > 0;
    }

    // ===================== 【改动 1】查询我的提醒（必须传 userId） =====================
    public List<Remind> getMyReminds(Long userId, Integer status) {
        LambdaQueryWrapper<Remind> wrapper = new LambdaQueryWrapper<>();

        // 只查当前登录用户的提醒 → 最重要！
        wrapper.eq(Remind::getUserId, userId);

        // 按状态筛选
        if (status != null) {
            wrapper.eq(Remind::getStatus, status);
        }
        // 按提醒时间升序
        wrapper.orderByAsc(Remind::getRemindTime);
        return remindMapper.selectList(wrapper);
    }

    // ===================== 手动关闭提醒 =====================
    public boolean closeRemind(Long id) {
        Remind remind = remindMapper.selectById(id);
        if (remind == null) {
            return false;
        }
        remind.setStatus(2);
        return remindMapper.updateById(remind) > 0;
    }

    // ===================== 【改动 2】自动触发提醒（打印用户ID） =====================
    public List<Remind> getNeedTriggerRemind() {
        LambdaQueryWrapper<Remind> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Remind::getStatus, 0);
        wrapper.le(Remind::getRemindTime, LocalDateTime.now());

        List<Remind> remindList = remindMapper.selectList(wrapper);

        for (Remind remind : remindList) {
            System.out.println("======================================");
            System.out.println("🔔 自动提醒触发！");
            System.out.println("用户ID：" + remind.getUserId());  // 这里加了用户ID
            System.out.println("内容：" + remind.getContent());
            System.out.println("时间：" + remind.getRemindTime());
            System.out.println("======================================");

            messageServices.sendRemindMessage(
                    remind.getUserId(),
                    remind.getContent()
            );

            remind.setStatus(1);
            remindMapper.updateById(remind);
        }

        return remindList;
    }
}