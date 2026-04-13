package org.example.springaistart.controller;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@RestController
@RequestMapping("/ai")
public class ChatController {

    @Autowired
    private DeepSeekChatModel chatModel;

    @GetMapping("/generate")
    public  String generate(@RequestParam(value = "message", defaultValue = "你是谁（简单回答）") String message){

        System.out.println("message = "+message);

        //一次性问答
        String resp = chatModel.call(message);

        System.out.println("resp = "+resp);
        return resp;
    }
    @GetMapping("/generateStream0")
    public  Flux<ChatResponse> generateStream0(@RequestParam(value = "message", defaultValue = "你是谁（简单回答）") String message){

        System.out.println("message = "+message);

        //流式输出JSON格式
        Prompt prompt = new Prompt(new UserMessage(message));
        Flux<ChatResponse> stream = chatModel.stream(prompt);

        System.out.println("返回内容 = "+stream);
        return stream;
    }
    @GetMapping("/generateStream")
    public  Flux<String> generateStream(
            @RequestParam(value = "message", defaultValue = "你是谁（简单回答）") String message,
            HttpServletResponse response
    ){

        response.setCharacterEncoding("UTF-8");
        System.out.println("message = "+message);

        //流式返回内容
        Prompt prompt = new Prompt(new UserMessage(message));
        Flux<ChatResponse> stream = chatModel.stream(prompt);

        Flux<String> resp = stream.map(
                chatResponse -> chatResponse.getResult().getOutput().getText()
        ).doOnNext(text -> System.out.print(" " + text));

//        Flux<String> resp=stream.map(new Function<ChatResponse, String>() {
//            @Override
//            public String apply(ChatResponse chatResponse) {
//                return chatResponse.getResult().getOutput().getText();
//            }
//        });

        System.out.println("返回内容 = "+resp);
        return resp;
    }
}