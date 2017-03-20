package tool.cluster.model;

import model.Route;
import tool.cluster.detection.IOutlierDetection;
import tool.cluster.distancefunction.EditDistanceWithWeightFunction;
import tool.cluster.distancefunction.IDistanceFunction;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhongjian on 2017/3/9.
 */
public class DensityBasedReferenceRoutes implements IOutlierDetection {
    private List<Route> routes;
    private long startid;
    private long endid;
    IDistanceFunction distanceFunction = new EditDistanceWithWeightFunction(null);;
    private int alpha = 25;
    private Route representativeRoute = null;

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public IDistanceFunction getDistanceFunction() {
        return distanceFunction;
    }

    public void setDistanceFunction(IDistanceFunction distanceFunction) {
        this.distanceFunction = distanceFunction;
    }

    public DensityBasedReferenceRoutes() {
    }

    public DensityBasedReferenceRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public DensityBasedReferenceRoutes(long startid, long endid) {
        this.startid = startid;
        this.endid = endid;
    }


    public List<Route> getRoutes() {
        return routes;
    }

    public long getStartid() {
        return startid;
    }

    public void setStartid(long startid) {
        this.startid = startid;
    }

    public long getEndid() {
        return endid;
    }

    public void setEndid(long endid) {
        this.endid = endid;
    }

    public void addReferenceRoute(Route route) {
        if (routes == null) {
            routes = new LinkedList<>();
        }
        routes.add(route);
    }


    public void save(String path) {
        File f = new File(path);
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(f));
            for (Route r : routes){
                List<Long> edge_ids = r.getEdge_ids();
                bufferedWriter.write(edge_ids.toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
            if(bufferedWriter!=null){
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DensityBasedReferenceRoutes load(String path) {
        Pattern compile = Pattern.compile("(\\d*)_(\\d*)_referenceroutes.txt");
        Matcher matcher = compile.matcher(path);
        if(!matcher.find())
            return null;
        long sid = Long.valueOf(matcher.group(1));
        long eid = Long.valueOf(matcher.group(2));
        DensityBasedReferenceRoutes densityBasedReferenceRoutes = new DensityBasedReferenceRoutes(sid,eid);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            String s = null;
            while((s = bufferedReader.readLine())!=null){
                s = s.replace("[", "");
                s = s.replace("]","");
                String[] split = s.split(",");
                Route route = new Route(0, 0);
                LinkedList<Long> edge_ids = new LinkedList<>();
                for (int i = 0; i < split.length; i++) {
                    edge_ids.add(Long.valueOf(split[i].trim()));
                }
                route.setEdge_ids(edge_ids);
                densityBasedReferenceRoutes.addReferenceRoute(route);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return densityBasedReferenceRoutes;
    }

    @Override
    public boolean isOutlier(Route detectionRoute) {
        for (int i = 0; i < getRoutes().size(); i++) {
            Route route = getRoutes().get(i);
            if (distanceFunction.distance(detectionRoute, route) < alpha) {
                representativeRoute = route;
                return false;
            }
        }
        representativeRoute = getRoutes().get(0);
        return true;
    }


    public Route getRepresentativeRoute() {
        return representativeRoute;
    }

    public static void main(String[] args) {
        DensityBasedReferenceRoutes load = load("referenceRoutes\\59565313370_59565219109_referenceroutes.txt");
        System.out.println(load.getRoutes().size());
        System.out.println(load.getRoutes().get(0));
        boolean outlier = load.isOutlier(load.getRoutes().get(2));
        System.out.println(outlier);
    }
}
