package tool.cluster.detection;

import model.Route;

/**
 * Created by lzj on 2016/7/26.
 */
public interface IOutlierDetection {
    boolean isOutlier(Route detectionRoute);
}
