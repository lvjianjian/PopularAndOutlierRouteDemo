package service.itfc;
import model.Node;

import java.util.List;

/**
 * Created by JQ-Cao on 2016/6/16.
 */
public interface IGetRegionInfService {
   List<List<Node>> getRegionInfService(double startLongitude,double startLatitude,double endLongitude,double endLatitude);
}
