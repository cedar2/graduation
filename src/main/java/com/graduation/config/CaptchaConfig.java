package com.graduation.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class CaptchaConfig {

    @Bean
    public DefaultKaptcha defaultKaptcha() {
        Properties props = new Properties();
        props.put("kaptcha.border", "no");
        props.put("kaptcha.textproducer.char.length", "4");
        props.put("kaptcha.textproducer.char.string", "23456789ABCDEFGHJKMNPQRSTUVWXYZ");
        props.put("kaptcha.image.width", "130");
        props.put("kaptcha.image.height", "45");
        props.put("kaptcha.textproducer.font.size", "34");
        props.put("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");

        DefaultKaptcha kaptcha = new DefaultKaptcha();
        kaptcha.setConfig(new Config(props));
        return kaptcha;
    }
}

