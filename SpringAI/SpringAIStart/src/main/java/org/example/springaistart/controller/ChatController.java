package org.example.springaistart.controller;


import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class ChatController {

    @Autowired
    private DeepSeekChatModel chatModel;

    @GetMapping("/generate")
    public  String generate(@RequestParam(value = "message", defaultValue = "给我讲个笑话") String message){

        System.out.println("message = "+message);

        String resp = chatModel.call(message);

        System.out.println("resp = "+resp);
        return resp;
    }
}