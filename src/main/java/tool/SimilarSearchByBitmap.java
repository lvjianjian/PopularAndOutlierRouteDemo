package tool;

import com.ada.routecount.main.model.RouteTrie;
import com.ada.routecount.main.model.TrieNode;
import model.*;
import service.cmpl.RoutesService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by lzj on 2016/10/9.
 */
public class SimilarSearchByBitmap extends Search {
    BitMap bitMap = new BitMap(BitMap.PARTIAL, "/home/zhujie/lvzhongjian/bitmap_100");



    int all = 0;
    long time = 0;
    long getBitmapTime = 0;




    private void timeClean() {
        time = 0;
        getBitmapTime = 0;
    }

    @Override
    protected boolean canSearch() {
        if (bitMap == null)
            return false;
        if (definition_id == -1)
            return false;
        return true;
    }

    private boolean isNewVertex(SubRoute parent, long next_vertex_id) {
        boolean isNew = true;
        if (parent == null)
            return isNew;
        while (parent != null) {
            if (parent.getCurrent_end_vertex_id() == next_vertex_id) {
                isNew = false;
                break;
            }
            parent = parent.getPrivous();
        }

        return isNew;
    }



    @Override
    public double[] scoreAndUseTime(Trajectory trajectory) {
        timeClean();
        double[] st = new double[2];
        long time1 = System.currentTimeMillis();
        double score = score(trajectory);
        st[1] = (System.currentTimeMillis() - time1) - getBitmapTime;
        st[0] = score;
        return st;
    }


    private BitSet getFromBitMap(long id) {
        long time1 = System.currentTimeMillis();
        BitSet bitSet = bitMap.get(id);
        getBitmapTime += (System.currentTimeMillis() - time1);
        return bitSet;
    }

    @Override
    protected double getScoreOfOneRoute(SubRoute r) {
        return (double) r.getNow_bitset().cardinality() / all;
    }

    @Override
    protected void addSubRoutes(long current_end_vertex_id, LinkedList<SubRoute> stack, SubRoute pop, Trajectory trajectory) {
        Map<Long, Set<Edge>> transfers =
                RoutesService.getRoadNet().getTransfers();
        Set<Edge> set = transfers.get(current_end_vertex_id);
        if (set == null)
            return;
        set.forEach(new Consumer<Edge>() {
            @Override
            public void accept(Edge edge) {
                SubRoute subRoute = new SubRoute();
                subRoute.setPrivous(pop);

                BitSet set1 = getFromBitMap(edge.getEndVertex().getId());
                if (set1 != null) {
                    if (pop == null) {//第一条边
                        BitSet clone = ((BitSet) getFromBitMap(current_end_vertex_id).clone());
                        clone.and((getFromBitMap(getDeftination_id())));
                        all = clone.cardinality();
                        System.out.println("all is " + all);
                        clone.and(set1);
                        subRoute.setNow_bitset(clone);
                    } else {
                        if (isNewVertex(pop, edge.getEndVertex().getId())) {//确保是一个新的点
                            BitSet clone = ((BitSet) pop.getNow_bitset().clone());
                            clone.and(set1);
                            subRoute.setNow_bitset(clone);
                        } else {
                            subRoute.setNow_bitset(new BitSet());
                        }

                    }
                    subRoute.setCurrent_end_vertex_id(edge.getEndVertex().getId());
                    subRoute.setEdgeid(edge.getId());
                    if (subRoute.getNow_bitset().cardinality() > 0)
                        //TODO 剪枝操作
                        if (!prun(subRoute, trajectory))
                            stack.push(subRoute);
                }
            }
        });
    }

    public static void main(String[] args) {
        int[] alphas = new int[]{0};

        for (int k = 0; k < alphas.length; k++) {
            int alpha = alphas[k];
            String pathname = "/home/zhujie/lvzhongjian/search/routetree/";
            File dir = new File(pathname);
            String[] list = dir.list();
            SimilarSearchByBitmap similarSearch = new SimilarSearchByBitmap();
            similarSearch.setMIN_ED_DISTANCE(alpha);
            for (int i = 0; i < list.length; i++) {
                String filename = list[i];
                System.out.println("filename is " + filename);
                if (!filename.endsWith("xml"))
                    continue;
                RouteTrie routeTrie = RouteTrie.readFromXml(pathname + filename);

                Set<TrieNode> triNodesOfDesitination = routeTrie.getTriNodesOfDesitination(routeTrie.d_id());
                System.out.println("destination id is " + routeTrie.d_id());
                java.util.Iterator<TrieNode> iterator1 = triNodesOfDesitination.iterator();
                BitMap bitMap = new BitMap(BitMap.PARTIAL, "/home/zhujie/lvzhongjian/bitmap_100");
                File direc = new File(pathname + alpha + "/");
                direc.mkdir();
                File file = new File(direc, filename + ".search.result");
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
                    long end_vertex_id = RoutesService.getRoadNet().getEdge_map().get(edgeids.get(edgeids.size() - 1)).getEndVertex().getId();
                    similarSearch.setDefinition_id(end_vertex_id);
                    System.out.println(String.format("the %d traj:", route_id));
//            long start = RoutesService.getRoadNet().getEdge_map().get(edgeids.get(0)).getStartVertex().getId();
//            System.out.println("startid:"+start);
//            BitSet bs = (BitSet) bitMap.get(start).clone();
                    for (int j = 0; j < edgeids.size(); j++) {
                        System.out.print(edgeids.get(j) + ",");
                    }
                    System.out.println();
//            System.out.println(bs.cardinality()+":"+bs);
                    long l = System.currentTimeMillis();
//                    double score = similarSearch.score(trajectory);
                    double[] doubles = similarSearch.scoreAndUseTime(trajectory);
                    long time = System.currentTimeMillis() - l;
                    System.out.println("last score is " + doubles[0]);
                    try {
                        bw.write("the " + route_id + ", search score is " + doubles[0] + " , use time " + doubles[1]+", contain bitmap time "+time);
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
}
