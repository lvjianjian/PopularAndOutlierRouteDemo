package web.rest;

import model.Node;
import model.RouteOfZj;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lzj on 2016/7/27.
 */
@RestController
@RequestMapping("/rest/routesOfZJ")
public class RoutesOfZJRest {


    @RequestMapping(value = "/getAllTrajs.json", method = RequestMethod.POST)
    @ResponseBody
    public List<RouteOfZj> getAllRoute() {
        List<RouteOfZj> routes = new LinkedList<>();
        File file = new File("D:\\吕中剑\\OutlierDetection\\data\\wise2015_data\\lzj\\data_vid_gd_9470_10290.txt");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(",");
                RouteOfZj r = new RouteOfZj(new Long(split[0]));

                List<Long> vertexids = new LinkedList<>();
                for (int i = 0; i < new Integer(split[1]); i++) {
                    vertexids.add(new Long(split[i + 2]));
                }
                r.setVertex_ids(vertexids);
//                System.out.println(r.getNodes().get(0));
                routes.add(r);
            }
//            System.out.println(routes.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("return routes and routes size is " + routes.size());
        return routes;
    }


    @RequestMapping(value = "/loadZhujieRoute.json", method = RequestMethod.POST)
    @Deprecated
    public List<List<List<Node>>> load_test() {

        return null;
    }



}
