package tool.cluster.distancefunction.constraint;

/**
 * Created by lzj on 2016/7/26.
 * constraint of similar
 */
public interface IConstraint {

    /**
     *
     * @param r1_index
     * @param r2_index
     * @return if satisfy constraint return true else false
     */
    boolean similar(int r1_index,int r2_index);

    /**
     * do some clean
     */
    void clean();
}
