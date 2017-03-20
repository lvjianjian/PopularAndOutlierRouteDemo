package tool.cmpl;

import model.RoadNet;
import model.Vertex;
import tool.itfc.IKPath;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JQ-Cao on 2016/6/7.
 */
public class Our implements IKPath{
    @Override
    public List<List<Vertex>> getKPath(RoadNet roadNet, long startID_vertex, long endID_vertex, int k, double similarity) {
        List<List<Vertex>> A = new ArrayList<>();
        List<List<Vertex>> B = new ArrayList<>();

        A.add(Dijkstra.Dijkstra(startID_vertex, endID_vertex, roadNet));
        if (A.isEmpty()) {
            return null;
        }
        for(int j=0;j<k-1;++j){
            if(j<=A.size()){
                for(int i=0;i<A.get(j).size()-1;++i){

                }
            }
        }
        return A;
    }
}
