package com.flying.fish.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * @Description json转换测试
 * @Author jianglong
 * @Date 2020/06/12
 * @Version V1.0
 */
public class JsonMain {

    @Test
    public void beanToJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = new User("jack",22);
        StringWriter sw = new StringWriter();
        //JsonGenerator gen = new JsonFactory().createGenerator(sw);
        //objectMapper.writeValue(gen, user);
        //gen.close();
        objectMapper.writeValue(sw, user);
        String ss = sw.toString();
        System.out.println(ss);
    }

    @Test
    public void jsonToBean() throws IOException {
        String jsonStr = "{\"name\":\"jack\",\"age\":22}";
        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.readValue(jsonStr, User.class);
        System.out.println(user.toString());
    }

    @Test
    public void mapToJson() throws IOException {
        Map<String,Object> userMap = new HashMap<>();
        userMap.put("name","jack");
        userMap.put("age", 22);
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter sw = new StringWriter();
        objectMapper.writeValue(sw, userMap);
        System.out.println(sw.toString());
    }

    @Test
    public void jsonToMap() throws IOException {
        String jsonStr = "{\"name\":\"jack\",\"age\":22}";
        ObjectMapper objectMapper = new ObjectMapper();
        Map userMap = objectMapper.readValue(jsonStr, Map.class);
        System.out.println(userMap.toString());
    }

    @Test
    public void jsonToList() throws IOException {
        String jsonStr = "[\"name\",\"jack\",\"age\",22]";
        ObjectMapper objectMapper = new ObjectMapper();
        //Object [] objs = objectMapper.readValue(jsonStr, Object[].class);
        //List<Object> list = Arrays.asList(objs);
        List<Object> list = objectMapper.readValue(jsonStr, ArrayList.class);
        list.forEach(v->System.out.println(v));
    }

    @Test
    public void listToJson() throws IOException {
        List<Object> list = Arrays.asList("name","jack","age",22);
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter sw = new StringWriter();
        objectMapper.writeValue(sw, list);
        System.out.println(sw.toString());
    }

    @Data
    public static class User{
        private String name;
        private int age;

        public User(){
        }

        public User(String name, int age){
            this.name = name;
            this.age = age;
        }
    }
}
