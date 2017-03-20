package tool;

import com.ada.routecount.main.model.RouteTrie;
import com.ada.routecount.main.model.TrieNode;
import model.*;
import scala.collection.Iterator;
import service.cmpl.RoutesService;
import tool.cluster.distancefunction.EditDistanceWithWeightFunction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by lzj on 2016/8/12.
 */
public class SimilarSearchByMatrix extends Search {



    private TransferMatrix matrix;
    public TransferMatrix getMatrix() {
        return matrix;
    }
    public void setMatrix(TransferMatrix matrix) {
        this.matrix = matrix;
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
    protected boolean canSearch() {
        if (matrix == null)
            return false;
        return true;
    }

    @Override
    public double[] scoreAndUseTime(Trajectory trajectory) {
        double[] st = new double[2];
        long time1 = System.currentTimeMillis();
        double score = score(trajectory);
        st[1] = (System.currentTimeMillis() - time1) ;
        st[0] = score;
        return st;
    }


    @Override
    protected double getScoreOfOneRoute(SubRoute r) {
        return r.getScore();
    }

    @Override
    protected void addSubRoutes(long current_end_vertex_id, final LinkedList<SubRoute> stack, SubRoute pop, Trajectory trajectory) {
        final long[] all_count = {0};
        Map<Long, TransferMatrixNode> nodeInfosFromStartVertex = matrix.getNodeInfosFromStartVertex(current_end_vertex_id);
        if (nodeInfosFromStartVertex == null)
            return;
        nodeInfosFromStartVertex.values().forEach(new Consumer<TransferMatrixNode>() {
            @Override
            public void accept(TransferMatrixNode transferMatrixNode) {
                all_count[0] += transferMatrixNode.getCount();
            }
        });
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

                TransferMatrixNode set1 = nodeInfosFromStartVertex.get(edge.getEndVertex().getId());
                if (set1 != null) {
                    if (pop == null) {//第一条边
                        subRoute.setScore((double)set1.getCount()/all_count[0]);
                    } else {
                        if (isNewVertex(pop, edge.getEndVertex().getId())) {//确保是一个新的点
                            subRoute.setScore(pop.getScore()*set1.getCount()/all_count[0]);
                        } else {
                            subRoute.setScore(0);
                        }
                    }
                    subRoute.setCurrent_end_vertex_id(edge.getEndVertex().getId());
                    subRoute.setEdgeid(edge.getId());
                    if (subRoute.getScore() > 0)
                        if (!prun(subRoute, trajectory)) {
//                            System.out.println("add subroute:" + subRoute.toString() +","+subRoute.getScore()+","+subRoute.getCurrent_end_vertex_id());
                            stack.push(subRoute);
                        }
                }
            }
        });
    }


    public static void main(String[] args) {
        int[] alphas = new int[]{0,10,15,20,25,30,35};

        for (int k = 0; k < alphas.length; k++) {
            int alpha = alphas[k];
            String pathname = "/home/zhujie/lvzhongjian/search/routetree/";
//            String pathname = "E:\\ZhongjianLv\\project\\PopularRouteDemo\\trie\\";
            File dir = new File(pathname);
            String[] list = dir.list();
            SimilarSearchByMatrix similarSearch = new SimilarSearchByMatrix();
            similarSearch.setMIN_ED_DISTANCE(alpha);
            for (int i = 0; i < list.length; i++) {
                String filename = list[i];
                System.out.println("filename is " + filename);
                if (!filename.endsWith("xml"))
                    continue;
                RouteTrie routeTrie = RouteTrie.readFromXml(pathname + filename);

                Set<TrieNode> triNodesOfDesitination = routeTrie.getTriNodesOfDesitination(routeTrie.d_id());
                System.out.println("destination id is " + routeTrie.d_id());
                similarSearch.setMatrix(TransferMatrix.load(routeTrie.d_id()));
                similarSearch.setDefinition_id(routeTrie.d_id());
                java.util.Iterator<TrieNode> iterator1 = triNodesOfDesitination.iterator();
                File direc = new File(pathname + "matrix_"+alpha + "/");
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
                    System.out.println(String.format("the %d traj:", route_id));
//                  long start = RoutesService.getRoadNet().getEdge_map().get(edgeids.get(0)).getStartVertex().getId();
//                  System.out.println("startid:"+start);
//                   BitSet bs = (BitSet) bitMap.get(start).clone();
                    for (int j = 0; j < edgeids.size(); j++) {
                        System.out.print(edgeids.get(j) + ",");
                    }
                    System.out.println();
//                  System.out.println(bs.cardinality()+":"+bs);
//                  double score = similarSearch.score(trajectory);
                    double[] doubles = similarSearch.scoreAndUseTime(trajectory);
                    System.out.println("last score is " + doubles[0]);
                    try {
                        bw.write("the " + route_id + ", search score is " + doubles[0] + " , use time " + doubles[1]);
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
