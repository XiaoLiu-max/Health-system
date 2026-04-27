//package com.health.task;
//
//import com.health.service.HealthReportService;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//public class HealthReportTask {
//
//    private final HealthReportService reportService;
//
//    public HealthReportTask(HealthReportService reportService) {
//        this.reportService = reportService;
//    }
//
//    /**
//     * 每周一 0点 生成上周周报
//     */
//    @Scheduled(cron = "0 0 0 ? * MON")
//    public void weekReport() {
//        reportService.createWeekReport();
//        System.out.println("✅ 周报已生成");
//    }
//
//    /**
//     * 每月1号 0点 生成上月月报
//     */
//    @Scheduled(cron = "0 0 0 1 * ?")
//    public void monthReport() {
//        reportService.createMonthReport();
//        System.out.println("✅ 月报已生成");
//    }
//}

package com.health.task;

import com.health.entity.User;
import com.health.service.MessageService;
import com.health.service.HealthReportService;
import com.health.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class HealthReportTask {

    private final HealthReportService reportService;
    private final MessageService messageService;
    private final UserService userService;

    // 构造注入两个 service
    public HealthReportTask(HealthReportService reportService, MessageService messageService,UserService userService) {
        this.reportService = reportService;
        this.messageService = messageService;
        this.userService = userService;
    }

    // ===================== 【防重复标记】 =====================
    private LocalDate lastWeekReportDate = null;
    private LocalDate lastMonthReportDate = null;

    /**
     * 每周一 0点 生成上周周报 + 发送提醒
     */

//    @Scheduled(fixedDelay = 3000) // 测试用：每3秒执行一次
    @Scheduled(cron = "0 0 0 ? * MON")  //真正上线用这个
    public void weekReport() {

        LocalDate today = LocalDate.now();

        // 🔴 防重复：今天已经发过周报，直接跳过
        if (lastWeekReportDate != null && lastWeekReportDate.equals(today)) {
            return;
        }

        reportService.createWeekReport();
        System.out.println("✅ 周报已生成");

        // 发送每周提醒（你写的消息接口）
        List<User> allUser = userService.list();
        for(User u : allUser){
            messageService.sendHealthWarn(
                    u.getId(),
                    "每周健康报告已生成",
                    "请及时查看您的健康数据"
            );
        }
        System.out.println("✅ 每周提醒已发送");
        lastWeekReportDate = today;
    }



    /**
     * 每月1号 0点 生成上月月报 + 发送提醒
     */

//    @Scheduled(fixedDelay = 5000) // 测试用：每5秒执行一次
    @Scheduled(cron = "0 0 0 1 * ?")  //真正上线用这个
    public void monthReport() {

        LocalDate today = LocalDate.now();

        // 🔴 防重复：今天已经发过月报，直接跳过
        if (lastMonthReportDate != null && lastMonthReportDate.equals(today)) {
            return;
        }

        reportService.createMonthReport();
        System.out.println("✅ 月报已生成");

        // 发送每月提醒
        List<User> allUser = userService.list();
        for(User u : allUser){
            messageService.sendHealthWarn(
                    u.getId(),
                    "每月健康报告已生成",
                    "请及时查看您的月度健康统计"
            );
        }
        System.out.println("✅ 每月提醒已发送");
        lastMonthReportDate = today;
    }
}