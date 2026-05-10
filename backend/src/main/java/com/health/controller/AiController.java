//package com.health.controller;
//
//import com.health.utils.XfyunAiUtil;
//import com.health.vo.AiChatVO;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//
//@RestController
//@RequestMapping("/ai")
//public class AiController {
//
//    @Resource
//    private XfyunAiUtil xfyunAiUtil;
//
//    @PostMapping("/chat")
//    public AiChatVO chat(@RequestBody String question) {
//        return xfyunAiUtil.chat(question);
//    }
//}

package com.health.controller;

import com.health.utils.XfyunAiUtil;
import com.health.vo.AiChatVO;
import com.health.vo.AiQuestionVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private XfyunAiUtil xfyunAiUtil;

    // ✅ 改成用 AiQuestionVO 接收前端的 JSON 请求
    @PostMapping("/chat")
    public AiChatVO chat(@RequestBody AiQuestionVO vo) {
        return xfyunAiUtil.chat(vo.getQuestion());
    }
}