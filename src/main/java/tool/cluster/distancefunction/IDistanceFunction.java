package tool.cluster.distancefunction;

import model.Route;
/**
 * Created by lzj on 2016/7/25.
 */
public interface IDistanceFunction {
    /**
     * calculate distance
     * 针对内部route计算
     * @param r1_index route index of r1
     * @param r2_index route index of r2
     * @return distance between r1 and r2
     */
    double distance(int r1_index, int r2_index);



    /**
     *
     * @param r1
     * @param r2
     * @return
     */
    double distance(Route r1,Route r2);

    /**
     * clean info that generate before or create some datastruct
     */
    void clean();


}
