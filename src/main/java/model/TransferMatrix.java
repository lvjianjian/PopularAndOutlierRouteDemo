package model;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by lzj on 2016/8/12.
 */
public class TransferMatrix {
    private static final String path = "/home/zhujie/lvzhongjian/transfer_matrix";
//    private static final String path = "E:\\ZhongjianLv\\project\\PopularRouteDemo\\transfer_matrix";
    private static final String prefixFileName = "transfermatrix_";
    private long deftination_id;
    private Map<Long,Map<Long,TransferMatrixNode>> matrix = new HashMap<>();


    public TransferMatrix(long deftination_id) {
        this.deftination_id = deftination_id;
    }


    public long getDeftination_id() {
        return deftination_id;
    }

    public void setDeftination_id(long deftination_id) {
        this.deftination_id = deftination_id;
    }

    /**
     * 加边
     * @param start_vertex_id 边的起始id
     * @param end_vertex_id 边的结束id
     * @param edge_id 边id
     * @param count 个数
     */
    public void addNodeInfo(long start_vertex_id,long end_vertex_id,long edge_id,long count){
        Map<Long, TransferMatrixNode> transferMatrixNodes = matrix.get(start_vertex_id);
        if(transferMatrixNodes == null) {
            transferMatrixNodes = new HashMap<>();
            matrix.put(start_vertex_id,transferMatrixNodes);
        }
        TransferMatrixNode transferMatrixNode = transferMatrixNodes.get(end_vertex_id);
        if(transferMatrixNode == null) {
            transferMatrixNode = new TransferMatrixNode(end_vertex_id, edge_id, count);
            transferMatrixNodes.put(end_vertex_id,transferMatrixNode);
        }else{
            transferMatrixNode.setCount(transferMatrixNode.getCount() + count);
        }
    }


    /**
     * 得到从起点开始的所有结点（边）信息
     * @param start_vertex_id
     * @return 不存在返回null，存在返回Map<Long, TransferMatrixNode>
     */
    public Map<Long, TransferMatrixNode> getNodeInfosFromStartVertex(long start_vertex_id){
        return matrix.get(start_vertex_id);
    }



    /**
     * 获取起点到终点的结点（边）信息
     * @param start_vertex_id
     * @param end_vertex_id
     * @return 不存在返回null，存在返回TransferMatrixNode
     */
    public TransferMatrixNode getNodeInfo(long start_vertex_id, long end_vertex_id){
        Map<Long, TransferMatrixNode> longTransferMatrixNodeMap = getNodeInfosFromStartVertex(start_vertex_id);
        if(longTransferMatrixNodeMap == null)
            return null;
        TransferMatrixNode transferMatrixNode = longTransferMatrixNodeMap.get(end_vertex_id);
        return transferMatrixNode;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        System.out.println(deftination_id);
        matrix.forEach(new BiConsumer<Long, Map<Long, TransferMatrixNode>>() {
            @Override
            public void accept(Long aLong, Map<Long, TransferMatrixNode> longTransferMatrixNodeMap) {
                sb.append("vertexid:"+aLong+"\n");
                longTransferMatrixNodeMap.forEach(new BiConsumer<Long, TransferMatrixNode>() {
                    @Override
                    public void accept(Long aLong, TransferMatrixNode transferMatrixNode) {
                        sb.append("======>to vertexid:"+transferMatrixNode.getEnd_vertex_id()+",edgeid:"+transferMatrixNode.getEdge_id()+"\n");
                    }
                });
            }
        });
        return sb.toString();
    }


    /**
     *
     * 载入transferMatrix,针对spark导出的数据
     * @param did 目的点id
     * @return
     */
    public static TransferMatrix load(long did){
        File f = new File(path+"/"+prefixFileName+did+"/part-00000");
        TransferMatrix transferMatrix = new TransferMatrix(did);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));

            String s1 = null;
            while ((s1 = reader.readLine()) != null){
                String[] split = s1.split(",");
                transferMatrix.addNodeInfo(new Long(split[0]),new Long(split[1]), new Long(split[2]), new Long(split[3]));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transferMatrix;
    }
}
