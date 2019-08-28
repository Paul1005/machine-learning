package revisionOperator;

import java.util.HashMap;

public class Belief {
    private HashMap<String, String> solution;
    private int rank;

    public Belief(){
        solution = new HashMap<>();
        rank = 0;
    }

    public HashMap<String, String> getSolution() {
        return solution;
    }

    public void put(String key, String value) {
        solution.put(key, value);
    }

    public void setSolution(HashMap<String, String> solution){
        this.solution = solution;
    }

    public void replace(String key, String value){
        solution.replace(key, value);
    }

    public int getRank() {
        return rank;
    }

    public void increaseRank() {
        rank++;
    }

    public void decreaseRank(){
        rank--;
    }

    public void setRank(int rank){
        this.rank = rank;
    }

    @Override
    public String toString() {
        return solution.toString() + " rank: " + rank;
    }
}
