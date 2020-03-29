package com.company.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DataRecorder {
    //This class record important data of an algorithm
    public String iterationN = "iteration";
    //int shortestD;
    public String runningTime = "runningTime";
    public String pre_runningTime = "pre_runningTime";
    public String pre_iterationN = "pre_iterationN";
    public String hk_bfsVisitedE = "Hk_bfsVisitedE";
    public String hK_dfsVisitedE = "HK_dfsVisitedE";
    public String jocg_bfsVisitedE = "Jocg_bfsVisitedE";
    public String jocg_dfsVisitedE = "Jocg_dfsVisitedE";
    public String pre_bfsVisitedE = "pre_bfsVisitedE";
    public String pre_dfsVisitedE = "pre_dfsVisitedE";
    public String deletedEdgesN = "deletedEdgesN";
    private List<String> nameList;
    private ArrayList<Long> dataList;
    String[] dataName = new String[]{iterationN,runningTime,pre_runningTime,pre_iterationN,
            hk_bfsVisitedE,hK_dfsVisitedE,jocg_bfsVisitedE,jocg_dfsVisitedE,pre_bfsVisitedE,
            pre_dfsVisitedE,deletedEdgesN};

    public DataRecorder(){
        nameList = Arrays.asList(dataName);
        dataList = new ArrayList<>();
        for(int i = 0; i < nameList.size(); i++){
            dataList.add((long)0);
        }
    }

    public void add(String key, long number){
        int index = indexOf(key);
        dataList.set(index,dataList.get(index)+number);
    }

    public void set(String key, long number){
        int index = indexOf(key);
        dataList.set(index,number);
    }


    private int indexOf(String key){
        return nameList.indexOf(key);
    }

    public long get(String key){
        return dataList.get(indexOf(key));
    }

    public void reset(){
        dataList = new ArrayList<>();
        for(int i = 0; i < nameList.size(); i++){
            dataList.add((long)0);
        }
    }

    public String printResult(){
        String result = "";
        for(int i = 0; i < dataList.size(); i++){
            result = result.concat(nameList.get(i) +" : " + dataList.get(i) + "\n");
        }
        return result;
    }

}
