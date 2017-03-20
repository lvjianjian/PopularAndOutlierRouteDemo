package tool.itfc;

import model.RoadNet;
import model.Vertex;
import java.util.List;

/**
 * Created by JQ-Cao on 2016/6/7.
 */
public interface IKPath {
    List<List<Vertex>> getKPath(RoadNet roadNet,long startID_vertex,long endID_vertex,int k,double similarity);
}
