package service.cmpl;

import model.Edge;
import model.Node;
import model.RoadNet;
import model.Vertex;
import org.springframework.stereotype.Service;
import service.itfc.IGetKMPR;
import service.itfc.IGetRegionInfService;
import tool.Properties;
import tool.cmpl.GetRegionInf;
import tool.cmpl.LoadSource;
import tool.cmpl.Yen;
import tool.itfc.IGetRegionInf;

import java.util.*;

/**
 * Created by JQ-Cao on 2016/6/12.
 */
@Service
public class GetKMPR implements IGetKMPR,IGetRegionInfService {

    public GetKMPR(){}

    private static LoadSource loadSource = new LoadSource();
    private static Set<Vertex> vertexSet = loadSource.loadVertex(Properties.vertexFile);
    private static Set<Edge> edgeSet = loadSource.loadEdge(Properties.edgeFile,vertexSet);
    private static RoadNet roadNet = new RoadNet(edgeSet,vertexSet);
    private static Map<Integer,Long> map = loadSource.loadGaoDeMap(Properties.gaodeApiMap);
    private static Set<Long> fileNames = loadSource.getFilenames(Properties.libraryPath);
    private static List<Long> sampleList = loadSource.getSample(Properties.samplePath);

    @Override
    public List<List<Vertex>> getKMPRByYen(long startId, long disId,boolean topKSwitch) {
        setRoadNetByDis(disId);
        if(startId<500000){
            startId = map.get((int)startId);
        }
        if(disId<500000){
            disId = map.get((int)disId);
        }
        if(!topKSwitch){
            return new Yen().getKPath(roadNet, startId, disId, 1, Properties.similarity);
        }else{
            return new Yen().getKPath(roadNet, startId, disId, Properties.KPath, Properties.similarity);
        }
    }

    @Override
    public List<List<Vertex>> getKMPRByOur(long startId, long disId) {
        setRoadNetByDis(disId);
        return null;
    }

    private void setRoadNetByDis(long disId){
        //TODO
        loadSource.updateVertex(vertexSet,loadSource.loadV_map(Properties.VFile+disId+".txt", loadSource.loadCorr_map(Properties.mapFile)));
    }

    @Override
    public List<List<Node>> getRegionInfService(double startLongitude, double startLatitude, double endLongitude, double endLatitude) {
        Set<Vertex> vertexes = new GetRegionInf().getRegion(startLongitude,startLatitude,endLongitude,endLatitude,roadNet);
        List<List<Node>> result = new ArrayList<>();
        for(Vertex vertex:vertexes){
            List<Node> tempList = new ArrayList<>();
            Node node = new Node(vertex.getId(),vertex.getLatitude(),vertex.getLongitude());
            if(fileNames.contains(vertex.getId())){
                node.setStatus(true);
            }
            tempList.add(node);
            vertex.getNextVertices().forEach(x -> tempList.add(new Node(x.getId(),x.getLatitude(),x.getLongitude())));
            result.add(tempList);
        }
        return result;
    }

    public static List<Long> getSampleList() {
        return sampleList;
    }

    public static void setSampleList(List<Long> sampleList) {
        GetKMPR.sampleList = sampleList;
    }


}
