package tool.cmpl;

import model.RoadNet;
import model.Vertex;
import tool.Properties;
import tool.itfc.IKPath;


import java.util.*;

/**
 * Created by JQ-Cao on 2016/6/7.
 */
public class Yen implements IKPath {

    public static <T extends Comparable<T>> boolean compare(List<Vertex> a, List<Vertex> b) {
        if (a.size() != b.size())
            return false;
        for (int i = 0; i < a.size(); i++) {
            if (!(a.get(i) == (b.get(i))))
                return false;
        }
        return true;
    }


    public static <T extends Comparable<T>> boolean contains(List<Vertex> a, Vertex b) {
        for (Vertex vertex : a) {
            if (vertex == b) {
                return true;
            }
        }
        return false;
    }

    public static <T extends Comparable<T>> boolean similar(List<Vertex> a, List<Vertex> b, double similar) {
        double i = 0;
        for (Vertex vertex : b) {
            if (contains(a, vertex)) {
                ++i;
            }
        }
        if ((i / a.size()) > similar) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<List<Vertex>> getKPath(RoadNet roadNet, long startID_vertex, long endID_vertex, int k, double similarity) {
        List<Vertex> vertexes_cache = new ArrayList<>();

        List<List<Vertex>> A = new ArrayList<>();
        List<List<Vertex>> B = new ArrayList<>();
        A.add(Dijkstra.Dijkstra(startID_vertex, endID_vertex, roadNet));
        if (A.isEmpty()) {
            return null;
        }
        int j = 0;
        while (A.size() < k&&j<A.size()) {
            j += 1;
            for (int i = 1; i < A.get(j - 1).size() - 1; ++i) {
                Vertex spurNode = A.get(j - 1).get(i - 1);
                List<Vertex> rootPath = A.get(j - 1).subList(0, i);
                for (List<Vertex> p : A) {
                    if (i < p.size() && compare(rootPath, p.subList(0, i))) {
                        if (roadNet.getVertex_map().get(p.get(i - 1).getId()).getNextVertices().remove(p.get(i))) {
                            vertexes_cache.add(p.get(i - 1));
                            vertexes_cache.add(p.get(i));
                        }
                    }
                }
                List<Vertex> totalPath = new ArrayList<>();
                totalPath.addAll(rootPath);
                List<Vertex> pathTemp = Dijkstra.Dijkstra(spurNode.getId(), endID_vertex, roadNet);
                if (pathTemp.size() > 0) {
                    totalPath.addAll(pathTemp);
                    B.add(totalPath);
                }
                for (int m = 0; m < vertexes_cache.size(); m += 2) {
                    vertexes_cache.get(m).getNextVertices().add(vertexes_cache.get(m + 1));
                }
            }
            if (B.isEmpty())
                break;
            B.forEach(x -> {
                boolean judgeSimilarity = true;
                for (List<Vertex> y : A) {
                    if (similar(x, y, Properties.similarity)) {
                        judgeSimilarity = false;
                    }
                }
                if (judgeSimilarity) {
                    A.add(x);
                }
            });
            B.clear();
            Comparator<List<Vertex>> OrderIsdn = new Comparator<List<Vertex>>() {
                @Override
                public int compare(List<Vertex> o1, List<Vertex> o2) {
                    double number_a=1.0;
                    for(Vertex vertex:o1){
                        number_a*=vertex.getCost();
                    }
                    double number_b=1.0;
                    for(Vertex vertex:o2){
                        number_b*=vertex.getCost();
                    }
                    if (number_b > number_a) {
                        return 1;
                    } else if (number_b < number_a) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            };
            A.sort(OrderIsdn);
        }
        if(A.size()>k){
            return A.subList(0,k);
        }else {
            return A;
        }

    }


}
