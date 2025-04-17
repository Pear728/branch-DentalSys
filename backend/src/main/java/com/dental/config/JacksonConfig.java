package com.dental.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;

/**
 * Jackson配置类，用于处理JSON序列化和反序列化
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        
        // 注册自定义的反序列化模块
        SimpleModule module = new SimpleModule();
        
        // 添加性别字段的自定义反序列化器
        module.addDeserializer(Integer.class, new GenderDeserializer());
        
        objectMapper.registerModule(module);
        
        return objectMapper;
    }
    
    /**
     * 性别字段反序列化器
     * 将"男"转换为0，"女"转换为1
     */
    public static class GenderDeserializer extends JsonDeserializer<Integer> {
        @Override
        public Integer deserialize(JsonParser p, DeserializationContext ctxt) 
                throws IOException, JsonProcessingException {
            String value = p.getValueAsString();
            
            // 检查字段名是否为gender，只对gender字段进行特殊处理
            if (p.getCurrentName() != null && "gender".equals(p.getCurrentName())) {
                if ("男".equals(value)) {
                    return 0;
                } else if ("女".equals(value)) {
                    return 1;
                } else {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }
            
            // 对于其他整数字段，尝试正常解析
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
}
