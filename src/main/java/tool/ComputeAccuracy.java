package tool;

import com.sun.org.apache.xpath.internal.operations.Bool;
import model.ODPair;
import model.Route;
import tool.cmpl.LoadSource;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lzj on 2016/10/22.
 */
public class ComputeAccuracy {


    LoadSource loadSource = new LoadSource();
    List<ODPair> odPairList = null;

    public ComputeAccuracy() {
        odPairList = loadSource.loadODPairsFromTrie(Properties.trieFilePath);
    }

    public void compute(String type, String filepath, double threshold, String outputfile) {
        double accuracy = 0;
        double average = 0;
        double[] doubles = null;
        switch (type) {
            case "IBAT":
                doubles = processIBAT(filepath, threshold, outputfile);
                break;
            case "DBTOD":
                doubles = processDBTOD(filepath, outputfile);
                break;
            case "TPRO":
                doubles = processTPRO(filepath, threshold, outputfile);
                break;
            case "PTS":
                doubles = processPTS(filepath, threshold, outputfile);
                break;
        }
        accuracy = doubles[0];
        average = doubles[1];
        System.out.println("accuracy=" + accuracy + ",averageTime=" + average);

    }

    private double[] processPTS(String filepath, double threshold, String outputfile) {
        return processDir(filepath, threshold, "the (.*), search score is (.*) , use time (.*), ", true);
    }

    private double[] processDBTOD(String filepath, String outputfile) {
        return processDir(filepath, -1, "(\\d*),(.*),(.*)", false);
    }

    private double[] processIBAT(String filepath, double threshold, String outputfile) {
        return processDir(filepath, threshold, "the (\\d*) ibat score is (.*) , use time (.*),", false);
    }

    private double[] processTPRO(String filepath, double threshold, String outputfile) {
        return processDir(filepath, threshold, "the (\\d*) tpro score is (.*) , use time (.*)", false);
    }


    private double[] processDir(String filepath, double threshold, String patterString, boolean isPTS) {
        double[] accuracyAndTime = new double[2];
        int all = 0;
        int correct = 0;
        int time = 0;
        File f = new File(filepath);
        String[] list = f.list();
        for (int i = 0; i < list.length; i++) {
            String filename = list[i];
            Pattern p = Pattern.compile("(\\d*)_(\\d*).xml");
            Matcher matcher = p.matcher(filename);
            if (!matcher.find())
                continue;
            long oid = new Long(matcher.group(1));
            long did = new Long(matcher.group(2));
            ODPair odPair = null;
            for (int i1 = 0; i1 < odPairList.size(); i1++) {
                ODPair temp = odPairList.get(i1);
                if (did == temp.getEnd_vertex_id() && oid == temp.getStart_vertex_id()) {
                    odPair = temp;
                    break;
                }
            }
            File file1 = new File(filepath + filename);
            int[] ints =null ;
            if(!isPTS){
                ints = processOneFile(file1, patterString, odPair, threshold);
            }else{
                ints = processOnePTSFile(file1,patterString,odPair,threshold);
            }
            all += ints[1];
            correct += ints[0];
            time += ints[2];
        }
        accuracyAndTime[0] = (double) correct / all;
        accuracyAndTime[1] = (double) time / all;
        return accuracyAndTime;
    }


