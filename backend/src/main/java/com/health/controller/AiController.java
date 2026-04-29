package com.health.controller;

import com.health.utils.XfyunAiUtil;
import com.health.vo.AiChatVO;
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

    @PostMapping("/chat")
    public AiChatVO chat(@RequestBody String question) {
        return xfyunAiUtil.chat(question);
    }
}