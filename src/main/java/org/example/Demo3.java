package org.example;

import com.sun.istack.internal.NotNull;

import javax.xml.transform.Source;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author liuzhaoxu
 * @date 2023年05月31日 9:49
 */
public class Demo3 {
    //    题目3
//            第三题
//
//    定义一个函数式接口NumberToString,其中抽象方法String convert(int num)，使用注解@FunctionalInterface
//    在测试类中定义static void decToHex(int num ,NumberToString nts), 该方法的预期行为是使用nts将一个十进制整数转换成十六进制表示的字符串，
//    tips:已知该行为与Integer类中的toHexString方法一致
//    测试decToHex (),使用方法引用完成需求
//    代码如下：
    static void decToHex(int num ,NumberToString nts) {
        String convert = nts.convert(num);
        char[] chars = convert.toCharArray();
        ArrayList<Character> list = new ArrayList<>();
        for (int i = 0; i < chars.length; i++) {
            list.add(chars[i]);
        }
       list.forEach(System.out::println);
    }
//    public static void main(String[] args) {
////        fdasfas
//        List<String> strings = Demo3.sortGetTop3LongWords1("ds fdasfasa dfs afsa +++++++++++ ======");
//        strings.forEach(System.out::println);
//        //
//        System.out.println("\n=========================================\n");
//        List<String> strings2 = Demo3.sortGetTop3LongWords2("ds fdasfasa dfs afsa +++++++++++ ======");
//        strings2.forEach(System.out::println);
//
//    }

    public static List<String> sortGetTop3LongWords1(@NotNull String sentence) {
        // 先切割句子，获取具体的单词信息
        String[] words = sentence.split(" ");
        List<String> wordList = new ArrayList<>();
        // 循环判断单词的长度，先过滤出符合长度要求的单词
        for (String word : words) {
            if (word.length() > 5) {
                wordList.add(word);
            }
        }
        // 对符合条件的列表按照长度进行排序
        wordList.sort((o1, o2) -> o2.length() - o1.length());
        // 判断list结果长度，如果大于3则截取前三个数据的子list返回
        if (wordList.size() > 3) {
            wordList = wordList.subList(0, 3);
        }
        return wordList;
    }

    public static List<String> sortGetTop3LongWords2(@NotNull String sentence) {
        return Arrays
//                分割
                .stream(sentence.split(" "))
//                筛选长度
                .filter(i -> i.length() > 5)
                .sorted((s1,s2)->-(s1.length()-s2.length()))
                .limit(3)
//                收集
                .collect(Collectors.toList());
    }

    /**
     * 演示map的用途：一对一转换
     */
     public  static  void stringToIntMap() {
        List<String> ids = Arrays.asList("205", "105", "308", "469", "627", "193", "111");
        // 使用流操作
        List<User> results = ids.stream()
                .map(id -> {
                    User user = new User();
                    user.setId(id);
                    return user;
                })
                .collect(Collectors.toList());
        System.out.println(results);
    }
    public  static  void stringToIntMap2() {
        List<String> ids = Arrays.asList("205", "105", "308", "469", "627", "193", "111");

        System.out.println(
                ids.stream().map(i -> new User(i)).collect(Collectors.toList())
        );
    }

//    public static void main(String[] args) {
//        Demo3.stringToIntMap();
//        Demo3.stringToIntMap2();
//
//    }
    public static void stringToIntFlatmap3() {
        List<String> sentences = Arrays.asList("hello world","Jia Gou Wu Dao");
        // 使用流操作
        List<String> results = sentences.stream()
                .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
                .collect(Collectors.toList());
        System.out.println(results);
    }

    public static void stringToIntFlatmap4() {
        List<String> sentences = Arrays.asList("hello world","Jia Gou Wu Dao");

        System.out.println(
                sentences.stream().flatMap((i) -> Arrays.stream(i.split(" "))).collect(Collectors.toList())
        );
    }


//    public static void main(String[] args) {
//        Demo3.stringToIntFlatmap3();
//        Demo3.stringToIntFlatmap4();
//
//    }

    public static void testPeekAndforeach() {
        List<String> sentences = Arrays.asList("hello world","Jia Gou Wu Dao");
        // 演示点1： 仅peek操作，最终不会执行
        System.out.println("----before peek----");
        sentences.stream().peek(sentence -> System.out.println(sentence));
        System.out.println("----after peek----");
        // 演示点2： 仅foreach操作，最终会执行
        System.out.println("----before foreach----");
        sentences.stream().forEach(sentence -> System.out.println(sentence));
        System.out.println("----after foreach----");
        // 演示点3： peek操作后面增加终止操作，peek会执行
        System.out.println("----before peek and count----");
        sentences.stream().peek(sentence -> System.out.println(sentence)).count();
        System.out.println("----after peek and count----");

    }

    public static void main(String[] args) {
        String str = "";
        if (!"".equals(str)) {
            char firstChar = str.charAt(0);
            char secondChar = str.charAt(1);
            System.out.println("" + firstChar + secondChar);
            return;
        }
        System.out.println("dd");

    }


}
