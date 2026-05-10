package com.health.controller;

import com.health.service.RemindService;
import com.health.entity.Remind;
import com.health.utils.UserContext;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/remind")
public class RemindController {

    @Resource
    private RemindService remindService;

    // 新增提醒
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody Remind remind) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long currentUserId = UserContext.getUserId();
            if (currentUserId == null) {
                result.put("code", 401);
                result.put("msg", "未登录，请先登录");
                return result;
            }
            remind.setUserId(currentUserId);
            boolean success = remindService.addRemind(remind);

            if (success) {
                result.put("code", 200);
                result.put("msg", "提醒添加成功!");
                result.put("data", remind);
            } else {
                result.put("code", 500);
                result.put("msg", "添加失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    // 查询全部
    @GetMapping("/all")
    public Map<String, Object> findAll() {
        Map<String, Object> result = new HashMap<>();
        List<Remind> list = remindService.findAll();
        result.put("code", 200);
        result.put("msg", "成功");
        result.put("data", list);
        return result;
    }

    // 根据ID查询详情
    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        Remind remind = remindService.getById(id);
        result.put("code", 200);
        result.put("msg", "成功");
        result.put("data", remind);
        return result;
    }

    // 修改提醒
    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody Remind remind) {
        Map<String, Object> result = new HashMap<>();
        try {
            remind.setId(id);
            boolean success = remindService.updateRemind(remind);
            if (success) {
                result.put("code", 200);
                result.put("msg", "修改成功!");
            } else {
                result.put("code", 500);
                result.put("msg", "修改失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    // 删除提醒
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Remind remind = remindService.getById(id);
            if (remind == null) {
                result.put("code", 404);
                result.put("msg", "提醒不存在");
                return result;
            }
            boolean success = remindService.deleteById(id);
            if (success) {
                result.put("code", 200);
                result.put("msg", "删除成功!");
            } else {
                result.put("code", 500);
                result.put("msg", "删除失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "删除异常: " + e.getMessage());
        }
        return result;
    }

    // 查询我的提醒
    @GetMapping("/my")
    public Map<String, Object> getMyReminds(@RequestParam(required = false) Integer status) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long currentUserId = UserContext.getUserId();
            if (currentUserId == null) {
                result.put("code", 401);
                result.put("msg", "未登录");
                return result;
            }

            List<Remind> list = remindService.getMyReminds(currentUserId, status);
            result.put("code", 200);
            result.put("msg", "成功");
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    // 关闭提醒
    @PutMapping("/{id}/close")
    public Map<String, Object> closeRemind(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Remind remind = remindService.getById(id);
            if (remind == null) {
                result.put("code", 404);
                result.put("msg", "提醒不存在");
                return result;
            }
            if (remind.getStatus() != 0) {
                result.put("code", 400);
                result.put("msg", "只有待提醒状态才能关闭");
                return result;
            }
            boolean success = remindService.closeRemind(id);
            if (success) {
                result.put("code", 200);
                result.put("msg", "提醒已关闭");
            } else {
                result.put("code", 500);
                result.put("msg", "关闭失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "关闭异常: " + e.getMessage());
        }
        return result;
    }

    // 系统触发提醒（支持重复）
    @GetMapping("/trigger")
    public Map<String, Object> getTriggerRemind() {
        Map<String, Object> result = new HashMap<>();
        List<Remind> list = remindService.getNeedTriggerRemind();
        result.put("code", 200);
        result.put("msg", "成功");
        result.put("data", list);
        return result;
    }

    // 重新打开提醒
    @PutMapping("/{id}/open")
    public Map<String, Object> openRemind(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Remind remind = remindService.getById(id);
            if (remind == null) {
                result.put("code", 404);
                result.put("msg", "提醒不存在");
                return result;
            }
            if (remind.getStatus() != 2) {
                result.put("code", 400);
                result.put("msg", "只有已关闭状态才能重新打开");
                return result;
            }
            boolean success = remindService.openRemind(id);
            if (success) {
                result.put("code", 200);
                result.put("msg", "提醒已重新打开");
            } else {
                result.put("code", 500);
                result.put("msg", "打开失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "打开异常: " + e.getMessage());
        }
        return result;
    }
}