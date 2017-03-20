package tool;

import com.ada.routecount.main.model.RouteTrie;
import com.ada.routecount.main.model.TrieNode;
import model.BitMap;
import model.Trajectory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by lzj on 2016/10/11.
 */
public class IBAT {

    //测试次数
    int TRY_NUMBER = 100;
    //抽取的样例轨迹id数量
    int SAMPLE_SIZE = 256;

    //结束ibat算法的轨迹数
    int END_SIZE = 3;
    //最大抽取边次数
    int MAX_TEST_SIZE = 60;

    Random random = new Random();

    BitMap bitMap = new BitMap(BitMap.PARTIAL, "/home/zhujie/lvzhongjian/bitmap_100");

    long time = 0;
    long getBitmapTime = 0;


    private BitSet getFromBitMap(long id) {
        long time1 = System.currentTimeMillis();
        BitSet bitSet = bitMap.get(id);
        getBitmapTime += (System.currentTimeMillis() - time1);
        return bitSet;
    }

    private void cleanTime() {
        time = 0;
        getBitmapTime = 0;
    }


    public double[] scoreAndUseTime(Trajectory trajectory) {
        double[] ts = new double[2];
        cleanTime();
        long time1 = System.currentTimeMillis();
        double score = score(trajectory);
        ts[1] = System.currentTimeMillis() - time1 - getBitmapTime;
        ts[0] = score;
        return ts;
    }

    public double score(Trajectory trajectory) {
        double score = 0;
        List<Long> vertex_ids = trajectory.getVertex_ids();
        long o_id = vertex_ids.get(0);
        long d_id = vertex_ids.get(vertex_ids.size() - 1);
        BitSet bitSet1 = getFromBitMap(o_id);
        BitSet bitSet = (BitSet) getFromBitMap(d_id).clone();
        bitSet.and(bitSet1);
        int all_count = 0;
        for (int i = 0; i < TRY_NUMBER; i++) {
            BitSet bitSet2 = randomSamples(bitSet);
            int count = 0;
            do {
                count = count + 1;
                long vertexid = vertex_ids.get(random.nextInt(vertex_ids.size()));
                bitSet2.and(getFromBitMap(vertexid));
                if (count == MAX_TEST_SIZE)
                    bitSet2 = new BitSet();
            } while (bitSet2.cardinality() > END_SIZE);
//            System.out.println("count is "+count);
            all_count += count;
        }
        double c = 2 * H(SAMPLE_SIZE - 1) - 2 * (SAMPLE_SIZE - 1) / (double) SAMPLE_SIZE;
        score = Math.pow(2, -((all_count / (double) TRY_NUMBER) / c));
        return score;
    }

    private double H(int i) {
        return Math.log1p(i - 1) + 0.57721566;//logip(x) = ln(x+1)
    }

    private List<Integer> randomSamples(List<Integer> origin) {
        List<Integer> set = new LinkedList<>();
        while (set.size() < SAMPLE_SIZE) {
//            System.out.println(set.size());
            set.add(origin.get(random.nextInt(origin.size())));
        }
        return set;
    }

    private BitSet randomSamples(BitSet origin) {
        BitSet bs = new BitSet();
        List<Integer> origin1 = new LinkedList<>();
        for (int i = 0; i < origin.length(); ) {
            int x = origin.nextSetBit(i);
            origin1.add(new Integer(x));
            i = x + 1;
        }
        List<Integer> set = randomSamples(origin1);
//        System.out.println(set);
        set.forEach(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                bs.set(integer);
            }
        });
        return bs;
    }

    public static void main(String[] args) {
        IBAT ibat = new IBAT();
        String pathname = "/home/zhujie/lvzhongjian/search/routetree/";
        File dir = new File(pathname);
        String[] list = dir.list();
//        list[0] = "60560416648_59567301461.xml";
//        list[1] =  "60560416648_60560412767.xml";
        for (int i = 0; i < list.length; i++) {
            String filename = list[i];
            System.out.println("filename is " + filename);
//            filename = "60560416648_59567301461.xml";
            if (!filename.endsWith("xml"))
                continue;
            RouteTrie routeTrie = RouteTrie.readFromXml(pathname + filename);

            Set<TrieNode> triNodesOfDesitination = routeTrie.getTriNodesOfDesitination(routeTrie.d_id());
            java.util.Iterator<TrieNode> iterator1 = triNodesOfDesitination.iterator();
            BitMap bitMap = new BitMap(BitMap.PARTIAL, "/home/zhujie/lvzhongjian/bitmap_100");
            File file = new File(pathname + filename + ".ibat.result");
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(file));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            while (iterator1.hasNext()) {
                TrieNode next = iterator1.next();
                int route_id = next.route_id();
                scala.collection.immutable.List<Object> objectList = next.vertex_ids();
                List<Long> edgeids = new LinkedList<>();
                scala.collection.Iterator<Object> iterator = objectList.iterator();
                while (iterator.hasNext()) {
                    Long next1 = (Long) iterator.next();
                    edgeids.add(next1);
                }
                Trajectory trajectory = new Trajectory();
                trajectory.setEdge_ids(edgeids);
                long l = System.currentTimeMillis();
                double[] doubles = ibat.scoreAndUseTime(trajectory);
                double score = doubles[0];
                long time = System.currentTimeMillis() - l;
                try {
                    bw.write("the " + route_id + " ibat score is " + score + " , use time " + doubles[1] + ", contain bitmap time " + time);
                    bw.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (bw != null) {
                try {
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        bw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }
}
