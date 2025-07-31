package com.hanyang.ideacode;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hanyang.ideacode.mapper")
public class IdeaCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdeaCodeApplication.class, args);
    }

}
