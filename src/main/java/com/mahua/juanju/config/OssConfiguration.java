package com.mahua.juanju.config;


import com.mahua.juanju.utils.AliOSSUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration//声明是配置文件
@Slf4j//日志
//@ConfigurationProperties(prefix = "juanju.alioss")
@Data
public class OssConfiguration {
	private String endpoint;
	private String accessKeyId;
	private String accessKeySecret;
	private String buckNetName;
	@Bean//创建一个Bean
	@ConditionalOnMissingBean//容器里面要是有Bean，那么就不需要创建一个新的bean
	public AliOSSUtil aliOssUtil(){
		log.info("开始创建阿里云文件上传工具{}");
		return new AliOSSUtil(endpoint,accessKeyId,accessKeySecret,buckNetName);
	}
}