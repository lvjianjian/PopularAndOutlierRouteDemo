package tool.cmpl;

import model.RoadNet;
import model.Vertex;
import tool.*;
import tool.Properties;


import java.util.*;

/**
 * Created by JQ-Cao on 2016/6/8.
 */
public class Dijkstra {
    public static List<Vertex> Dijkstra(long startNo, long disNo, RoadNet roadNet) {
        Comparator<Vertex> OrderIsdn = new Comparator<Vertex>() {
            public int compare(Vertex o1, Vertex o2) {
                // TODO Auto-generated method stub
                double number_a = o1.getCostForDis();
                double number_b = o2.getCostForDis();
                if (number_b > number_a) {
                    return 1;
                } else if (number_b < number_a) {
                    return -1;
                } else {
                    return 0;
                }

            }
        };
        Queue<Vertex> priorityQueue = new PriorityQueue<Vertex>(tool.Properties.size, OrderIsdn);

        List<Vertex> visitedVertexAsBridge = new ArrayList<>();
        Set<Vertex> visitedVertex = new HashSet<>();
        roadNet.getVertex_map().get(startNo).setCostForDis(1.0);
        priorityQueue.add(roadNet.getVertex_map().get(startNo));
        while (!priorityQueue.isEmpty()) {
            Vertex tempVertex = priorityQueue.poll();
            visitedVertex.add(tempVertex);
            if (tempVertex.getId() == disNo) {
                break;
            } else {
                if(!visitedVertexAsBridge.contains(tempVertex)){
                    visitedVertexAsBridge.add(tempVertex);
                    //to repair some edge without taxi pass
                    for(Vertex vertex:tempVertex.getNextVertices()){
                        if(!visitedVertexAsBridge.contains(vertex)){
                            if(vertex.getCost()==tool.Properties.defaultMin){
                                System.out.print(vertex.getId()+":"+"Old:" + Properties.defaultMin + "\t");
                                double max = Properties.defaultMin;
                                double min = Properties.defaultMax;
                                for(Vertex nextVertex:vertex.getNextVertices()){
                                    if(max<nextVertex.getCost()){
                                        max = nextVertex.getCost();
                                    }
                                    if(min>nextVertex.getCost()){
                                        min = nextVertex.getCost();
                                    }
                                }
                                if(tempVertex.getCost()<(min+max)/2){
                                    vertex.setCost(tempVertex.getCost()/2);
                                }else{
                                    vertex.setCost((min+max)/4);
                                }
                                System.out.println("new:" +vertex.getCost() + "\t");
                            }
                            if(vertex.getCostForDis()< tempVertex.getCostForDis()*vertex.getCost()){
                                vertex.setCostForDis(tempVertex.getCostForDis() * vertex.getCost());
                                vertex.setParent(tempVertex);
                                visitedVertex.add(vertex);
                                priorityQueue.add(vertex);
                            }
                        }
                    }
                }
            }
        }


        List<Vertex> path = new ArrayList<>();
        double gailv = 1.0;
        Vertex temp = roadNet.getVertex_map().get(disNo);
        while (temp != null && temp.getParent() != null && temp.getId() != startNo) {
            path.add(temp);
            System.out.print("\t" + temp.getId() + ":" + temp.getCost());
            gailv *= temp.getCost();
            temp = temp.getParent();
        }
        path.add(temp);
        visitedVertex.forEach(x -> {
            x.setCostForDis(0.0);
            x.setParent(null);
        });
        if(path.size()>2){
            Collections.reverse(path);
        }else {
            path.clear();
        }
        System.out.print("\n");
        System.out.println(gailv);
        return path;
    }
}
