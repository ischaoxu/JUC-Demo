package org.example;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author liuzhaoxu
 * @date 2023年07月06日 8:03
 */
public class ChorusPlus {
    private static final String NV_TAG = "女：";
    private static final String NAN_TAG = "男：";
    private static final String TU_TAG = "合：";
    private static final String NULL_TAG = "";

    private static int nvCount = 0;
    private static int nanCount = 0;
    private static int tuCount = 0;

    private final Lock lock = new ReentrantLock();
    private final Condition nvCondition = lock.newCondition();
    private final Condition nanCondition = lock.newCondition();

    private static Scanner scanner = null;

    private static String song = "";

    public ChorusPlus() {
        ChorusPlus.getSong();
    }

    public static void main(String[] args) {
        ChorusPlus chorusPlus = new ChorusPlus();
        new Thread(() -> {
            try {
                chorusPlus.nvSing();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "女").start();

        new Thread(() -> {
            try {
                chorusPlus.nanSing();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "男").start();


    }

    private void nanSing() throws InterruptedException {
        while (!"".equals(song)) {
            Thread.sleep(400);
            lock.lock();
            String name = Thread.currentThread().getName();
            switch (ChorusPlus.firstZi()) {
                case ChorusPlus.NV_TAG:
                    nanCondition.await();
                    break;
                case ChorusPlus.NAN_TAG:
                    ChorusPlus.nvCount = 0;
                    ChorusPlus.delFirstLine();
                    System.out.println("线程【" + name + "】" + "唱：" + ChorusPlus.getFirstLine());
                    ChorusPlus.delFirstLine();
                    ChorusPlus.nanCount = 1;
                    break;
                case ChorusPlus.TU_TAG:
                    ChorusPlus.nanCount = -1;
                    ChorusPlus.nvCount = 0;
                    ChorusPlus.delFirstLine();
                    ChorusPlus.tuCount = 1;
                    System.out.println("线程【" + name + "】" + "|合唱|：" + ChorusPlus.getFirstLine());
                    break;
                case ChorusPlus.NULL_TAG:
                    if (!"".equals(song)) {
                        System.out.println("=======停顿=====");
                    }
                        ChorusPlus.delFirstLine();
                        break;
                default:
                    if (ChorusPlus.nanCount != 0 && ChorusPlus.nvCount == 0 && ChorusPlus.nanCount != -1) {
                        System.out.println("线程【" + name + "】" + "唱：" + ChorusPlus.getFirstLine());
                        ChorusPlus.delFirstLine();
                    } else if (ChorusPlus.nvCount == -1 ) {
                            if (ChorusPlus.tuCount == 0) {
                                ChorusPlus.tuCount = 1;
                                System.out.println("线程【" + name + "】" + "|合唱|：" + ChorusPlus.getFirstLine());
                                ChorusPlus.nanCount = -1;
                                ChorusPlus.nvCount = 0;
                            } else if (ChorusPlus.tuCount == 1) {
                                ChorusPlus.tuCount = 2;
                                System.out.println("线程【" + name + "】" + "|合唱|：" + ChorusPlus.getFirstLine());
                                ChorusPlus.delFirstLine();
                                ChorusPlus.nanCount = -1;
                                ChorusPlus.nvCount = 0;
                            } else if (ChorusPlus.tuCount == 2) {
                                ChorusPlus.tuCount = 0;
                            }
                    }
            }
            nvCondition.signal();
            lock.unlock();
        }

    }

    private void nvSing() throws InterruptedException {
        while (!"".equals(song)) {
            Thread.sleep(400);
            lock.lock();
            String name = Thread.currentThread().getName();
            switch (ChorusPlus.firstZi()) {
                case ChorusPlus.NAN_TAG:
                    nvCondition.await();
                    break;
                case ChorusPlus.NV_TAG:
                    ChorusPlus.nanCount = 0;
                    ChorusPlus.delFirstLine();
                    System.out.println("线程【" + name + "】" + "唱：" + ChorusPlus.getFirstLine());
                    ChorusPlus.delFirstLine();
                    ChorusPlus.nvCount = 1;
                    break;
                case ChorusPlus.TU_TAG:
                    ChorusPlus.nvCount = -1;
                    ChorusPlus.nanCount = 0;
                    ChorusPlus.delFirstLine();
                    ChorusPlus.tuCount = 1;
                    System.out.println("线程【" + name + "】" + "|合唱|：" + ChorusPlus.getFirstLine());
                    break;
                case ChorusPlus.NULL_TAG:
                    if (!"".equals(song)) {
                        System.out.println("=======停顿=====");
                    }
                        ChorusPlus.delFirstLine();
                        break;
                default:
                    if ( ChorusPlus.nvCount != 0 &&ChorusPlus.nanCount == 0 &&ChorusPlus.nvCount != -1) {

                        System.out.println("线程【" + name + "】" + "唱：" + ChorusPlus.getFirstLine());
                        ChorusPlus.delFirstLine();

                    } else if (ChorusPlus.nanCount == -1 ) {
                        if (ChorusPlus.tuCount == 0) {
                            ChorusPlus.tuCount = 1;
                            System.out.println("线程【" + name + "】" + "|合唱|：" + ChorusPlus.getFirstLine());
                            ChorusPlus.nanCount = 0;
                            ChorusPlus.nvCount = -1;
                        } else if (ChorusPlus.tuCount == 1 ) {
                            ChorusPlus.tuCount = 2;
                            System.out.println("线程【" + name + "】" + "|合唱|：" + ChorusPlus.getFirstLine());
                            ChorusPlus.delFirstLine();
                            ChorusPlus.nanCount = 0;
                            ChorusPlus.nvCount = -1;
                        } else if (ChorusPlus.tuCount == 2) {
                            ChorusPlus.tuCount = 0;
                        }
                    }
            }
            nanCondition.signal();
            lock.unlock();
        }
    }

    /**
     * 获取第一行
     *
     * @return java.lang.String
     * @author liuzhaoxu
     * @date 2023/7/6 8:11
     */
    public static String getFirstLine() {
        // 使用split()方法将字符串分割为多行
        String[] lines = song.split("\\r?\\n");
        // 访问第一行
        String firstLine = lines[0];

        if ("".equals(song)) {
            System.out.println("没有剩余歌词，唱完啦~~~");
        }


        return firstLine;
    }

    /**
     * 删除第一行
     *
     * @author liuzhaoxu
     * @date 2023/7/6 8:41
     */
    public static void delFirstLine() {
        // 判断第一行是否以换行符开头
        if (song.startsWith("\n")) {
            // 删除第一个换行符
            song = song.substring(1);
//            System.out.println("=================剩余歌词==============\n" + song + "===>\\n开头");
            return;
        }
        // 查找第一个换行符的索引
        int firstNewlineIndex = song.indexOf("\n");
        // 移除第一行字符串
        song = song.substring(firstNewlineIndex + 1);
//        System.out.println("=================剩余歌词==============\n" + song + "===>delFirstLine非\\n开头");
    }

    /**
     * 获取第一个“X：”字
     *
     * @return java.lang.String
     * @author liuzhaoxu
     * @date 2023/7/6 9:49
     */
    public static String firstZi() {
        String firstLine = ChorusPlus.getFirstLine();
        if (!"".equals(firstLine)) {
            char firstChar = firstLine.charAt(0);
            char secondChar = firstLine.charAt(1);
            return "" + firstChar + secondChar;
        }
        return "";
    }

    //获取歌词
    public static void getSong() {
        //文件内容：Hello World|Hello Zimug
        String fileName = "D:\\Workspace\\IDEAWorkspace\\untitled\\src\\main\\resources\\song2.txt";
        try {
            scanner = new Scanner(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        StringBuffer stringBuffer = new StringBuffer();
        while (scanner.hasNextLine()) {
            //按行读取字符串
            String line = scanner.nextLine();
            stringBuffer.append(line + "\n");
//            System.out.println(line);
        }
        song = stringBuffer.toString();
    }


}
