package tool;

import java.io.File;

/**
 * Created by JQ-Cao on 2016/6/6.
 */
public class Properties {
    private static String OS = System.getProperty("os.name").toLowerCase();
    public static String  PROJECT_PATH = null;
    //    public static final String PROJECT_PATH = "E:\\ZhongjianLv\\project\\PopularRouteDemo\\";
    //judge windods or linux to set project path
    static {
        if(isLinux()){
            PROJECT_PATH =  "/root/tomcat/webapps/ROOT/";
        }else if(isWindows()){
            PROJECT_PATH = "E:\\ZhongjianLv\\project\\PopularRouteDemo\\";
        }
    }


    /*参数 */
    public static final int KPath = 3;
    public static final double similarity = 0.7;

    public static final String separator = File.separator;

    /* library path*/
    public static final String libraryPath = "D:"+separator+"data"+separator+"shrinkSet"+separator+"result_new";
    /*file path*/

    public static final String edgeFile = PROJECT_PATH + "edges_new.txt";
    public static final String vertexFile = PROJECT_PATH + "vertices_new.txt";
//    public static final String edgeFile = "/home/zhujie/lvzhongjian/search/edges_new.txt";
//    public static final String vertexFile  = "/home/zhujie/lvzhongjian/search/vertices_new.txt";

    public static final String mapFile = PROJECT_PATH + "hashMap.txt";
    public static final String VFile = "D:"+separator+"data"+separator+"shrinkSet"+separator+"result_new"+separator+"V";
    public static final String gaodeApiMap = PROJECT_PATH + "map.txt";

    /*sample data save path*/
    public static final String samplePath = "D:"+separator+"data"+separator+"shrinkSet"+separator+"result_new"+separator+"sample.txt";

    /*默认最小优先队列大小*/
    public static final int size = 10000;

    public static final double defaultMin = 1E-300;
    public static final double defaultMax = 1.0;
    public static String odpairFilePath = PROJECT_PATH + "odpairwithroute"+separator+"part-00000";
    public static String trieFilePath = PROJECT_PATH + "trie"+separator;
    public static String referencesPath = PROJECT_PATH + "referenceRoutes"+separator;



    public static boolean isLinux(){
        return OS.indexOf("linux")>=0;
    }

    public static boolean isWindows(){
        return OS.indexOf("windows")>=0;
    }
}
