package com.mitocode.springreactore.config;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(){
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name","dbejx1mbw",
                        "api_key","938542424263458",
                        "api_secret","aD6SdddlxjFQbEhFjzy-cMIfzLs"
        ));
    }
}
