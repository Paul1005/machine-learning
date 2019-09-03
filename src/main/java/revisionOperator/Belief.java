package revisionOperator;

import java.util.HashMap;

/*
This class is only utilized in unused methods, it is design to store likelihood that a solution is true (rank is used to measure this, higher=more likely)
 */
public class Belief {
    private HashMap<String, String> solution;
    private int rank;

    public Belief(){
        solution = new HashMap<>();
        rank = 0;
    }

    HashMap<String, String> getSolution() {
        return solution;
    }

    void put(String key, String value) {
        solution.put(key, value);
    }

    void setSolution(HashMap<String, String> solution){
        this.solution = solution;
    }

    void replace(String key, String value){
        solution.replace(key, value);
    }

    int getRank() {
        return rank;
    }

    void increaseRank() {
        rank++;
    }

    public void decreaseRank(){
        rank--;
    }

    void setRank(int rank){
        this.rank = rank;
    }

    @Override
    public String toString() {
        return solution.toString() + " rank: " + rank;
    }
}
