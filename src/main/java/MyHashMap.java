import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyHashMap<T> {

    List<T> list = new ArrayList<>();
    HashMap<Integer,T> myMap;

    public MyHashMap(){}

    public MyHashMap(List<T> list){
        myMap = new HashMap<>();
        int count = 1;
        for(T element : list){
            myMap.put(count,element);
            count++;
        }
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public HashMap<Integer, T> getMyMap() {
        return myMap;
    }

    public void setMyMap(HashMap<Integer, T> myMap) {
        this.myMap = myMap;
    }

    public T get(int key){
        return myMap.get(key);
    }

    public void stampaPossibiliScelte(){

        for(int key : myMap.keySet()){
            System.out.println(key + "  " + myMap.get(key));
        }
    }

    public static void stampaSpettacoli(MyHashMap<Spettacolo> spettacolo){

        for(int key : spettacolo.myMap.keySet()){
            System.out.println(key + "  " + spettacolo.get(key).getSede().getCitta().getNome());
        }
    }

}
