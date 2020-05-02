package com.mjzh.xml;


import org.dom4j.*;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * XML动态解析Demo
 * @author zhou
 */
public class XmlDemo {

    /**
     * 表结构
     * 表一：project key QuMain
     * 表二 project relativePath QuMain salesmanCode
     * @param args
     * @throws UnsupportedEncodingException
     * @throws DocumentException
     */
    public static void main(String[] args) throws UnsupportedEncodingException, DocumentException {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + "\txsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" + "\t<modelVersion>4.0.0</modelVersion>\n" + "\t<groupId>com.mjzh</groupId>\n" + "\t<artifactId>springboot</artifactId>\n" + "\t<version>0.0.1-SNAPSHOT</version>\n" + "\t<name>SpringBoot</name>\n" + "\t<description>Demo project for Spring Boot</description>\n" + "\n" + "\t<!-- Inherit defaults from Spring Boot -->\n" + "\t<parent>\n" + "\t\t<groupId>org.springframework.boot</groupId>\n" + "\t\t<artifactId>spring-boot-starter-parent</artifactId>\n" + "\t\t<version>2.2.0.RELEASE</version>\n" + "\t\t<relativePath/> <!-- lookup parent from repository -->\n" + "\t</parent>\n" + "\n" + "\t<properties>\n" + "\t\t<java.version>1.8</java.version>\n" + "\t</properties>\n" + "\n" + "\t<!-- Add typical dependencies for a web application -->\n" + "\t<dependencies>\n" + "\t\t<dependency>\n" + "\t\t\t<groupId>org.springframework.boot</groupId>\n" + "\t\t\t<artifactId>spring-boot-starter-web</artifactId>\n" + "\t\t</dependency>\n" + "\t\t<dependency>\n" + "\t\t\t<groupId>org.springframework.boot</groupId>\n" + "\t\t\t<artifactId>spring-boot-starter-actuator</artifactId>\n" + "\t\t</dependency>\n" + "\t\t<dependency>\n" + "\t\t\t<groupId>org.springframework.cloud</groupId>\n" + "\t\t\t<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>\n" + "\t\t\t<version>2.1.1.RELEASE</version>\n" + "\t\t</dependency>\n" + "\t\t<dependency>\n" + "\t\t\t<groupId>org.springframework.cloud</groupId>\n" + "\t\t\t<artifactId>spring-cloud-starter-netflix-ribbon</artifactId>\n" + "\t\t\t<version>2.1.1.RELEASE</version>\n" + "\t\t</dependency>\n" + "\t\t<dependency>\n" + "\t\t\t<groupId>org.springframework.boot</groupId>\n" + "\t\t\t<artifactId>spring-boot-starter-test</artifactId>\n" + "\t\t\t<scope>test</scope>\n" + "\t\t\t<exclusions>\n" + "\t\t\t\t<exclusion>\n" + "\t\t\t\t\t<groupId>org.junit.vintage</groupId>\n" + "\t\t\t\t\t<artifactId>junit-vintage-engine</artifactId>\n" + "\t\t\t\t</exclusion>\n" + "\t\t\t</exclusions>\n" + "\t\t</dependency>\n" + "\t\t<dependency>\n" + "\t\t\t<groupId>junit</groupId>\n" + "\t\t\t<artifactId>junit</artifactId>\n" + "\t\t\t<version>RELEASE</version>\n" + "\t\t</dependency>\n" + "\t\t<dependency>\n" + "\t\t\t<groupId>dom4j</groupId>\n" + "\t\t\t<artifactId>dom4j</artifactId>\n" + "\t\t\t<version>1.1</version>\n" + "\t\t\t<scope>test</scope>\n" + "\t\t</dependency>\n" + "\t</dependencies>\n" + "\n" + "\t<!-- Package as an executable jar -->\n" + "\t<build>\n" + "\t\t<plugins>\n" + "\t\t\t<plugin>\n" + "\t\t\t\t<groupId>org.springframework.boot</groupId>\n" + "\t\t\t\t<artifactId>spring-boot-maven-plugin</artifactId>\n" + "\t\t\t</plugin>\n" + "\t\t</plugins>\n" + "\t</build>\n" + "\n" + "</project>\n";
        Document doc = DocumentHelper.parseText(xml);
        Map<String, Object> map = (Map<String, Object>) xml2map(doc.getRootElement());
        System.out.println(map);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("parent", "QuMain");
        Map<String, Object> quMainMap = new HashMap<>();
        quMainMap.put("relativePath", "salesmanCode");
        resultMap.put("QuMain", quMainMap);
        resultMap.put("dependency", "ItemAccilist");
        Map<String, Object> itemAcciListMap = new HashMap<>();
        itemAcciListMap.put("artifactId", "birth");
        itemAcciListMap.put("version", "name");
        itemAcciListMap.put("groupId", "age");
        resultMap.put("ItemAccilist", itemAcciListMap);
        xmlMap(null, map, resultMap);
    }

    public static void xmlList(String key, List<Object> list, Map<String, Object> resultMap) {
        for (int i = 0, len = list.size(); i < len; i++) {
            Object value = list.get(i);
            if (value instanceof List) {
                xmlList(key, (List) value, resultMap);
            } else if (value instanceof Map) {
                xmlMap(key, (Map<String, Object>) value, resultMap);
            }
        }
    }

    private static void xmlMap(String key, Map<String, Object> map, Map<String, Object> resultMap) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof List) {
                xmlList(entry.getKey(), (List) value, resultMap);
            } else if (value instanceof Map) {
                xmlMap(entry.getKey(),(Map<String, Object>) value, resultMap);
            } else {
                Object o = resultMap.get(key);
                if (o != null) {
                    Object o1 = resultMap.get(o);
                    Map<String, Object> valueMap = (Map<String, Object>) o1;
                    Object resultKey = valueMap.get(entry.getKey());
                    System.out.println(resultKey+" "+value);
                }
                //(quMain)
                //(List<ItemAcciList>)
            }
        }
    }

    private static Object xml2map(Element element) {
        System.out.println(element);
        Map<String, Object> map = new HashMap<String, Object>();
        List<Element> elements = element.elements();
        if (elements.size() == 0) {
            map.put(element.getName(), element.getText());
            if (!element.isRootElement()) {
                return element.getText();
            }
        } else if (elements.size() == 1) {
            map.put(elements.get(0).getName(), xml2map(elements.get(0)));
        } else if (elements.size() > 1) {
            // 多个子节点的话就得考虑list的情况了，比如多个子节点有节点名称相同的
            // 构造一个map用来去重
            Map<String, Element> tempMap = new HashMap<String, Element>();
            for (Element ele : elements) {
                tempMap.put(ele.getName(), ele);
            }
            Set<String> keySet = tempMap.keySet();
            for (String string : keySet) {
                Namespace namespace = tempMap.get(string).getNamespace();
                List<Element> elements2 = element.elements(new QName(string, namespace));
                // 如果同名的数目大于1则表示要构建list
                if (elements2.size() > 1) {
                    List<Object> list = new ArrayList<Object>();
                    for (Element ele : elements2) {
                        list.add(xml2map(ele));
                    }
                    map.put(string, list);
                } else {
                    // 同名的数量不大于1则直接递归去
                    map.put(string, xml2map(elements2.get(0)));
                }
            }
        }
        return map;
    }
}