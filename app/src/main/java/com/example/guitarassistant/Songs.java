package com.example.guitarassistant;

import java.io.Serializable;
import java.util.LinkedList;

public class Songs  implements Serializable {

    public int bId;

    public LinkedList<Song> songs;

    public Songs(String str){
        String strArray[] = str.split("#");
        try {
            bId = Integer.parseInt(strArray[0]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        songs = new LinkedList<>();
        for(int i = 1; i < strArray.length; i++){
            if(!strArray[i].equals("")){
                Song song = new Song(strArray[i]);
                songs.add(song);
            }
        }
    }
    
    public void addSong(){
        insertSong((String.valueOf(bId) + "`新建歌曲`"));
    }

    public void insertSong(String str){
        Song song = new Song(str);
        songs.add(song);
        bId += 1;
    }
    
    public void deleteSong(int _id){
        Song song = null;
        for(Song x:songs){
            if(x.id == _id)
                song = x;
        }
        if(songs != null)
            songs.remove(song);
    }

    public void setSong(int _id, String _name, LinkedList<Integer> _beats){
        for(Song x:songs){
            if(x.id == _id){
                x.name = _name;
                x.beats = _beats;
            }
        }
    }

    public String getSongName(int _id){
        for(Song x:songs){
            if(x.id == _id)
                return x.name;
        }
        return null;
    }

    public LinkedList<Integer> getSongBeats(int _id){
        for(Song x:songs){
            if(x.id == _id)
                return x.beats;
        }
        return null;
    }

    @Override
    public String toString() {
        String str = String.valueOf(bId) + "#";
        for(Song x:songs){
            str = str + String.valueOf(x.id) + "`" + x.name + "`";
            for(int y:x.beats){
                str = str + String.valueOf(y) + "`";
            }
            str += "#";
        }
        str += "#";
        return str;
    }
}