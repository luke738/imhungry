package servlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHelper {

    private PreparedStatement ps;
    private ResultSet rs;
    private Connection conn;

    public DatabaseHelper(PreparedStatement ps, ResultSet rs, Connection conn){
        this.ps = ps;
        this.rs = rs;
        this.conn = conn;
    }

    protected int findHighestPos(String listname, int userID){
        String actualListname = changeToDatabaseFormat(listname);
        String recipeQuery = "SELECT pos FROM recipe" + actualListname + " l WHERE userID = " + userID;
        String restaurantQuery = "SELECT pos FROM rest" + actualListname + " l WHERE userID = " + userID;
        int highestPos = -1;
        try {
            // get the positions of recipes
            ps = conn.prepareStatement(recipeQuery);
            rs = ps.executeQuery();
            // find the highest position in recipes
            while (rs.next()){
                int pos = rs.getInt("pos");
                if (pos > highestPos) {
                    highestPos = pos;
                }
            }
            // get the positions of restaurants
            ps = conn.prepareStatement(restaurantQuery);
            rs = ps.executeQuery();
            // find the highest position in restaurants
            while (rs.next()) {
                int pos = rs.getInt("pos");
                if (pos > highestPos){
                    highestPos = pos;
                }
            }
        }
        catch(SQLException e){
            System.out.println("SQLException in function \"validate\"");
            e.printStackTrace();
        }
        return highestPos;
    }

    protected void updateIndicesAfterRemove(String listname, int pos, int userID) {
        try {
            String actualName = changeToDatabaseFormat(listname);
            // stop when you reach the highest pos
            int highestPos = findHighestPos(listname, userID);
            // update the pos column for item after removed item to highestPos
            for (int i=pos+1; i<=highestPos; i++){
                // is pos in recipe[listname] ?
                ps = conn.prepareStatement("SELECT r.pos FROM recipe" + actualName + " r WHERE r.pos = ? AND r.userID = ?");
                ps.setInt(1, i);
                ps.setInt(2, userID);
                rs = ps.executeQuery();
                // pos IS in recipe[listname]
                if (rs.next()){
                    // change pos to be one less than before
                    ps = conn.prepareStatement("UPDATE recipe" + actualName + " SET pos = ? WHERE pos = ? AND userID = ?");
                    ps.setInt(1, i-1);
                    ps.setInt(2, i);
                    ps.setInt(3, userID);
                    // if pos is in recipe[listname] it won't be in restaurants
                    continue;
                }
                // is pos in rest[listname] ?
                ps = conn.prepareStatement("SELECT r.pos FROM rest" + actualName + " r WHERE r.pos = ? and r.userID = ?");
                ps.setInt(1, i);
                ps.setInt(2, userID);
                rs = ps.executeQuery();
                // pos IS in rest[listname]
                if (rs.next()){
                    // change pos to be one less than before
                    ps = conn.prepareStatement("UPDATE rest" + actualName + " SET pos = ? WHERE pos = ? AND userID = ?");
                    ps.setInt(1, i-1);
                    ps.setInt(2, i);
                    ps.setInt(3, userID);
                }
            }
        } catch(SQLException e){
            System.out.println("SQLException in function \"validate\"");
            e.printStackTrace();
        }

    }

    // changes input of listname so it can be appended to sql command
    // ex: "To Explore" becomes "toexplore"
    // this means the conversion doesn't have to be done for every function
    protected String changeToDatabaseFormat(String listname){
        String actualListname = listname;
        if (listname.equals("Favorites")){
            actualListname = "favorites";
        } else if (listname.equals("To Explore")){
            actualListname = "toexplore";
        } else if (listname.equals("Do Not Show")){
            actualListname = "donotshow";
        }
        return actualListname;
    }


}
