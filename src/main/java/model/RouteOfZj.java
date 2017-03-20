package model;

import service.cmpl.RoutesServiceOfZJ;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by lzj on 2016/7/27.
 */
public class RouteOfZj extends Route {
    public RouteOfZj(long id) {
        super(id,1);
    }


    @Override
    public List<Vertex> getVertexs() {
        if(vertexs == null){
            vertexs = new LinkedList<>();
            Map<Long, Vertex> vertex_map = RoutesServiceOfZJ.getRoadNet().getVertex_map();
            vertex_ids.forEach(new Consumer<Long>() {
                @Override
                public void accept(Long vertex_id) {
                    vertexs.add(vertex_map.get(vertex_id));
                }
            });
        }
        return vertexs;
    }

    @Override
    public double getAllDis() {
        return 0;
    }
}
