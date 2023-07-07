package org.example;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liuzhaoxu
 * @date 2023年07月05日 10:00
 */
public class Chorus {

    private static String song = "";
    private static int tuCount = 0;
    private final Lock lock = new ReentrantLock();
    private final Condition nvCondition = lock.newCondition();
    private final Condition nanCondition = lock.newCondition();
    private static Scanner scanner = null;
    private static final String NV_TAG = "女";
    private static final String NAN_TAG = "男";
    private static final String TU_TAG = "合";
    //唱歌
    public static void extractLyrics(String lyrics, String singer) {
        int femaleIndex = lyrics.indexOf(singer + "：");
        if (femaleIndex != -1) {
            int newlineIndex = lyrics.indexOf("\n", femaleIndex);
            if (newlineIndex != -1) {
                String substring = lyrics.substring(femaleIndex + 2, newlineIndex);
                String name = Thread.currentThread().getName();
                String firstWord = Chorus.getFirstWord(song);
                if (Chorus.TU_TAG.equals(firstWord)) {
                    System.out.println("线程【" + name + "】||合唱:" + substring);

                } else {

                    System.out.println("线程【" + name + "】:" + substring);
                }

            }
        }
    }

    //清洗数据
    public static void modifyLyrics(String lyrics) {
        //文件内容：Hello World|Hello Zimug
        String fileName = "D:\\Workspace\\IDEAWorkspace\\untitled\\src\\main\\resources\\song.txt";
        try {
            scanner = new Scanner(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        StringBuffer stringBuffer = new StringBuffer();
        while (scanner.hasNextLine()) {
            //按行读取字符串
            String line = scanner.nextLine();
            stringBuffer.append(line);
//            System.out.println(line);
        }
        song = stringBuffer.toString().replaceAll("(女|男|合)：", "\n$1：");
        int newLineIndex = song.indexOf("\n");
        if (newLineIndex != -1) {
            song = song.substring(0, newLineIndex) + song.substring(newLineIndex + 1);
            System.out.println(song);
        }
    }

    //获取前置标识
    public static String getFirstWord(String lyrics) {
        int colonIndex = lyrics.indexOf("：");
        if (colonIndex != -1) {
//            System.out.println(lyrics.substring(0, colonIndex) + "<==>开头");
            return lyrics.substring(0, colonIndex);
        }
        return "";
    }

    //刷新剩余歌词
    public static void refreshSong(String oldSong,String start) {



        // 查找以指定性别开头的第一行的结束位置
        int endIndex = oldSong.indexOf("\n");
//        System.out.println(endIndex);
        if (endIndex == -1) {
            song = "";
            return;
        }
        // 获取第一行内容
        String firstLine = oldSong.substring(0, endIndex);

        if (firstLine.startsWith(start + "：")) {
            // 删除第一行
            String output = oldSong.substring(endIndex + 1);
            song = output;
        } else {
            song = oldSong;// 不以指定性别开头，不做修改
        }

        // 使用正则表达式匹配并替换第一行
//        String newSong = "";
//        int newLineIndex = oldSong.indexOf("\n");
//        if (newLineIndex != -1) {
//            newSong = oldSong.substring(newLineIndex + 1);
//        }
//        song = newSong;
    }


    private void nvSing() throws InterruptedException {
        while (!"".equals(song)) {
            lock.lock();
            try {
                System.out.println("女唱========");
                String firstWord = Chorus.getFirstWord(song);
                if (!Chorus.NV_TAG.equals(firstWord)) {
                    if (Chorus.TU_TAG.equals(firstWord)) {
                        int count = tuCount;
                        if (count == 0 || count == 1) {
                            Chorus.extractLyrics(song, "合");
                            if (count == 0) {
                                tuCount = 1;
                            } else if (count == 1) {
                                tuCount = 2;
                                Chorus.refreshSong(song,"合");
                            }
                        }
                    }
                    System.out.println("女等待========");
                    nvCondition.await();
                } else {
                    tuCount = 0;
                    Chorus.extractLyrics(song, "女");
                        Chorus.refreshSong(song,"女");

                }
                nanCondition.signal();
            } finally {
                System.out.println("女放锁========");
                lock.unlock();
            }
        }
    }


    private void nanSing() throws InterruptedException {
        while (!"".equals(song)) {
            lock.lock();
            try {
                System.out.println("男唱========");
                String firstWord = Chorus.getFirstWord(song);
                if (!Chorus.NAN_TAG.equals(firstWord)) {
                    if (Chorus.TU_TAG.equals(firstWord)) {
                        int count = tuCount;
                        if (count == 0 || count == 1) {
                            Chorus.extractLyrics(song, "合");
                            if (count == 0) {
                                tuCount = 1;
                            } else if (count == 1) {
                                tuCount = 2;
                                Chorus.refreshSong(song,"合");
                            }
                        }
                    }
                    System.out.println("男等待========");
                    nanCondition.await();
                } else {
                    tuCount = 0;
                    Chorus.extractLyrics(song, "男");
                        Chorus.refreshSong(song,"男");
                }
                nvCondition.signal();
            } finally {
                System.out.println("男放锁========");
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("================歌词====================");
        Chorus.modifyLyrics(song);
        System.out.println("================开始演唱====================");

        Chorus chorus = new Chorus();

        new Thread(() -> {
            try {

                chorus.nvSing();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "女").start();

        new Thread(() -> {
            try {

                chorus.nanSing();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "男").start();


    }

}
