import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SingingThreadDemo {
    public static void main(String[] args) {
        final String lyrics =
                "女：\n" +
                        "入夜渐微凉\n" +
                        "繁花落地成霜\n" +
                        "你在远方眺望\n" +
                        "耗尽所有暮光\n" +
                        "不思量自难相忘\n" +
                        "\n" +
                        "男：\n" +
                        "夭夭桃花凉\n" +
                        "前世你怎舍下\n" +
                        "这一海心茫茫\n" +
                        "还故作不痛不痒不牵强\n" +
                        "都是假象\n" +
                        "\n" +
                        "女：\n" +
                        "凉凉夜色为你思念成河\n" +
                        "化作春泥呵护着我\n" +
                        "男：\n" +
                        "浅浅岁月拂满爱人袖\n" +
                        "片片芳菲入水流\n" +
                        "女：\n" +
                        "凉凉天意潋滟一身花色\n" +
                        "落入凡尘伤情着我\n" +
                        "男：\n" +
                        "生劫易渡情劫难了\n" +
                        "折旧的心还有几分前生的恨\n" +
                        "还有几分\n" +
                        "\n" +
                        "合：\n" +
                        "前生的恨";

        final ReentrantLock lock = new ReentrantLock();
        final Condition maleSingerCondition = lock.newCondition();
        final Condition femaleSingerCondition = lock.newCondition();
        final Condition chorusCondition = lock.newCondition();

        final boolean[] maleNext = {true}; // 男歌手是否先唱

        Thread maleSingerThread = new Thread(() -> {
            lock.lock();
            try {
                String[] lines = lyrics.split("\n男：\n");
                String[] maleLines = lines[1].split("\n\n女：\n");
                System.out.println("男歌手开始唱歌...");
                for (String line : maleLines) {
                    if (!maleNext[0]) {
                        maleSingerCondition.await();
                    }
                    System.out.println("男：" + line);
                    femaleSingerCondition.signal();
                    chorusCondition.await();
                    maleNext[0] = false;
                }
                // 唤醒女歌手线程，完成最后一次合唱
                femaleSingerCondition.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });

        Thread femaleSingerThread = new Thread(() -> {
            lock.lock();
            try {
                String[] lines = lyrics.split("\n女：\n");
                String[] femaleLines = lines[1].split("\n\n男：\n");
                System.out.println("女歌手开始唱歌...");
                for (String line : femaleLines) {
                    if (maleNext[0]) {
                        femaleSingerCondition.await();
                    }
                    System.out.println("女：" + line);
                    chorusCondition.signal();
                    maleSingerCondition.await();
                    maleNext[0] = true;
                }
                // 唤醒男歌手线程，以便进行第二遍合唱
                maleSingerCondition.signal();
                chorusCondition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });

        maleSingerThread.start();
        femaleSingerThread.start();
    }
}