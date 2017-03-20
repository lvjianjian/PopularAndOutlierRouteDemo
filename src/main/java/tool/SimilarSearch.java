package tool;

import com.ada.routecount.main.model.RouteTrie;
import com.ada.routecount.main.model.TrieNode;
import model.*;
import scala.collection.Iterator;
import service.cmpl.RoutesService;
import tool.cluster.distancefunction.EditDistanceWithWeightFunction;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by lzj on 2016/8/12.
 */
public class SimilarSearch extends Search{

    private TransferMatrix matrix;
    private EditDistanceWithWeightFunction distanceWithWeightFunction = new EditDistanceWithWeightFunction(null);
    private int MIN_ED_DISTANCE = 10;

    public TransferMatrix getMatrix() {
        return matrix;
    }

    public void setMatrix(TransferMatrix matrix) {
        this.matrix = matrix;
    }

    @Override
    protected boolean canSearch() {
        if(matrix == null)
            return false;
        return true;
    }

    @Override
    protected long getDeftination_id() {
        return matrix.getDeftination_id();
    }

    @Override
    public double[] scoreAndUseTime(Trajectory trajectory) {
        return new double[0];
    }


    @Override
    protected double getScoreOfOneRoute(SubRoute r) {
        return r.getScore();
    }

    @Override
    protected void addSubRoutes(long start_vertex_id, final LinkedList<SubRoute> stack, SubRoute old_subroute, Trajectory trajectory) {
        final long[] all_count = {0};

        Map<Long, TransferMatrixNode> nodeInfosFromStartVertex = matrix.getNodeInfosFromStartVertex(start_vertex_id);
        if (nodeInfosFromStartVertex == null)
            return;
        nodeInfosFromStartVertex.values().forEach(new Consumer<TransferMatrixNode>() {
            @Override
            public void accept(TransferMatrixNode transferMatrixNode) {
                all_count[0] += transferMatrixNode.getCount();
            }
        });
        nodeInfosFromStartVertex.values().forEach(new Consumer<TransferMatrixNode>() {
            @Override
            public void accept(TransferMatrixNode transferMatrixNode) {
                SubRoute route = null;
                route = new SubRoute();
                route.setPrivous(old_subroute);
                if(old_subroute ==null)
                    route.setScore((transferMatrixNode.getCount() / (double) all_count[0]));
                else
                    route.setScore(old_subroute.getScore() * (transferMatrixNode.getCount() / (double) all_count[0]));
                route.setCurrent_end_vertex_id(transferMatrixNode.getEnd_vertex_id());
                route.setEdgeid(transferMatrixNode.getEdge_id());
                //TODO 剪枝操作
                if (!prun(route, trajectory))
                    stack.push(route);
            }
        });
    }



    public static void main(String[] args) {
        RouteTrie routeTrie = RouteTrie.readFromXml("D:\\lzj_spark_test\\result\\ibat_100_250_5\\60560416648_59567301461.xml");
        TransferMatrix matrix = BuildTransferMartix.build(routeTrie);
        TransferMatrix matrix2 = BuildTransferMartix.buildFromFile("D:\\lzj_spark_test\\result\\transfermatrix_59567301461");
        SimilarSearch similarSearch = new SimilarSearch();
        similarSearch.setMatrix(matrix);
        SimilarSearch similarSearch2 = new SimilarSearch();
        similarSearch2.setMatrix(matrix2);
        Trajectory trajectory = new Trajectory();
        Set<TrieNode> triNodesOfDesitination = routeTrie.getTriNodesOfDesitination(routeTrie.d_id());
        java.util.Iterator<TrieNode> iterator1 = triNodesOfDesitination.iterator();
        TrieNode next = iterator1.next();
        scala.collection.immutable.List<Object> objectList = next.vertex_ids();
        List<Long> edgeids = new LinkedList<>();
        Iterator<Object> iterator = objectList.iterator();
        while (iterator.hasNext()) {
            Long next1 = (Long) iterator.next();
            edgeids.add(next1);
        }
        trajectory.setEdge_ids(edgeids);
        double score = similarSearch.score(trajectory);
        System.out.println(score);
        double score2 = similarSearch2.score(trajectory);
        System.out.println(score2);
    }
}
