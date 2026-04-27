package com.health.controller;
import com.health.service.RemindService;
import com.health.entity.Remind;
import com.health.utils.UserContext;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/remind")
public class RemindController {

    @Resource
    private RemindService remindService;

    // ===================== 原有基础接口 =====================
    // 1.新增（必须传 userId）
    @PostMapping("/add")
    public String add(@RequestBody Remind remind) {
        // 从上下文自动获取当前登录用户ID
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            return "未登录，请先登录";
        }
        remind.setUserId(currentUserId);
        boolean result = remindService.addRemind(remind);
        return result ? "提醒添加成功!" : "添加失败";
    }

    // 2.查询全部
    @GetMapping("/all")
    public List<Remind> findAll() {
        return remindService.findAll();
    }

    // 3.根据id查询详情
    @GetMapping("/{id}")
    public Remind getById(@PathVariable Long id) {
        System.out.println("==============================");
        System.out.println("后端收到查询请求，ID = " + id);
        Remind remind = remindService.getById(id);
        System.out.println("查询到的数据 = " + remind);
        System.out.println("==============================");
        return remind;
    }

    // 4.修改
    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @RequestBody Remind remind) {
        remind.setId(id);
        boolean result = remindService.updateRemind(remind);
        return result ? "修改成功!" : "修改失败";
    }

    // 5.删除
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        boolean result = remindService.deleteById(id);
        return result ? "删除成功!" : "删除失败";
    }

    // ===================== 业务扩展接口 =====================
    // 查询我的提醒 → 【改动：必须传 userId】
    @GetMapping("/my")
    public List<Remind> getMyReminds(@RequestParam(required = false) Integer status) {
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            throw new RuntimeException("未登录");
        }
        return remindService.getMyReminds(currentUserId, status);
    }

    // 关闭提醒
    @PutMapping("/{id}/close")
    public String closeRemind(@PathVariable Long id) {
        boolean result = remindService.closeRemind(id);
        return result ? "提醒已关闭" : "数据不存在";
    }

    // 系统触发接口
    @GetMapping("/trigger")
    public List<Remind> getTriggerRemind() {
        return remindService.getNeedTriggerRemind();
    }

}