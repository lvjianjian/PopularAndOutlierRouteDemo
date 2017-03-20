package model;

import java.io.*;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lzj on 2016/9/17.
 */
public class BitMap {

    public static int ALL = -1;//所有点的bitset全部放入内存
    public static int PARTIAL = 0; // 部分读取
    private Map<Long, BitSet> map = new HashMap<>();
    private Map<Long, BitSet> buffer = new HashMap<>();
    private String path; //文件所在路径
    private int mode; //读取文件模式,-1为一次性读取

    public BitMap(int mode, String path) {
        this.mode = mode;
        this.path = path;
        if (mode == ALL) {
            try {
                loadBitmap(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Deprecated
    public void loadBitmap(String filename) {
        long start = System.currentTimeMillis();
//            File file = new File(path+"/bitmap.txt");
        File file = new File(filename);

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String s = null;
            while ((s = bufferedReader.readLine()) != null) {
                String[] strings = s.split("\\|");
                BitSet bitSet = new BitSet();
                String[] split = strings[1].split(",");
                for (int i = 0; i < split.length; i++) {
                    bitSet.set(new Integer(split[i]), true);
                }
                map.put(new Long(strings[0]), bitSet);
            }

            System.out.println(String.format("load all bitsets finish, get %d bitsets, consume time %d", map.size(), (System.currentTimeMillis() - start)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BitSet getBitSetFromFile(String filename, long key) {
        BitSet bitSet = null;
        File file = new File(filename);
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String s = null;
            while ((s = bufferedReader.readLine()) != null) {
                String[] strings = s.split("\\|");
                if (new Long(strings[0]) == key) {
                    bitSet = new BitSet();
                    String[] split = strings[1].split(",");
                    for (int i = 0; i < split.length; i++) {
                        bitSet.set(new Integer(split[i]), true);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitSet;

    }

    public void loadBitmap(long value) throws Exception {

        if (mode == ALL) {
            loadBitmap(path + "/bitmap.txt");

        } else {
            // 部分读取，通过key查找文件
            map.clear();
            File f = new File(path);
            if (f.isDirectory()) {
                String[] list = f.list();
                for (int i = 0; i < list.length; i++) {
                    String filename = list[i];
                    Pattern compile = Pattern.compile("bitmap-(\\d*)-(\\d*).txt");
                    Matcher matcher = compile.matcher(filename);
                    if (matcher.matches()) {
                        Long start = new Long(matcher.group(1));
                        Long end = new Long(matcher.group(2));
                        if (start <= value && end >= value) {
                            //读取
                            buffer.put(value, getBitSetFromFile(path + "/" + filename, value));
                            return;
                        }
                    }
                }
            } else {
                throw new Exception("%s is not directory".format(path));
            }
        }


    }

    public BitSet get(Long key) {

        BitSet bitSet = null;
        bitSet = buffer.get(key);
        if (bitSet != null) {//从缓冲区取
            return bitSet;
        }
        try {
            loadBitmap(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bitSet = buffer.get(key);
//        System.out.println(key+":"+bitSet.cardinality());
        return bitSet;
    }

    public static void main(String[] args) {
        BitMap bitMap = new BitMap(BitMap.PARTIAL, "C:\\Users\\JQ-Cao\\Downloads");
//        BitMap bitMap = new BitMap(BitMap.PARTIAL, "/home/zhujie/lvzhongjian/bitmap");
        BitSet bitSet = bitMap.get(60565003739L);
        System.out.println(bitSet);

    }

}
