package service.cmpl;

import model.*;
import org.springframework.stereotype.Service;
import tool.Properties;
import tool.cmpl.LoadSource;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by lzj on 2016/7/15.
 */
@Service
public class RoutesService {

    public RoutesService(){}

    private static LoadSource loadSource = new LoadSource();
    private static Set<Vertex> vertexSet = loadSource.loadVertex(Properties.vertexFile);
    ;
    private static Set<Edge> edgeSet = loadSource.loadEdge(Properties.edgeFile, vertexSet);
    ;

    private static RoadNet roadNet = new RoadNet(edgeSet, vertexSet);

    public static Set<Vertex> getVertexSet() {
        return vertexSet;
    }

    public static Set<Edge> getEdgeSet() {
        return edgeSet;
    }

    //    private static List<ODPair> odPairList = null;
    private static List<ODPair> odPairList = loadSource.loadODPairsFromTrie(Properties.trieFilePath);


    public static List<ODPair> getOdPairList() {
        return odPairList;
    }

    public static RoadNet getRoadNet() {
        return roadNet;
    }

    private static HashMap<Integer, Route> trajInOutlierShow;

    public static HashMap<Integer, Route> getTrajInOutlierShow() {
        Set<Route> ss = new HashSet<>();
        if (trajInOutlierShow == null) {
            trajInOutlierShow = new HashMap<>();
            for (ODPair odpair :
                    getOdPairList()) {
                odpair.getRoutes().forEach(new Consumer<Route>() {
                    @Override
                    public void accept(Route route) {
                        ss.add(route);
                    }
                });
            }
        }
        final int[] i = {1};
        ss.forEach(new Consumer<Route>() {
            @Override
            public void accept(Route route) {
                trajInOutlierShow.put(i[0], route);
                ++i[0];
            }
        });
        return trajInOutlierShow;
    }


    private static List<String[]> outlierShowWithNumberAndOdpair;
    public static List<String[]> getTrajInOutlierShowWithNumberAndOdpair(){
        if(outlierShowWithNumberAndOdpair == null){
            outlierShowWithNumberAndOdpair = new LinkedList<>();
            getTrajInOutlierShow().forEach(new BiConsumer<Integer, Route>() {
                @Override
                public void accept(Integer integer, Route route) {
                    String[] temp = new String[3];
                    temp[0] = String.valueOf(integer);
                    temp[1] = String.valueOf(route.getVertex_ids().get(0));
                    temp[2] = String.valueOf(route.getVertex_ids().get(route.getVertex_ids().size() - 1));
                    outlierShowWithNumberAndOdpair.add(temp);
                }
            });
        }
        return outlierShowWithNumberAndOdpair;
    }

    public static void main(String[] args) {
        HashMap<Integer, Route> trajInOutlierShow = getTrajInOutlierShow();
        System.out.println(trajInOutlierShow.size());
    }
}
