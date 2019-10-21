package com.kralofsky.citybikes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.telegram.telegrambots.ApiContextInitializer;

import java.util.Locale;

@Controller
@SpringBootApplication
public class CitybikeTelegramBotApp {
    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        //ApiContextInitializer.init();
        SpringApplication.run(CitybikeTelegramBotApp.class, args);
    }
}
