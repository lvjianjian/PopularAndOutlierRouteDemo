package tool.cluster.representativechoose;

import tool.cluster.clusterfunction.IClusterFunction;

/**
 * Created by lzj on 2016/7/26.
 */
public abstract class AbstractReferenceChoose implements IReferenceChoose{
    protected IClusterFunction clusterFunction = null;

    AbstractReferenceChoose(IClusterFunction clusterFunction){
        this.clusterFunction = clusterFunction;
    }



}
