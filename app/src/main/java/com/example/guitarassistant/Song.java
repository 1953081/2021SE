package com.example.guitarassistant;

import java.io.Serializable;
import java.util.LinkedList;

public class Song  implements Serializable {

    public int id;
    public String name;
    public LinkedList<Integer> beats;

    public Song(String str){
        String strArray[] = str.split("`");
        try {
            id = Integer.parseInt(strArray[0]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        name = strArray[1];
        beats = new LinkedList<>();
        for(int i = 2; i < strArray.length; i++){
            try {
                int j = Integer.parseInt(strArray[i]);
                beats.add(j);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
}