    /**
     * @return 0:correct,1:all,2:time
     */
    private int[] processOnePTSFile(File file, String patternString, ODPair odPair, double threshold) {
        int[] correctAndAll = new int[3];
        BufferedReader bufferedReader = null;
        String s1 = null;
        Map<Long, Double> map = new HashMap<>();
        Pattern pattern = Pattern.compile(patternString);
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            while ((s1 = bufferedReader.readLine()) != null) {
                Matcher matcher1 = pattern.matcher(s1);
                if (matcher1.find()) {
                    map.put(new Long(matcher1.group(1)), new Double(matcher1.group(2)));
                    correctAndAll[2] += new Double(matcher1.group(3));
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Route> routes = odPair.getRoutes();
        for (int i1 = 0; i1 < routes.size(); i1++) {
            ++correctAndAll[1];
            Route route = routes.get(i1);
            Double aDouble = map.get(route.getId());
            boolean isOutlier = route.getIsOutlier();
            if (aDouble <= threshold) {// 预测为异常
                if (isOutlier == true)
                    ++correctAndAll[0];
            } else {
                if (isOutlier == false)
                    ++correctAndAll[0];
            }
        }
        return correctAndAll;
    }

    /**
     * @return 0:correct,1:all,2:time
     */
    private int[] processOneFile(File file, String patternString, ODPair odPair, double threshold) {
        int[] correctAndAll = new int[3];
        if (threshold > 0) {//针对double 分数类型
            BufferedReader bufferedReader = null;
            String s1 = null;
            Map<Long, Double> map = new HashMap<>();
            Pattern pattern = Pattern.compile(patternString);
            try {
                bufferedReader = new BufferedReader(new FileReader(file));
                while ((s1 = bufferedReader.readLine()) != null) {
                    Matcher matcher1 = pattern.matcher(s1);
                    if (matcher1.find()) {
                        map.put(new Long(matcher1.group(1)), new Double(matcher1.group(2)));
                        correctAndAll[2] += new Double(matcher1.group(3));
                    }
                }
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<Route> routes = odPair.getRoutes();
            for (int i1 = 0; i1 < routes.size(); i1++) {
                ++correctAndAll[1];
                Route route = routes.get(i1);
                Double aDouble = map.get(route.getId());
                boolean isOutlier = route.getIsOutlier();
                if (aDouble > threshold) {// 预测为异常
                    if (isOutlier == true)
                        ++correctAndAll[0];
                } else {
                    if (isOutlier == false)
                        ++correctAndAll[0];
                }
            }
        } else {//针对boolean类型
            BufferedReader bufferedReader = null;
            String s1 = null;
            Map<Long, Boolean> map = new HashMap<>();
            Pattern pattern = Pattern.compile(patternString);
            try {
                bufferedReader = new BufferedReader(new FileReader(file));
                while ((s1 = bufferedReader.readLine()) != null) {
                    Matcher matcher1 = pattern.matcher(s1);
                    if (matcher1.find()) {
                        map.put(new Long(matcher1.group(1)), new Boolean(matcher1.group(2)));
                        correctAndAll[2] += new Double(matcher1.group(3));
                    }
                }
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<Route> routes = odPair.getRoutes();
            for (int i1 = 0; i1 < routes.size(); i1++) {
                ++correctAndAll[1];
                Route route = routes.get(i1);
                boolean isOutlier = route.getIsOutlier();
                if (map.get(route.getId()) == isOutlier) {// 预测和真实相同
                    ++correctAndAll[0];
                }
            }
        }
        return correctAndAll;
    }


    public static void main(String[] args) {
        ComputeAccuracy computeAccuracy = new ComputeAccuracy();
        computeAccuracy.compute("IBAT", "D:\\lzj_spark_test\\data\\trie\\ibat\\", 0.55, "");
//        computeAccuracy.compute("DBTOD", "D:\\lzj_spark_test\\data\\trie\\dbtod\\new\\15_0.03\\", -1, "");
//        computeAccuracy.compute("DBTOD", "D:\\lzj_spark_test\\data\\trie\\dbtod\\new\\20_0.03\\", -1, "");
//        computeAccuracy.compute("DBTOD", "D:\\lzj_spark_test\\data\\trie\\dbtod\\new\\25_0.03\\", -1, "");
//        computeAccuracy.compute("DBTOD", "D:\\lzj_spark_test\\data\\trie\\dbtod\\new\\30_0.03\\", -1, "");
//        computeAccuracy.compute("DBTOD", "D:\\lzj_spark_test\\data\\trie\\dbtod\\new\\35_0.03\\", -1, "");


//        computeAccuracy.compute("PTS", "D:\\lzj_spark_test\\data\\trie\\search\\15\\", 0.03, "");
//        computeAccuracy.compute("PTS", "D:\\lzj_spark_test\\data\\trie\\search\\20\\", 0.03, "");
//        computeAccuracy.compute("PTS", "D:\\lzj_spark_test\\data\\trie\\search\\25\\", 0.03, "");
//        computeAccuracy.compute("PTS", "D:\\lzj_spark_test\\data\\trie\\search\\30\\", 0.03, "");
//        computeAccuracy.compute("PTS", "D:\\lzj_spark_test\\data\\trie\\search\\35\\", 0.03, "");
//        computeAccuracy.compute("DBTOD", "D:\\lzj_spark_test\\data\\trie\\dbtod\\new\\20_0.01\\", -1, "");
//        computeAccuracy.compute("DBTOD", "D:\\lzj_spark_test\\data\\trie\\dbtod\\new\\20_0.02\\", -1, "");
        computeAccuracy.compute("DBTOD", "D:\\lzj_spark_test\\data\\trie\\dbtod\\new\\20_0.03\\", -1, "");
//        computeAccuracy.compute("DBTOD", "D:\\lzj_spark_test\\data\\trie\\dbtod\\new\\20_0.04\\", -1, "");
//        computeAccuracy.compute("DBTOD", "D:\\lzj_spark_test\\data\\trie\\dbtod\\new\\20_0.05\\", -1, "");

//
//        computeAccuracy.compute("DBTOD", "D:\\lzj_spark_test\\data\\trie\\kmtod\\new\\6_15\\",-1,"");
//        computeAccuracy.compute("DBTOD", "D:\\lzj_spark_test\\data\\trie\\kmtod\\new\\6_20\\",-1,"");
//        computeAccuracy.compute("DBTOD", "D:\\lzj_spark_test\\data\\trie\\kmtod\\new\\6_25\\",-1,"");
//        computeAccuracy.compute("DBTOD", "D:\\lzj_spark_test\\data\\trie\\kmtod\\new\\6_30\\",-1,"");
//        computeAccuracy.compute("DBTOD", "D:\\lzj_spark_test\\data\\trie\\kmtod\\new\\6_35\\",-1,"");
//        computeAccuracy.compute("DBTOD", "D:\\lzj_spark_test\\data\\trie\\kmtod\\new\\12_25\\",-1,"");

        computeAccuracy.compute("TPRO", "D:\\lzj_spark_test\\data\\trie\\tpro\\",115,"");
    }

}
