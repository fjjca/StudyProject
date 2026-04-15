package org.example.springaistart.controller;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
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

    @GetMapping("/runtimeOptions")
    public String runtimeOptions(
            @RequestParam(value = "message") String message,
            @RequestParam(value = "temp", required = false) Double temp
    ) {
        System.out.println("收到消息："+message);

        Prompt prompt;
        if (temp != null) {
            // 构建带 temperature 的 DeepSeekChatOptions，覆盖默认 temp，也可以修改其他的参数（如使用的model）
            var opts = DeepSeekChatOptions.builder()
                    .temperature(temp)
                    .build();
            prompt = new Prompt(message, opts);
            System.out.println("使用运行时覆盖 temperature=" + temp);
        } else {
            // 无 temperature 传入时，使用默认配置
            prompt = new Prompt(message);
            System.out.println("使用默认 temperature");
        }

        ChatResponse resp = chatModel.call(prompt);
        String result = resp.getResult().getOutput().getText();
        System.out.println("模型返回："+ result);
        return result;
    }

}