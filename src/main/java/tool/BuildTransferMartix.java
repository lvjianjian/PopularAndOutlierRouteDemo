package tool;

import com.ada.routecount.main.model.Route;
import com.ada.routecount.main.model.RouteTrie;
import com.ada.routecount.main.model.TrieNode;
import model.Edge;
import model.TransferMatrix;
import model.TransferMatrixNode;
import scala.collection.JavaConversions;
import scala.collection.immutable.List;
import service.cmpl.RoutesService;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by lzj on 2016/8/12.
 * 从routetire（odpair）中构造转移矩阵
 */
public class BuildTransferMartix {
    public static TransferMatrix build(RouteTrie routeTrie) {
        if (routeTrie == null) {
            return null;
        }
        TransferMatrix matrix = new TransferMatrix(routeTrie.d_id());
        Set<TrieNode> triNodesOfDesitination = routeTrie.getTriNodesOfDesitination(routeTrie.d_id());
        Map<Long, Edge> edge_map = RoutesService.getRoadNet().getEdge_map();
        triNodesOfDesitination.forEach(new Consumer<TrieNode>() {
            @Override
            public void accept(TrieNode trieNode) {
                List<Object> objectList = trieNode.vertex_ids();//这个vertexids是edgeids
                java.util.List<Object> objects = JavaConversions.seqAsJavaList(objectList);
                long count = trieNode.freq();
                for (int i = 0; i < objects.size(); i++) {
                    Object edgeid = objects.get(i);
                    Edge edge = edge_map.get(edgeid);
                    matrix.addNodeInfo(edge.getStartVertex().getId(), edge.getEndVertex().getId(), edge.getId(), count);
                }
            }
        });
        return matrix;
    }


    /**
     * @param filename transfermatrix_{did}
     * @return
     */
    public static TransferMatrix buildFromFile(String filename) {
        if (filename == null) {
            return null;
        }
        String[] split = filename.split("\\\\");
        String[] split1 = split[split.length - 1].split("_");
        long did = new Long(split1[split1.length - 1]);
        TransferMatrix matrix = new TransferMatrix(did);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] split2 = line.split(",");
                matrix.addNodeInfo(new Long(split2[0]),new Long(split2[1]),new Long(split2[2]),new Integer(split2[3]));
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
            matrix = null;
        }finally {
            if(br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return matrix;

    }

    public static void main(String[] args) {
//        RouteTrie routeTrie = RouteTrie.readFromXml("D:\\lzj_spark_test\\result\\ibat_100_256_5\\60560416648_59567301461.xml");
//        TransferMatrix matrix = build(routeTrie);
//        System.out.println(matrix);

        TransferMatrix matrix2 = buildFromFile("D:\\lzj_spark_test\\result\\transfermatrix_59565219109");
        System.out.println(matrix2);
    }

}
