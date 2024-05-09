package com.mahua.juanju;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//@MapperScan("com.mahua.juanjucenter.mapper")
@SpringBootApplication
@EnableScheduling//开启定时任务
public class JuanJuApplication {

	public static void main(String[] args) {
		SpringApplication.run(JuanJuApplication.class, args);
	}

}
