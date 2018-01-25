package com.xinshan.utils;

import java.util.*;

/**
 * Created by mxt on 15-7-11.
 */
public class SplitUtils {
    public static void main(String[] args) {
        String s = "1,2,3,45,32,43412";
        System.out.println(splitToList(s, ","));
    }

    public static Set<Integer> splitToSet(String content, String del) {
        Set<Integer> set = new HashSet<>();
        if (content != null && !content.equals("")) {
            StringTokenizer tokenizer = new StringTokenizer(content, del);
            while (tokenizer.hasMoreTokens()) {
                Object object = tokenizer.nextToken();
                set.add(Integer.parseInt(object.toString().trim()));
            }
        }
        return set;
    }

    public static Set<String> splitToStrSet(String content, String del) {
        Set<String> set = new HashSet<>();
        if (content != null && !content.equals("")) {
            StringTokenizer tokenizer = new StringTokenizer(content, del);
            while (tokenizer.hasMoreTokens()) {
                Object object = tokenizer.nextToken();
                set.add(object.toString().trim());
            }
        }
        return set;
    }

    public static List<Integer> splitToList(String content, String del) {
        List<Integer> list = new ArrayList<Integer>();
        if (content != null && !content.equals("")) {
            StringTokenizer tokenizer = new StringTokenizer(content, del);
            while (tokenizer.hasMoreTokens()) {
                Object object = tokenizer.nextToken();
                list.add(Integer.parseInt(object.toString().trim()));
            }
        }
        return list;
    }

    public static List<String> splitToStrList(String content, String del) {
        List<String> list = new ArrayList<String>();
        if (content != null && !content.equals("")) {
            String s[] = content.split(del);
            list.addAll(Arrays.asList(s));
        }
        return list;
    }

    public static String listToString(List list) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).toString().trim());
            if (i != list.size() -1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
    public static String setToString(Set set) {
        int size = set.size();
        int i = 0;
        StringBuffer sb = new StringBuffer();
        for (Object o:set) {
            i++;
            sb.append(o.toString().trim());
            if (i != size){
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
