package tool.OutlierDection;

import model.Route;
import model.Trajectory;
import tool.cluster.distancefunction.EditDistanceWithWeightFunction;
import tool.cluster.distancefunction.IDistanceFunction;

import java.util.List;

/**
 * Created by lzj on 2016/8/11.
 * 通过暴力比较计算outler分值，分值为与待测轨迹所有相似轨迹的频率之和
 */
public class EasyScore {

    private List<Route> routes;
    private IDistanceFunction distanceFunction;

    public EasyScore(List<Route> routes){
        this.routes = routes;
        distanceFunction = new EditDistanceWithWeightFunction(routes);
    }

    /**
     *
     * @param trajectory 待检测轨迹
     * @param threshold 相似临界值
     * @return
     */
    public double score(Trajectory trajectory,double threshold){
        double score = 0;
        int sum = 0;
        for (int i = 0; i < routes.size(); i++) {
            sum += routes.get(i).getFrequency();
            if(distanceFunction.distance(routes.get(i),trajectory) < threshold){
                score += routes.get(i).getFrequency();
            }
        }
        score = score/sum;
        return score;
    }


    public static void main(String[] args) {

    }
}
