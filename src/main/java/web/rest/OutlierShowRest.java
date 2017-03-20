package web.rest;

import model.ODPair;
import model.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import service.cmpl.RoutesService;
import tool.Properties;
import tool.cluster.model.DensityBasedReferenceRoutes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhongjian on 2017/3/9.
 */

@RestController
@RequestMapping(value = "/rest/outliershow")
public class OutlierShowRest {


    @Autowired
    private RoutesService routesService;

//    @RequestMapping(value = "/test.json", method = RequestMethod.GET)
//    public boolean test(){
//        return true;
//    }

    @RequestMapping(value = "/getTrajForTable.json", method = RequestMethod.POST)
    @ResponseBody
    public List<String[]> getTrajForTable(HttpServletRequest request) {
        System.out.println("getTrajForTable");
        List<String[]> trajInOutlierShowWithNumberAndOdpair = RoutesService.getTrajInOutlierShowWithNumberAndOdpair();
        System.out.println(trajInOutlierShowWithNumberAndOdpair.size());
        return trajInOutlierShowWithNumberAndOdpair;
    }


    /**
     * 检查{numberID} 这条轨迹是否outlier
     *
     * @param numberID
     * @return
     */
    @RequestMapping(value = "/checkOutlier.json", method = RequestMethod.POST)
    public boolean checkOutlier(int numberID, long sid, long eid) {
        System.out.println("check route :" + numberID);
        Route route = RoutesService.getTrajInOutlierShow().get(numberID);
        String path = Properties.referencesPath + String.format("%d_%d_referenceroutes.txt", sid, eid);
        System.out.println(path);
        DensityBasedReferenceRoutes load = DensityBasedReferenceRoutes.load(path);
        System.out.println(load.getRoutes().size());
        return load.isOutlier(route);
    }


    /**
     * @param numberID
     * @return
     */
    @RequestMapping(value = "/getRouteBynumberID.json", method = RequestMethod.POST)
    public Route getRoutes(int numberID) {
        return RoutesService.getTrajInOutlierShow().get(numberID);
    }


    @RequestMapping(value = "/getRepresentativeRoute.json", method = RequestMethod.POST)
    public Route getRepresentativeRoute(int numberID, long sid, long eid) {
        System.out.println("getRepresentativeRoute about :" + numberID);
        Route route = RoutesService.getTrajInOutlierShow().get(numberID);
        DensityBasedReferenceRoutes load = DensityBasedReferenceRoutes.load(Properties.referencesPath + String.format("%d_%d_referenceroutes.txt", sid, eid));
        System.out.println(load.getRoutes().size());
        System.out.println(load.isOutlier(route));
        System.out.println(load.getRepresentativeRoute());
        return load.getRepresentativeRoute();
    }

}
