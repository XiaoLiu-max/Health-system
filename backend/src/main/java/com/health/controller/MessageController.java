package com.health.controller;

import com.health.service.MessageService;
import com.health.service.MessageServices;
import com.health.utils.UserContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private MessageServices messageServices;

    @Resource
    private MessageService messageService;

    // ===================== 基础 =====================
    @GetMapping("/list")
    public Map<String, Object> getMyMessage() {
        Map<String, Object> map = new HashMap<>();
        try {
            Long userId = UserContext.getUserId();
            map.put("code", 200);
            map.put("data", messageServices.getMyMessage(userId));
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

//    @PostMapping("/read")
//    public Map<String, Object> readMessage(Long msgId) {
//        Map<String, Object> map = new HashMap<>();
//        try {
//            messageServices.readMessage(msgId);
//            map.put("code", 200);
//            map.put("msg", "已读成功");
//        } catch (Exception e) {
//            map.put("code", 500);
//            map.put("msg", e.getMessage());
//        }
//        return map;
//    }

    @PostMapping("/read")
    public Map<String, Object> readMessage(@RequestParam Long msgId) {
        Map<String, Object> map = new HashMap<>();
        try {
            messageServices.readMessage(msgId);
            map.put("code", 200);
            map.put("msg", "已读成功");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // ===================== 1. 测试：给自己发 健康异常 消息 =====================
    @PostMapping("/test/send/abnormal/self")
    public Map<String, Object> testSendSelfAbnormal(String content) {
        Map<String, Object> map = new HashMap<>();
        try {
            Long userId = UserContext.getUserId();
            messageService.sendHealthWarn(userId, content, "请关注您的健康状态");
            map.put("code", 200);
            map.put("msg", "已给自己发送【健康异常】消息");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // ===================== 2. 测试：给好友发 健康异常 消息 =====================
    @PostMapping("/test/send/abnormal/friend")
    public Map<String, Object> testSendFriendAbnormal(Long friendId, String content) {
        Map<String, Object> map = new HashMap<>();
        try {
            Long userId = UserContext.getUserId();
            messageServices.sendAbnormalToFriend(userId, friendId, content);
            map.put("code", 200);
            map.put("msg", "已给好友发送【健康异常】消息");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // ===================== 3. 测试：给自己发 健康报告 提醒 =====================
    @PostMapping("/test/send/report/self")
    public Map<String, Object> testSendSelfReport() {
        Map<String, Object> map = new HashMap<>();
        try {
            Long userId = UserContext.getUserId();
            messageService.sendHealthWarn(userId, "健康报告已生成", "请及时查看您的健康数据");
            map.put("code", 200);
            map.put("msg", "已给自己发送【健康报告】消息");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // ===================== 4. 测试：给好友发 健康报告（兼容版） =====================
    @PostMapping("/test/send/report/friend")
    public Map<String, Object> testSendReportToFriend(Long friendId, String url) {
        Map<String, Object> map = new HashMap<>();
        try {
            Long userId = UserContext.getUserId();
            messageServices.sendWeekReportToFriend(userId, friendId, url);
            map.put("code", 200);
            map.put("msg", "已给好友发送【健康报告】");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // ===================== 给好友发 月报 提醒 =====================
    @PostMapping("/test/send/monthReport/friend")
    public Map<String, Object> testSendMonthReportToFriend(Long friendId, String url) {
        Map<String, Object> map = new HashMap<>();
        try {
            Long userId = UserContext.getUserId();
            messageServices.sendMonthReportToFriend(userId, friendId, url);
            map.put("code", 200);
            map.put("msg", "已给好友发送【月度健康报告】");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // ===================== 5. 测试：好友聊天 =====================
    @PostMapping("/test/send/chat")
    public Map<String, Object> testSendChat(Long toUid, String content) {
        Map<String, Object> map = new HashMap<>();
        try {
            Long fromUid = UserContext.getUserId();
            messageServices.sendChatMessage(fromUid, toUid, content);
            map.put("code", 200);
            map.put("msg", "私聊发送成功");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // ===================== 6. 测试：自定义提醒（type=3） =====================
    @PostMapping("/test/send/remind")
    public Map<String, Object> testSendRemind(String content) {
        Map<String, Object> map = new HashMap<>();
        try {
            Long userId = UserContext.getUserId();
            messageServices.sendRemindMessage(userId, content);
            map.put("code", 200);
            map.put("msg", "已发送自定义提醒");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // ===================== 7. 测试：发送好友申请消息 =====================
    @PostMapping("/test/send/friendApply")
    public Map<String, Object> testSendFriendApply(Long toUid) {
        Map<String, Object> map = new HashMap<>();
        try {
            Long fromUid = UserContext.getUserId();
            String content = "用户" + fromUid + " 申请添加你为好友";
            messageServices.sendFriendApplyMsg(fromUid, toUid, content);
            map.put("code", 200);
            map.put("msg", "好友申请消息发送成功");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }


    @PostMapping("/recall/ultimate")
    public Map<String, Object> recallUltimate() {
        Map<String, Object> map = new HashMap<>();
        try {
            // 修复后的这一行
            Long uid = UserContext.getUserId();
            boolean ok = messageServices.recallLatestSelfChatMsg(uid);
            if (ok) {
                map.put("code", 200);
                map.put("msg", "撤回成功");
            } else {
                map.put("code", 400);
                map.put("msg", "撤回失败");
            }
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    @GetMapping("/unread/count")
    public Map<String, Object> getUnreadCount() {
        Map<String, Object> map = new HashMap<>();
        try {
            Long userId = UserContext.getUserId();
            long count = messageServices.countUnread(userId);
            map.put("code", 200);
            map.put("data", count);
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    @PostMapping("/read/user/{fromUid}")
    public Map<String, Object> readAllFromUser(@PathVariable Long fromUid) {
        Map<String, Object> map = new HashMap<>();
        try {
            Long userId = UserContext.getUserId();
            messageServices.markAllReadFromUser(userId, fromUid);
            map.put("code", 200);
            map.put("msg", "全部已读");
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    // ===================== 【单个好友未读数量】会话列表角标 =====================
//    @GetMapping("/unread/friend")
//    public Map<String, Object> getUnreadByFriend(Long friendId) {
//        Map<String, Object> map = new HashMap<>();
//        try {
//            Long userId = UserContext.getUserId();
//            long count = messageServices.countUnreadByFriend(userId, friendId);
//            map.put("code", 200);
//            map.put("data", count);
//        } catch (Exception e) {
//            map.put("code", 500);
//            map.put("msg", e.getMessage());
//        }
//        return map;
//    }

    @GetMapping("/unread/friend")
    public Map<String, Object> getUnreadByFriend(@RequestParam Long friendId) {
        Map<String, Object> map = new HashMap<>();
        try {
            Long userId = UserContext.getUserId();
            long count = messageServices.countUnreadByFriend(userId, friendId);
            map.put("code", 200);
            map.put("data", count);
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", e.getMessage());
        }
        return map;
    }
}