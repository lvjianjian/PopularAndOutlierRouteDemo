package tool.itfc;

import model.RoadNet;
import model.Vertex;

import java.util.Set;

/**
 * Created by JQ-Cao on 2016/6/14.
 */
public interface IGetRegionInf {
    Set<Vertex> getRegion(double startLongitude,double startLatitude,double endLongitude,double endLatitude, RoadNet roadNet);
}
