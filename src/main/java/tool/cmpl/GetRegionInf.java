package tool.cmpl;

import model.RoadNet;
import model.Vertex;
import tool.itfc.IGetRegionInf;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by JQ-Cao on 2016/6/14.
 */
public class GetRegionInf implements IGetRegionInf {
    @Override
    public Set<Vertex> getRegion(double startLongitude, double startLatitude, double endLongitude, double endLatitude, RoadNet roadNet) {
        Set<Vertex> inRegion = new HashSet<>();
        for(Vertex vertex:roadNet.getVertex_map().values()){
            if(inRange(vertex.getLatitude(),startLatitude,endLatitude)&&inRange(vertex.getLongitude(),startLongitude,endLongitude)){
                inRegion.add(vertex);
            }
        }
        return inRegion;
    }



    private boolean inRange(double testValue,double rangeA,double rangeB){
        return (testValue<rangeB&&testValue>rangeA)||(testValue<rangeA&&testValue>rangeB);
    }
}
