package service.itfc;

import model.Vertex;

import java.util.List;

/**
 * Created by JQ-Cao on 2016/6/12.
 */
public interface IGetKMPR {
    //
    List<List<Vertex>> getKMPRByYen(long startId,long disId,boolean topKSwitch);
    List<List<Vertex>> getKMPRByOur(long startId,long disId);
}
