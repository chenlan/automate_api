package framework.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DataFileManager {

    public static <T> T readValue(String resourcePath, Class<T> t) {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        T data = null;
        try {
            data = objectMapper.readValue(DataFileManager.class.getResourceAsStream(resourcePath), t);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static <T> T readValue(String resourcePath, TypeReference<T> t) {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        T data = null;
        try {
            data = objectMapper.readValue(DataFileManager.class.getResourceAsStream(resourcePath), t);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static <T> T readValue(File file, TypeReference<T> t) {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        T data = null;
        try {
            data = objectMapper.readValue(file, t);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static <T> T readValue(File file, Class<T> t) {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        T data = null;
        try {
            data = objectMapper.readValue(file, t);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }


    public static <T> String toJsonString(T t) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static  <T> void toYamlFile(T t,String path){
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            mapper.writeValue(new File(path), t);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<File> findApiDir(String projectPath){
        List<File> fileList = new ArrayList<>();
        File file = new File(projectPath);
        if(file.exists()){
            for (File itemFile: file.listFiles()) {
                if(itemFile.isDirectory() && itemFile.getName().contains("Api")){
                    fileList.add(itemFile);
                }
            }
        }
        else{
            System.out.println("文件不存在!");
        }
        return fileList;
    }

    public static List<File> findFile(String projectPath, String endsWith) {
        List<File> fileList = new ArrayList<>();
        int fileNum = 0, folderNum = 0;
        File file = new File(projectPath);
        if (file.exists()) {
            LinkedList<File> list = new LinkedList<File>();
            File[] files = file.listFiles();
            for (File file2 : files) {
                if (file2.isDirectory()) {
                    list.add(file2);
                    folderNum++;
                } else {
                    if(endsWith!=null && file2.getName().endsWith(endsWith)){
                        fileList.add(file2);
                        fileNum++;
                    }
                }
            }
            File temp_file;
            while (!list.isEmpty()) {
                temp_file = list.removeFirst();
                files = temp_file.listFiles();
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        list.add(file2);
                        folderNum++;
                    } else {
                        if(endsWith!=null && file2.getName().endsWith(endsWith)){
                            fileList.add(file2);
                            fileNum++;
                        }
                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
        System.out.println("文件夹共有:" + folderNum + ",文件共有:" + fileNum);
        return fileList;
    }

    public static String templateFileReplace(String templatePath, HashMap<String, Object> data){
        Writer writer = new StringWriter();
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(DataFileManager.class.getResource(templatePath).getPath());
        mustache.execute(writer, data);
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    public static String templateStringReplace(String content, HashMap<String,Object> data){
        Writer writer = new StringWriter();
        MustacheFactory mf = new DefaultMustacheFactory();
        StringReader reader = new StringReader(content);
        Mustache mustache = mf.compile(reader, content);
        mustache.execute(writer, data);
        try {
            writer.flush();
            return writer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
       return content;
    }
}
