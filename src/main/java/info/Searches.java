package info;
import java.util.*;

public class Searches {
    public String searchTerm;
    public int specifiedRadius;
    public int expectedResults;
    public ArrayList<String> urls;

    public Searches(String st, int sr, int er, ArrayList<String> url){
        searchTerm = st;
        specifiedRadius = sr;
        expectedResults = er;
        urls = url;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Searches)) return false;
        Searches other = (Searches)obj;
        return searchTerm.equals(other.searchTerm) && specifiedRadius==other.specifiedRadius && expectedResults==other.expectedResults;
    }
}
