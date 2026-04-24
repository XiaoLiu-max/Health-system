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

    // ===================== 核心业务：查询我的提醒（筛选+排序） =====================
    public List<Remind> getMyReminds(Integer status) {
        LambdaQueryWrapper<Remind> wrapper = new LambdaQueryWrapper<>();
        // 按状态筛选
        if (status != null) {
            wrapper.eq(Remind::getStatus, status);
        }
        // 按提醒时间升序（最早的排前面）
        wrapper.orderByAsc(Remind::getRemindTime);
        return remindMapper.selectList(wrapper);
    }

    // ===================== 手动关闭提醒（改为 2） =====================
    public boolean closeRemind(Long id) {
        Remind remind = remindMapper.selectById(id);
        if (remind == null) {
            return false;
        }
        remind.setStatus(2);
        return remindMapper.updateById(remind) > 0;
    }

    // ===================== 自动触发提醒（核心功能） =====================
    public List<Remind> getNeedTriggerRemind() {
        // 1. 查询条件：待提醒(0) + 时间已到
        LambdaQueryWrapper<Remind> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Remind::getStatus, 0);
        wrapper.le(Remind::getRemindTime, LocalDateTime.now());

        List<Remind> remindList = remindMapper.selectList(wrapper);

        // 2. 遍历提醒：打印消息 + 改为已提醒(1)
        for (Remind remind : remindList) {
            System.out.println("======================================");
            System.out.println("🔔 自动提醒触发！");
            System.out.println("内容：" + remind.getContent());
            System.out.println("时间：" + remind.getRemindTime());
            System.out.println("======================================");

            // 标记为已提醒，防止重复提醒
            remind.setStatus(1);
            remindMapper.updateById(remind);
        }

        return remindList;
    }
}