package info;

import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class SearchesTest {
    @Test
    //testing with a string, and two ints
    public void instantiation() {
        String searchy = "search";
        int radius = 3;
        int result = 5;
        ArrayList<String> url = new ArrayList<String>(Arrays.asList("URL1", "URL2", "URL3", "URL4", "URL5", "URL6", "URL7", "URL8","URL9", "URL10"));
        Searches s = new Searches(searchy, radius, result, url);
        assertEquals(searchy, s.searchTerm);
        assertEquals(radius, s.specifiedRadius);
        assertEquals(result, s.expectedResults);
        assertEquals(url, s.urls);
    }

    @Test
    //test equality method
    public void equality() {
        String searchy = "search";
        int radius = 3;
        int result = 5;
        ArrayList<String> url = new ArrayList<String>(Arrays.asList("URL1", "URL2", "URL3", "URL4", "URL5", "URL6", "URL7", "URL8","URL9", "URL10"));
        Searches s1 = new Searches(searchy, radius, result, url);
        Searches s2 = new Searches(searchy, radius, result, url);
        assertEquals(s1, s2);
        s2.expectedResults++;
        assertNotEquals(s1, s2);
    }
}
