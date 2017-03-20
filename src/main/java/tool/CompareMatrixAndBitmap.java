package tool;


import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhongjian on 2017/1/18.
 * 比较以matrix和bitmap生成的概率差异
 */
public class CompareMatrixAndBitmap {

    public static final String path = "E:/ZhongjianLv/project/PopularRouteDemo/trie";

    public static final Map<String, Map<Integer, Double[]>> map = new HashMap<>();

    public static  final Pattern p = Pattern.compile("the (.*), search score is (.*) , use time (.*)");

    public static void main(String[] args) {
        int[] alpha = new int[]{25};//, 10, 15, 20, 25
        for (int i = 0; i < alpha.length; i++) {
            File f_search = new File(path + "/search/" + alpha[i] + "/");
            File f_martix = new File(path + "\\matrixsearch\\matrix_" + alpha[i] + "\\");
            String[] search = f_search.list();
            String[] matrix = f_martix.list();
            BufferedReader bitmapReader = null;
            BufferedReader matrixReader = null;
            for (int j = 0; j < search.length; j++) {
                try {
                    String filename = matrix[j];
                    bitmapReader = new BufferedReader(new FileReader(new File(f_search.getAbsolutePath() + "\\" + filename)));
                    matrixReader = new BufferedReader(new FileReader(new File(f_martix.getAbsolutePath() + "\\" + filename)));
                    String od_pair = filename.split("\\.")[0];
                    if(!map.containsKey(od_pair))
                    {
                        map.put(od_pair,new HashMap<>());
                    }
                    Map<Integer, Double[]> integerMap = map.get(od_pair);
                    String s = null;
                    while ((s = bitmapReader.readLine()) != null){
                        Matcher matcher = p.matcher(s);
                        matcher.find();
                        int id = new Integer(matcher.group(1));
                        if (!integerMap.containsKey(id)) {
                            integerMap.put(id,new Double[2]);
                        }
                        integerMap.get(id)[0] = new Double(matcher.group(2));
                    }
                    while ((s = matrixReader.readLine()) != null){
                        Matcher matcher = p.matcher(s);
                        matcher.find();
                        int id = new Integer(matcher.group(1));
                        integerMap.get(id)[1] = new Double(matcher.group(2));
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (bitmapReader != null)
                        try {
                            bitmapReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    if (matrixReader != null)
                        try {
                            matrixReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }

            map.forEach(new BiConsumer<String, Map<Integer, Double[]>>() {
                @Override
                public void accept(String s, Map<Integer, Double[]> integerMap) {
                    System.out.println(s);
                    integerMap.forEach(new BiConsumer<Integer, Double[]>() {
                        @Override
                        public void accept(Integer integer, Double[] doubles) {
                            if(doubles[0]>0.9)
                                System.err.println(integer);
                            System.out.println(integer+","+"bitmap:"+doubles[0]+",matrix:"+doubles[1]);
                        }
                    });
                }
            });
        }

    }
}
