package tool.itfc;

import model.Edge;
import model.Vertex;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by JQ-Cao on 2016/6/6.
 */
public interface ILoadSource {

    Map<Long,Integer> loadCorr_map(String corrFile);

    Map<Long,Double> loadV_map(String vFile,Map<Long,Integer> corr_map);

    Set<Vertex> loadVertex(String vertexFile);

    void updateVertex(Set<Vertex> vertices,Map<Long,Double> v_map);

    Set<Edge> loadEdge(String edgeFile,Set<Vertex> vertices);

    Map<Integer,Long> loadGaoDeMap(String fileName);

    Set<Long> getFilenames(String path);

    List<Long> getSample(String fileName);
}
