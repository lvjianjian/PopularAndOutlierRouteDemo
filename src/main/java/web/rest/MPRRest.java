package web.rest;

import model.Node;
import model.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import service.cmpl.GetKMPR;
import tool.cmpl.SaveSample;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JQ-Cao on 2016/6/12.
 */
@RestController
@RequestMapping(value = "/rest/lbs")
public class MPRRest {
    @Autowired
    private GetKMPR getKMPR;
    @RequestMapping(value = "/findPath.json",method = RequestMethod.POST)
    public List<List<Node>> getMPR(HttpServletRequest request){
        long disNo = Long.valueOf(request.getParameter("disNo").trim());
        long startNo = Long.valueOf(request.getParameter("startNo").trim());
        boolean topKSwitch = "On".equals(request.getParameter("topKSwitch"));
        List<List<Node>> result = new ArrayList<>();
        for (List<Vertex> vertexes : getKMPR.getKMPRByYen(startNo, disNo,topKSwitch)) {
            List<Node> nodes = new ArrayList<>();
            for(Vertex vertex:vertexes){
                nodes.add(new Node(vertex.getId(),vertex.getLatitude(),vertex.getLongitude()));
            }
            result.add(nodes);
        }
        return result;
    }

    @RequestMapping(value = "/getRegionInf.json",method = RequestMethod.POST)
    public List<List<Node>> getRegionInf(HttpServletRequest request){
        String[] startRegion = request.getParameter("regionStart").split(",");
        String[] endRegion = request.getParameter("regionEnd").split(",");
        double startLongitude = Double.valueOf(startRegion[0]);
        double startLatitude = Double.valueOf(startRegion[1]);
        double endLongitude = Double.valueOf(endRegion[0]);
        double endLatitude = Double.valueOf(endRegion[1]);
        return getKMPR.getRegionInfService(startLongitude,startLatitude,endLongitude,endLatitude);
    }

    @RequestMapping(value = "/cacheAsSample.json",method = RequestMethod.POST)
    public boolean cacheAsSample(HttpServletRequest request){
        long disNo = Long.valueOf(request.getParameter("disNo").trim());
        long startNo = Long.valueOf(request.getParameter("startNo").trim());
        return SaveSample.saveSample(startNo, disNo);
    }

    @RequestMapping(value = "/getPairForTable.json",method = RequestMethod.POST)
    public List<Long> getPairForTable(HttpServletRequest request){
        return GetKMPR.getSampleList();
    }


}
