package tool;

import com.ada.routecount.main.model.RouteTrie;
import com.ada.routecount.main.model.TrieNode;
import model.BitMap;
import model.Route;
import model.Trajectory;
import tool.cluster.distancefunction.EditDistanceWithWeightFunction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lzj on 2016/10/12.
 */
public class TPRO {
    long o_id;
    long d_id;
    private static int K = 5;


    BitMap bitMap = new BitMap(BitMap.PARTIAL, "/home/zhujie/lvzhongjian/bitmap_100");
    private Route[] popularRoutes = new Route[K];
    private double[] weightOfPopularRoutes = new double[K];
    private EditDistanceWithWeightFunction distanceWithWeightFunction = new EditDistanceWithWeightFunction(null);

    public void setO_idAndD_id(long o_id, long d_id, Route[] routes) {
        System.out.println("setO_idAndD_id method and routes's size is " + routes.length);
        this.o_id = o_id;
        this.d_id = d_id;
        BitSet bs = (BitSet) bitMap.get(o_id).clone();
        bs.and(bitMap.get(d_id));
        findKPopularRoutes(routes, bs);
    }

    private void findKPopularRoutes(Route[] routes, BitSet bs) {
        System.out.println("in findKPopularRoutes method");
        List<Integer>[] freqAll = new LinkedList[routes.length];
        for (int i = 0; i < routes.length; i++) {
            Route route = routes[i];
            List<Long> vertex_ids = route.getVertex_ids();
            List<Integer> freq = new LinkedList<>();
            for (int i1 = 0; i1 < vertex_ids.size(); i1++) {
                BitSet clone = (BitSet) bitMap.get(vertex_ids.get(i1)).clone();
                clone.and(bs);
                freq.add(clone.cardinality());
            }
            Collections.sort(freq, c);//从大到小排序
            freqAll[i] = freq;
        }
        findK(routes, freqAll);
        double sumall = 0;
        for (int i = 0; i < weightOfPopularRoutes.length; i++) {
            sumall += weightOfPopularRoutes[i];
        }
        for (int i = 0; i < weightOfPopularRoutes.length; i++) {
            weightOfPopularRoutes[i] = weightOfPopularRoutes[i] / sumall;
        }
    }

    private void findK(Route[] routes, List<Integer>[] freqAll) {
        System.out.println("in findK method");
        //find k
        int hasFound = 0;
        int index = 0;
        int ith_list = 0;
        int end_site = freqAll.length - 1;
        while (hasFound != K) {
            sort(freqAll, ith_list, end_site, index, routes);
            while (hasFound != K) {
                if (index >= freqAll[ith_list].size()) {
                    Route route = routes[ith_list++];
                    popularRoutes[hasFound++] = route;
                    System.out.println(hasFound - 1 + ", " + route.getId());
                    weightOfPopularRoutes[hasFound - 1] = getSumFreq(freqAll[ith_list - 1]);
                } else {
                    break;
                }
            }
            if (hasFound != K) {
                int temp = freqAll[ith_list].get(index);
                int count = 0;
                for (int i = ith_list; i < end_site; i++) {
                    if (temp == freqAll[i].get(index)) {
                        count++;
                        continue;
                    }
                    break;
                }
                if (count <= K - hasFound) {
                    for (int i = 0; i < count; i++) {
                        Route route = routes[ith_list + i];
                        popularRoutes[hasFound++] = route;
                        System.out.println(hasFound - 1 + ", " + route.getId());
                        weightOfPopularRoutes[hasFound - 1] = getSumFreq(freqAll[ith_list + i]);
                    }
                    ith_list = ith_list + count;
                    index++;
                    continue;
                } else {
                    end_site = ith_list + count - 1;
                    index++;
                }
            }
        }
    }

    private double getSumFreq(List<Integer> integers) {
        double sum = 0;
        for (int i = 0; i < integers.size(); i++) {
            sum += integers.get(i);
        }
        return sum;
    }

    /**
     * 对freqAll的第start到第end的list按list的第index列从大到小排序,若list的第index列没有，则比有值的大
     * 对应的routes跟着改变
     *
     * @param freqAll
     * @param start
     * @param end
     * @param index
     * @param routes
     */
    private void sort(List<Integer>[] freqAll, int start, int end, int index, Route[] routes) {
        quickSort(freqAll, start, end, index, routes);
    }


    private void quickSort(List<Integer>[] freqAll, int start, int end, int index, Route[] routes) {
        if (start >= end)
            return;
        List<Integer> list = freqAll[start];
        Route route = routes[start];
        int i = start;
        int j = end;
        while (i != j) {
            while (i != j) {
                if (index >= freqAll[j].size() || (index < list.size() && freqAll[j].get(index) > list.get(index))) {
                    freqAll[i] = freqAll[j];
                    routes[i] = routes[j];
                    i++;
                    break;
                }
                j--;
            }
            while (i != j) {
                if (index >= list.size() || (index < freqAll[j].size() && freqAll[j].get(index) < list.get(index))) {
                    freqAll[j] = freqAll[i];
                    routes[j] = routes[i];
                    j--;
                    break;
                }
                i++;
            }
        }
        freqAll[i] = list;
        routes[i] = route;
        quickSort(freqAll, start, i - 1, index, routes);
        quickSort(freqAll, i + 1, end, index, routes);
    }

    public double score(Trajectory trajectory) {
        double score = 0;
        for (int i = 0; i < K; i++) {
            score += (weightOfPopularRoutes[i] * distanceWithWeightFunction.distance(popularRoutes[i], trajectory));
        }
        return score;
    }

    Comparator<Integer> c = new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            if (o1 > o2)
                return -1;
            else if (o1 < o2)
                return 1;
            else
                return 0;
        }
    };

    public static void main(String[] args) {
        TPRO tpro = new TPRO();
        String pathname = "/home/zhujie/lvzhongjian/search/routetree/";
        File dir = new File(pathname);
        String[] list = dir.list();
//        list[0] = "60560416648_59567301461.xml";
//        list[1] =  "60560416648_60560412767.xml";
        for (int i = 0; i < list.length; i++) {
            String filename = list[i];
            System.out.println("filename is " + filename);
            long oid;
            long did;

//            filename = "60560416648_59567301461.xml";
            Pattern p = Pattern.compile("(\\d*)_(\\d*).xml");
            Matcher matcher = p.matcher(filename);
            if(!matcher.find())
                continue;
            oid = new Long(matcher.group(1));
            did = new Long(matcher.group(2));

            RouteTrie routeTrie = RouteTrie.readFromXml(pathname + filename);

            Set<TrieNode> triNodesOfDesitination = routeTrie.getTriNodesOfDesitination(routeTrie.d_id());
            java.util.Iterator<TrieNode> iterator1 = triNodesOfDesitination.iterator();
            BitMap bitMap = new BitMap(BitMap.PARTIAL, "/home/zhujie/lvzhongjian/bitmap_100");
            File file = new File(pathname + filename + ".tpro.result");
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(file));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Trajectory[] rs = new Trajectory[triNodesOfDesitination.size()];
            int index = 0;
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
                trajectory.setId(route_id);
                rs[index++] = trajectory;
            }

            tpro.setO_idAndD_id(oid, did, rs);
            for (int i1 = 0; i1 < rs.length; i1++) {
                long l = System.currentTimeMillis();
                double score = tpro.score(rs[i1]);
                long time = System.currentTimeMillis() - l;
                try {
                    bw.write("the " + rs[i1].getId() + " tpro score is " + score + " , use time " + time);
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
