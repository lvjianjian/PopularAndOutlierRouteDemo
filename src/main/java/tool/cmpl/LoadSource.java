package tool.cmpl;

import com.ada.routecount.main.model.RouteTrie;
import com.ada.routecount.main.model.TrieNode;
import model.*;
import tool.*;
import tool.Properties;
import tool.itfc.ILoadSource;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JQ-Cao on 2016/6/6.
 */
public class LoadSource implements ILoadSource {
    @Override
    public Map<Long, Integer> loadCorr_map(String corrFile) {
        Map<Long,Integer> corrMapVRel = new HashMap<Long, Integer>();
        File file = new File(corrFile);
        BufferedReader bufferedReader = null;
        try{
            bufferedReader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = bufferedReader.readLine()) != null) {
                String a[]=tempString.split(" ");
                for (String s:a) {
                    String temp[]=s.replace("(","").replace(")","").split(",");
                    corrMapVRel.put(Long.parseLong(temp[0]),Integer.parseInt(temp[1]));
                }
            }
            bufferedReader.close();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(bufferedReader!=null){
                try{
                    bufferedReader.close();
                }catch (IOException e1){
                    e1.printStackTrace();
                }
            }
        }
        return corrMapVRel;
    }

    @Override
    public Map<Long, Double> loadV_map(String vFile,Map<Long,Integer> corr_map) {
        Map<Long,Double> result_map = new HashMap<Long, Double>();
        File file = new File(vFile);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = bufferedReader.readLine()) != null) {
                String temp[] = tempString.split(",");
                for(long id:corr_map.keySet()){
                    Double tempCost = Double.valueOf(temp[corr_map.get(id) + 1]);
                    result_map.put(id,tempCost);
                }
            }
            bufferedReader.close();
        } catch (IOException IOE) {
            System.out.println("loadV_map have error! " + IOE.getMessage());
        }finally {
            if(bufferedReader!=null){
                try{
                    bufferedReader.close();
                }catch (IOException e1){
                    e1.printStackTrace();
                }
            }
        }
        return result_map;
    }

    @Override
    public Set<Vertex> loadVertex(String vertexFile) {
        Set<Vertex> vertices = new HashSet<Vertex>();
        File file = new File(vertexFile);
        BufferedReader bufferedReader = null;
        try{
            bufferedReader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString=bufferedReader.readLine())!=null){
                String temp[]=tempString.split("\t");
                vertices.add(new Vertex(Long.parseLong(temp[0]), Double.parseDouble(temp[2]), Double.parseDouble(temp[1])));
            }
            bufferedReader.close();
        }catch (IOException e){
            e.printStackTrace();
        }finally{
            if(bufferedReader!=null){
                try{
                    bufferedReader.close();
                }catch (IOException e1){
                    e1.printStackTrace();
                }
            }
        }
        return vertices;
    }

    public Set<Vertex> loadVertex2(String vertexFile) {
        Set<Vertex> vertices = new HashSet<Vertex>();
        File file = new File(vertexFile);
        BufferedReader bufferedReader = null;
        try{
            bufferedReader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString=bufferedReader.readLine())!=null){
                String temp[]=tempString.split("\t");
                vertices.add(new Vertex(Long.parseLong(temp[0]), Double.parseDouble(temp[1]), Double.parseDouble(temp[2])));
            }
            bufferedReader.close();
        }catch (IOException e){
            e.printStackTrace();
        }finally{
            if(bufferedReader!=null){
                try{
                    bufferedReader.close();
                }catch (IOException e1){
                    e1.printStackTrace();
                }
            }
        }
        return vertices;
    }

    @Override
    public void updateVertex(Set<Vertex> vertices, Map<Long, Double> v_map) {
        for(Vertex vertex:vertices){
            vertex.setCost(v_map.get(vertex.getId()));
        }
    }

    @Override
    public Set<Edge> loadEdge(String edgeFile, Set<Vertex> vertices) {
        Map<Long,Vertex> vertex_map = new HashMap<Long, Vertex>();
        for(Vertex vertex:vertices){
            vertex_map.put(vertex.getId(),vertex);
        }
        Set<Edge> edges = new HashSet<Edge>();
        File file = new File(edgeFile);
        BufferedReader bufferedReader = null;
        try{
            bufferedReader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString=bufferedReader.readLine())!=null){
                String temp[]=tempString.split("\t");
                Edge tempEdge = new Edge(Long.parseLong(temp[0]),vertex_map.get(Long.parseLong(temp[1])),
                        vertex_map.get(Long.parseLong(temp[2])),Double.valueOf(temp[3]));
                vertex_map.get(Long.parseLong(temp[1])).getNextVertices().add(vertex_map.get(Long.parseLong(temp[2])));
                edges.add(tempEdge);
            }
            bufferedReader.close();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(bufferedReader!=null){
                try {
                    bufferedReader.close();
                }catch (IOException e1){
                    e1.printStackTrace();
                }
            }
        }
        return edges;
    }


    @Override
    public Map<Integer, Long> loadGaoDeMap(String fileName) {
        Map<Integer,Long> map = new HashMap<>();
        File file = new File(fileName);
        BufferedReader bufferedReader = null;
        try{
            bufferedReader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = bufferedReader.readLine()) != null) {
                String temp[]=tempString.split(",");
                map.put(Integer.valueOf(temp[0]), Long.valueOf(temp[1]));
            }
            bufferedReader.close();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(bufferedReader!=null){
                try{
                    bufferedReader.close();
                }catch (IOException e1){
                    e1.printStackTrace();
                }
            }
        }
        return map;
    }

    @Override
    public Set<Long> getFilenames(String path) {
        Set<Long> result = new HashSet<>();
        File library = new File(path);
        if(library.isDirectory()){
            File[] fileArray = library.listFiles();
            for(File file:fileArray){
                if(file.isFile()){
                    if(file.getName().charAt(0)=='V'){
                        result.add(Long.valueOf(file.getName().substring(1,12)));
                    }
                }
            }
        }else {
            System.out.println("path was wrong! it's not library");
        }
        return result;
    }

    @Override
    public List<Long> getSample(String fileName) {
        List<Long> result = new ArrayList<>();
        Map<Long,Set<Long>> map = new HashMap<>();
        File file  = new File(fileName);
        BufferedReader bufferedReader = null;
        try{
            bufferedReader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString=bufferedReader.readLine())!=null){
                String temp[]=tempString.split("\\t");
                Set<Long> set = null;
                if(map.get(Long.valueOf(temp[0]))==null){
                    set = new HashSet<>();
                    map.put(Long.valueOf(temp[0]),set);
                }else{
                    set = map.get(Long.valueOf(temp[0]));
                }
                set.add(Long.valueOf(temp[1]));
            }
            bufferedReader.close();
        }catch (IOException IOE){
            IOE.printStackTrace();
        }
        map.keySet().forEach(x->{
            map.get(x).forEach(y->{
                result.add(x);
                result.add(y);
            });
        });
        return result;
    }

    /**
     *
     * @param filename odpair file path
     * @return odpairList
     */
    public List<ODPair> loadODPairs(String filename){
        List<ODPair> odPairList = new LinkedList<>();
        File file  = new File(filename);
        BufferedReader bufferedReader = null;
        Pattern odpair_compile = Pattern.compile("\\(\\((\\d*),(\\d*)\\),(\\d*),List\\((.*)\\)\\)");
        Matcher odpair_matcher = null;
        Pattern route_compile = Pattern.compile("\\((\\d*),edge_id:(.*)\\)");
        Matcher route_matcher = null;
        try{
            bufferedReader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString=bufferedReader.readLine())!=null){//get line
                odpair_matcher = odpair_compile.matcher(tempString);
                if(odpair_matcher.find()) {
                    long s_id = Long.parseLong(odpair_matcher.group(1));
                    long e_id = Long.parseLong(odpair_matcher.group(2));
                    if(s_id == e_id){
                        continue;
                    }
                    int route_num = Integer.parseInt(odpair_matcher.group(3));
                    ODPair odPair = new ODPair(s_id, e_id, route_num);
                    String[] routes = odpair_matcher.group(4).split(", ");//split route string
                    for (String route : routes) {
                        route_matcher = route_compile.matcher(route);
                        if(route_matcher.find()){
                            int freq = Integer.parseInt(route_matcher.group(1));
                            String[] edge_ids = route_matcher.group(2).split(",");//split edge id stirng
                            List<Long> edges = new LinkedList<>();
                            for (String edge_id : edge_ids) {
                                edges.add(Long.parseLong(edge_id));
                            }
                            odPair.addRoute(new Route(freq,edges));
                        }
                    }
                    odPairList.add(odPair);
                }
            }
            bufferedReader.close();
        }catch (IOException IOE){
            IOE.printStackTrace();
        }
        for (int i = 0; i < odPairList.size(); i++) {
            odPairList.get(i).cleanBadRoutes();
        }
        return odPairList;
    }

    /**
     *
     * @param path trie file path
     * @return odpairList
     */
    public List<ODPair> loadODPairsFromTrie(String path){
        System.out.println("loadODPairsFromTrie from "+ path);
        List<ODPair> odPairList = new LinkedList<>();
        File file = new File(path);
        if(file == null || !file.isDirectory()) {
            System.err.println("loadODPairsFromTrie fail, file error");
            return null;
        }
        String[] list = file.list();
        for (int i = 0; i < list.length; i++) {
            String s = list[i];
            Pattern p = Pattern.compile("(\\d*)_(\\d*).xml");
            Matcher matcher = p.matcher(s);
            if(!matcher.find())
                continue;
            long oid = new Long(matcher.group(1));
            long did = new Long(matcher.group(2));
            ODPair odPair = new ODPair(oid,did);
            RouteTrie routeTrie = RouteTrie.readFromXml(path + s);

            Set<TrieNode> triNodesOfDesitination = routeTrie.getTriNodesOfDesitination(routeTrie.d_id());
            java.util.Iterator<TrieNode> iterator1 = triNodesOfDesitination.iterator();
            int count=0;
            while (iterator1.hasNext()) {
                TrieNode next = iterator1.next();
                int route_id = next.route_id();
                scala.collection.immutable.List<Object> objectList = next.vertex_ids();
                List<Long> edgeids = new LinkedList<>();
                scala.collection.Iterator<Object> iterator = objectList.iterator();
                while (iterator.hasNext()) {
                    Long next1 = (Long) iterator.next();
                    edgeids.add(next1);
                }
                Route r = new Route(next.freq(),edgeids);
                r.setId(route_id);
                odPair.addRoute(r);
                ++count;
            }
            odPair.setRoute_num(count);
            odPairList.add(odPair);
            //载入是否是outlier
            File f = new File(path+"actual"+Properties.separator+s+".actual");
            if(f.canRead()){
                try {
                    Map<Long,Boolean> map = new HashMap<>();
                    String s1 = null;
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
                    while((s1 = bufferedReader.readLine())!=null) {
                        String[] split = s1.split(",");
                        map.put(new Long(split[0]), new Boolean(split[1]));
                    }
                    bufferedReader.close();

                    List<Route> routes = odPair.getRoutes();
                    for (int j = 0; j < routes.size(); j++) {
                        routes.get(j).setIsOutlier(map.get(routes.get(j).getId()));
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                File file1 = new File(path + "ibat" + Properties.separator + s + ".ibat.result");
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file1));
                String s1 = null;
                Map<Long,Double> map = new HashMap<>();
                Pattern pattern = Pattern.compile("the (\\d*) ibat score is (.*) ,");
                while ((s1 = bufferedReader.readLine())!=null){
                    Matcher matcher1 = pattern.matcher(s1);
                    if(matcher1.find()){
                        map.put(new Long(matcher1.group(1)),new Double(matcher1.group(2)));
                    }
                }
                bufferedReader.close();
                List<Route> routes = odPair.getRoutes();
                for (int i1 = 0; i1 < routes.size(); i1++) {
                    Route route = routes.get(i1);
                    route.setIbat_score(map.get(route.getId()));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                File file1 = new File(path + "search"+Properties.separator + s + ".search.result");
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file1));
                String s1 = null;
                Map<Long,Double> map = new HashMap<>();
                Pattern pattern = Pattern.compile("the (\\d*), search score is (.*) ,");
                while ((s1 = bufferedReader.readLine())!=null){
                    Matcher matcher1 = pattern.matcher(s1);
                    if(matcher1.find()){
                        map.put(new Long(matcher1.group(1)),new Double(matcher1.group(2)));
                    }
                }
                bufferedReader.close();
                List<Route> routes = odPair.getRoutes();
                for (int i1 = 0; i1 < routes.size(); i1++) {
                    Route route = routes.get(i1);
                    route.setSearch_score(map.get(route.getId()));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        System.out.println("loadODPairsFromTrie success, odpair's size is " + odPairList.size());
        return odPairList;
    }

    public static void main(String[] args) {
        LoadSource loadSource = new LoadSource();

//        List<ODPair> odPairList = loadSource.loadODPairs(tool.Properties.odpairFilePath);
        List<ODPair> odPairList = loadSource.loadODPairsFromTrie(Properties.trieFilePath);
        final int[] count = {0};
        odPairList.forEach(new Consumer<ODPair>() {
            @Override
            public void accept(ODPair odPair) {
                System.out.println(odPair);
                odPair.getRoutes().forEach(new Consumer<Route>() {
                    @Override
                    public void accept(Route route) {
                        System.out.println(route);
                        count[0]++;
                    }
                });
            }
        });
        System.out.println(count[0]);
    }
}
