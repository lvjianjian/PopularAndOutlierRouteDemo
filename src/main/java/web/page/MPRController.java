package web.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by JQ-Cao on 2016/6/12.
 */
@Controller
@RequestMapping(value = "/lbs")
public class MPRController {
    @RequestMapping(value = "/mpr.html",method = RequestMethod.GET)
    public String getMPR(HttpServletRequest request){
        return "mpr";
    }

    @RequestMapping(value = "/routes.html",method = RequestMethod.GET)
    public String getRoutes(HttpServletRequest request){
        return "routes";
    }

    @RequestMapping(value = "/routesOfZJ.html",method = RequestMethod.GET)
    public String getRoutesOfZJ(HttpServletRequest request){
        return "zhujie_routes";
    }


    @RequestMapping(value = "OutlierShow.html", method = RequestMethod.GET)
    public String getOutlierShow(HttpServletRequest request){
        System.out.println("getOutlierShow");
        return "outliershow";
    }
}
