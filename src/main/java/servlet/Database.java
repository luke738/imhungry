package servlet;

import com.google.gson.Gson;
import info.Info;
import info.Searches;
import info.RecipeInfo;
import info.RestaurantInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Database
{
    private Connection conn;
    private Statement st;
    private PreparedStatement ps;
    private ResultSet rs;

    public Database() {
        conn = null;
        ps = null;
        rs = null;

        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/imhungry","root", "root1234!");
        }
        catch (SQLException sqle) {
            System.out.println ("SQLException: " + sqle.getMessage());
        }
        catch (ClassNotFoundException cnfe) {
            System.out.println ("ClassNotFoundException: " + cnfe.getMessage());
        }
    }

    public Boolean checkUser(String username) {
        try {
            ps = conn.prepareStatement("SELECT u.userID FROM user u WHERE username=?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (!rs.next()) {
                return false;
            }
            return true;

        } catch (SQLException e) {
            System.out.println("SQLException in function \"validate\"");
            e.printStackTrace();
        }

        return false;
    }

    public String[] getPasswordInfo(String username) {
        try {
            if (checkUser(username)) {
                int userID = getUserID(username);
                ps = conn.prepareStatement("SELECT u.pw, u.salt FROM user u WHERE username=? AND userID =?");
                ps.setString(1, username);
                ps.setInt(2, userID);
                rs = ps.executeQuery();
                while(rs.next()) {
                    String password = rs.getString("pw");
                    String salt = rs.getString("salt");
                    String[] pINfo = new String[2];
                    pINfo[0] = salt;
                    pINfo[1] = password;
                    return pINfo;
                }
            }
        }catch (SQLException e) {
            System.out.println("SQLException in function \"validate\"");
            e.printStackTrace();
        }
        return new String[]{"",""};
    }

    public int getUserID(String username) {
        try {
            ps = conn.prepareStatement("SELECT u.userID FROM user u WHERE username=?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("userID");
            }

        } catch (SQLException e) {
            System.out.println("SQLException in function \"validate\"");
            e.printStackTrace();
        }
        return -1;
    }

    public Boolean createUser(String username, String passwordHash, String salt) {
        if(checkUser(username)){
            return false;
        }
        try {
            ps = conn.prepareStatement("INSERT INTO user(username, pw, salt) VALUES(?,?,?)");
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, salt);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("SQLException in function \"validate\"");
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<Info> getLists(int userID, String listname) {
        ArrayList<Info> pList = new ArrayList<Info>();
        try {
            if(listname.equals("Favorites")) {
                ps = conn.prepareStatement("SELECT DISTINCT rec.rID, rec.userID, r.recipeIDapi, r.prepTime, r.rating, r.CookTime, r.ingredient, r.instructions, r.imageURL, r.rname, rec.rID FROM recipefavorites rec JOIN recipe r WHERE rec.userID=? AND rec.rID = r.recipID");
            }
            else if(listname.equals("To Explore")) {
                ps = conn.prepareStatement("SELECT DISTINCT rec.rID, rec.userID, r.recipeIDapi, r.prepTime, r.rating, r.CookTime, r.ingredient, r.instructions, r.imageURL, r.rname, rec.rID FROM recipetoexplore rec JOIN recipe r WHERE rec.userID=? AND rec.rID = r.recipID");
            }
            else if(listname.equals("Do Not Show")) {
                ps = conn.prepareStatement("SELECT DISTINCT rec.rID, rec.userID, r.recipeIDapi, r.prepTime, r.rating, r.CookTime, r.ingredient, r.instructions, r.imageURL, r.rname, rec.rID FROM recipedonotshow rec JOIN recipe r WHERE rec.userID=? AND rec.rID = r.recipID");
            }
            else if(listname.equals("Grocery")){
                ps = conn.prepareStatement("SELECT DISTINCT grow.grocID, grow.userID, grow.recipeID AS 'rID', r.recipeIDapi, r.prepTime, r.rating, r.CookTime, r.ingredient, r.instructions, r.imageURL, r.rname FROM groceries grow JOIN recipe r WHERE grow.userID=? AND grow.recipeID = r.recipID");
            }
            ps.setInt(1, userID);
            rs = ps.executeQuery();


            while(rs.next()){
                int dbid = rs.getInt("rID");
                String rname = rs.getString("rname");
                int rating = rs.getInt("rating");
                int recipeIDapi = rs.getInt("recipeIDapi");
                int prepTime = rs.getInt("prepTime");
                int cookTime = rs.getInt("cookTime");
                Gson gson = new Gson();
                String ingredientsString   = rs.getString("ingredient");
                String[] ingredientsArray = gson.fromJson(ingredientsString, String[].class);
                ArrayList<String> ingredients = new ArrayList<String>(Arrays.asList(ingredientsArray));
                String instructionString   = rs.getString("instructions");
                String[] instructionArray = gson.fromJson(instructionString, String[].class);
                ArrayList<String> instructions = new ArrayList<String>(Arrays.asList(instructionArray));
                String imageurl = rs.getString("imageURL");
                RecipeInfo p = new RecipeInfo(rname, rating, recipeIDapi, prepTime, cookTime, ingredients, instructions,imageurl, dbid);
                pList.add(p);
            }

            //return grocery list after go thru recipes because no restaurants in grocery list
            if(listname.equals("Grocery")){
                return pList;
            }
            if(listname.equals("Favorites")) {
                ps = conn.prepareStatement("SELECT DISTINCT  rest.userID, rest.rID, r.rname, r.rating, r.placeID, r.address, r.priceL, r.driveTimeT, r.driveTimeV, r.phone, r.url,  rest.rID FROM restfavorites rest JOIN restaurant r WHERE rest.userID=? AND rest.rID = r.restaurantID");
            }
            else if(listname.equals("To Explore")) {
                ps = conn.prepareStatement("SELECT DISTINCT  rest.userID, rest.rID, r.rname, r.rating, r.placeID, r.address, r.priceL, r.driveTimeT, r.driveTimeV, r.phone, r.url, rest.rID FROM resttoexplore rest JOIN restaurant r WHERE rest.userID=? AND rest.rID = r.restaurantID");
            }
            else if(listname.equals("Do Not Show")) {
                ps = conn.prepareStatement("SELECT DISTINCT  rest.userID, rest.rID, r.rname, r.rating, r.placeID, r.address, r.priceL, r.driveTimeT, r.driveTimeV, r.phone, r.url, rest.rID FROM restdonotshow rest JOIN restaurant r WHERE rest.userID=? AND rest.rID = r.restaurantID");
            }

            ps.setInt(1, userID);
            rs = ps.executeQuery();
            System.out.println(rs);
            while(rs.next()){
                int dbid = rs.getInt("rID");
                String restname = rs.getString("rname");
                int rating = rs.getInt("rating");
                String placeID = rs.getString("placeID");
                String restaddress = rs.getString("address");
                String priceLevel = rs.getString("priceL");
                int price = priceLevel.length();
                String driveTimeT = rs.getString("driveTimeT");
                int driveTimeV = rs.getInt("driveTimeV");
                String phone = rs.getString("phone");
                String url = rs.getString("url");
                RestaurantInfo p = new RestaurantInfo(restname, rating, placeID, restaddress, price,driveTimeT, driveTimeV, phone, url, dbid);
                pList.add(p);
            }
            return pList;
        } catch (SQLException e) {
            System.out.println("SQLException in function \"validate\"");
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private Boolean addToList(int userID, Boolean isRecipe, String listname, Info i){
        //adding recipes
        try {
            if (isRecipe) {
                // check if recipe already exists in recipe table
                ps = conn.prepareStatement("SELECT r.recipeIDapi, r.recipID FROM recipe r WHERE r.recipeIDapi = ?");
                ps.setInt(1, ((RecipeInfo) i).recipeID);
                rs = ps.executeQuery();
                // if not already in recipe table, add it
                if (!rs.next()) {
                    ps = conn.prepareStatement("INSERT INTO recipe(recipeIDapi, prepTime, cookTime, ingredient, instructions, imageurl, rating, rname) VALUES(?,?,?,?,?,?,?,?)");
                    ps.setInt(1, ((RecipeInfo) i).recipeID);
                    ps.setInt(2, ((RecipeInfo) i).prepTime);
                    ps.setInt(3, ((RecipeInfo) i).cookTime);
                    Gson gson = new Gson();
                    String ingredientString = gson.toJson(((RecipeInfo) i).ingredients);
                    ps.setString(4, ingredientString);
                    String instructionString = gson.toJson(((RecipeInfo) i).instructions);
                    ps.setString(5, instructionString);
                    ps.setString(6, ((RecipeInfo) i).imageURL);
                    ps.setInt(7, i.rating);
                    ps.setString(8, ((RecipeInfo)i).name);
                    ps.executeUpdate();
                    ps = conn.prepareStatement("SELECT r.recipeIDapi, r.recipID FROM recipe r WHERE r.recipeIDapi = ?");
                    ps.setInt(1, ((RecipeInfo) i).recipeID);
                    rs = ps.executeQuery();
                    rs.next();
                }
                int dbids = rs.getInt("recipID");
                if (listname.equals("Favorites")) {
                    //checking that the specified user has the specified recipe in the Favorites list
                    ps = conn.prepareStatement("SELECT r.rID AND r.userID FROM recipefavorites r WHERE r.rID = ? AND r.userID = ?");
                } else if (listname.equals("Do Not Show")) {
                    //checking that the specified user has the specified recipe in the Donotshow list
                    ps = conn.prepareStatement("SELECT r.rID FROM recipedonotshow r WHERE r.rID = ? AND r.userID = ?");
                } else if (listname.equals("To Explore")) {
                    //checking that the specified user has the specified recipe in the to explore list
                    ps = conn.prepareStatement("SELECT r.rID FROM recipetoexplore r WHERE r.rID = ? AND r.userID = ?");
                } else if (listname.equals("Grocery")) {
                    //checking that the specified user has the specified recipe in the to explore list
                    ps = conn.prepareStatement("SELECT g.recipeID FROM groceries g WHERE g.recipeID = ? AND g.userID = ?");
                }
                ps.setInt(1, dbids);
                ps.setInt(2, userID);
                rs = ps.executeQuery();
                //Did already exist in the specified list
                if(rs.next()){
                    return false;
                }

                // TODO: FIND OUT WHAT POS TO GIVE NEW ITEM
                int highestPos = -1;
                if (listname.equals("Favorites")) {
                    highestPos = findHighestPos("favorites", userID);
                    //checking that the specified user has the specified recipe in the Favorites list
                    ps = conn.prepareStatement("INSERT INTO recipefavorites(rID, userid, pos) VALUES(?,?,?)");
                } else if (listname.equals("Do Not Show")) {
                    highestPos = findHighestPos("donotshow", userID);
                    //checking that the specified user has the specified recipe in the Donotshow list
                    ps = conn.prepareStatement("INSERT INTO recipedonotshow(rID, userid, pos) VALUES(?,?,?)");
                } else if (listname.equals("To Explore")) {
                    highestPos = findHighestPos("toexplore", userID);
                    //checking that the specified user has the specified recipe in the to explore list
                    ps = conn.prepareStatement("INSERT INTO recipetoexplore(rID, userid, pos) VALUES(?,?,?)");
                } else if (listname.equals("Grocery")) {
                    //checking that the specified user has the specified recipe in the to explore list
                    ps = conn.prepareStatement("INSERT INTO groceries(recipeID, userID) VALUES(?,?)");
                }
                ps.setInt(1, dbids);
                ps.setInt(2, userID);
                ps.setInt(3, highestPos + 1);
                ps.executeUpdate();
                return true;
            }

            //for adding restaurants
            else {
                ps = conn.prepareStatement("SELECT r.placeID, r.restaurantID FROM restaurant r WHERE r.placeID = ?");
                ps.setString(1, ((RestaurantInfo) i).placeID);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    ps = conn.prepareStatement("INSERT INTO restaurant(rname, address, priceL, driveTimeT, driveTimeV, phone, url, rating, placeID) VALUES(?,?,?,?,?,?,?,?,?)");
                    ps.setString(1, ((RestaurantInfo) i).name);
                    ps.setString(2, ((RestaurantInfo) i).address);
                    ps.setString(3, ((RestaurantInfo) i).priceLevel);
                    ps.setString(4, ((RestaurantInfo) i).driveTimeText);
                    ps.setInt(5, ((RestaurantInfo)i).driveTimeValue);
                    ps.setString(6, ((RestaurantInfo) i).phone);
                    ps.setString(7, ((RestaurantInfo) i).url);
                    ps.setInt(8, ((RestaurantInfo)i).rating);
                    ps.setString(9, ((RestaurantInfo) i).placeID);
                    ps.executeUpdate();
                    ps = conn.prepareStatement("SELECT r.restaurantID FROM restaurant r WHERE r.placeID = ?");
                    System.out.println("HERERE");
                    ps.setString(1, ((RestaurantInfo) i).placeID);
                    rs = ps.executeQuery();
                    rs.next();
                }
                int dbids = rs.getInt("restaurantID");
                if (listname.equals("Favorites")) {
                    ps = conn.prepareStatement("SELECT r.rID FROM restfavorites r WHERE r.rID= ? AND r.userID= ?");
                } else if (listname.equals("Do Not Show")) {
                    ps = conn.prepareStatement("SELECT r.rID FROM restdonotshow r WHERE r.rID= ? AND r.userID= ?");
                } else if (listname.equals("To Explore")) {
                    ps = conn.prepareStatement("SELECT r.rID FROM resttoexplore r WHERE r.rID= ? AND r.userID= ?");
                }
                ps.setInt(1, dbids);
                ps.setInt(2, userID);
                rs = ps.executeQuery();
                //Did already exist in the specified list
                if(rs.next()){
                    System.out.println("Here I am ");
                    return false;
                }
                // TODO: FIND OUT WHAT POS TO GIVE NEW ITEM
                int highestPos = -1;
                if (listname.equals("Favorites")) {
                    highestPos = findHighestPos("favorites", userID);
                    ps = conn.prepareStatement("INSERT INTO restfavorites(rID, userid, pos) VALUES(?,?,?)");
                } else if (listname.equals("Do Not Show")) {
                    highestPos = findHighestPos("donotshow", userID);
                    ps = conn.prepareStatement("INSERT INTO restdonotshow(rID, userid, pos) VALUES(?,?,?)");
                } else if (listname.equals("To Explore")) {
                    highestPos = findHighestPos("toexplore", userID);
                    ps = conn.prepareStatement("INSERT INTO resttoexplore(rID, userid, pos) VALUES(?,?,?)");
                }
                ps.setInt(1, dbids);
                ps.setInt(2, userID);
                ps.setInt(3, highestPos);
                ps.executeUpdate();
                return true;
            }

        }catch(SQLException e){
            System.out.println("SQLException in function \"validate\"");
            e.printStackTrace();
        }
        return false;

    }

    private int findHighestPos(String listname, int userID){
        String recipeQuery = "SELECT list.pos FROM recipe" + listname + " list WHERE userID=" + userID;
        String restaurantQuery = "SELECT list.pos FROM rest" + listname + " list WHERE userID=" + userID;
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

    private Boolean removeFromList(int userID, Boolean isRecipe, String listname, Info i) {
        try {
            //removing recipe

            if (isRecipe) {

                //checking if added to Recipe Database in the past
                // finding the ID of recipe in the database by identifying the unique api recipe ID
                ps = conn.prepareStatement("SELECT r.recipeIDapi, r.recipID FROM recipe r WHERE r.recipeIDapi = ?");
                ps.setInt(1, ((RecipeInfo) i).recipeID);
                rs = ps.executeQuery();
                // cannot remove an item that has not been added
                if(!rs.next()){
                    System.out.println("IM not supposed to be HERE ");
                    return false;
                }

                //storing the database ID for the recipe to remove
                int dbids = rs.getInt("recipID");
                if (listname.equals("Favorites")) {
                    //checking that the specified user has the specified recipe in the Favorites list
                    ps = conn.prepareStatement("SELECT r.rID AND r.userID FROM recipefavorites r WHERE r.rID = ? AND r.userID = ?");
                } else if (listname.equals("Do Not Show")) {
                    //checking that the specified user has the specified recipe in the Do Not Show list
                    ps = conn.prepareStatement("SELECT r.rID AND r.userID FROM recipedonotshow r WHERE r.rID = ? AND r.userID = ?");
                } else if (listname.equals("To Explore")) {
                    //checking that the specified user has the specified recipe in the To Explore list
                    ps = conn.prepareStatement("SELECT r.rID AND r.userID FROM recipetoexplore r WHERE r.rID =? AND r.userID = ?");
                } else if (listname.equals("Grocery")) {
                    //checking that the specified user has the specified recipe in the Grocery List
                    ps = conn.prepareStatement("SELECT g.recipeID AND g.userID FROM groceries g WHERE g.recipeID =? AND g.userID =?");
                }
                ps.setInt(1, dbids);
                ps.setInt(2, userID);
                rs = ps.executeQuery();
                //Did not exist in the specified Recipe list
                if(!rs.next()){
                    System.out.println("Cant delete what you dont have - recipe");
                    return false;
                }
                if (listname.equals("Favorites")) {
                    //checking that the specified user has the specified recipe in the Favorites list
                    ps = conn.prepareStatement("DELETE FROM recipefavorites WHERE rID = ? AND userID = ?");
                } else if (listname.equals("Do Not Show")) {
                    //checking that the specified user has the specified recipe in the Do Not Show list
                    ps = conn.prepareStatement("DELETE FROM recipedonotshow WHERE rID = ? AND userID = ?");
                } else if (listname.equals("To Explore")) {
                    //checking that the specified user has the specified recipe in the To Explore list
                    ps = conn.prepareStatement("DELETE FROM recipetoexplore  WHERE rID = ? AND userID = ?");
                } else if (listname.equals("Grocery")) {
                    //checking that the specified user has the specified recipe in the Grocery list
                    ps = conn.prepareStatement("DELETE FROM groceries  WHERE recipeID = ? AND userID = ?");
                }
                ps.setInt(1, dbids);
                ps.setInt(2, userID);
                ps.executeUpdate();
                System.out.println("about to return true");
                return true;
            }
            //for removing restaurants
            else {
                //checking if added to restaurant  Database in the past
                // finding the ID of recipe in the database by identifying the unique api recipe ID
                ps = conn.prepareStatement("SELECT r.placeID, r.restaurantID FROM restaurant r WHERE r.placeID = ?");
                ps.setString(1, ((RestaurantInfo) i).placeID);
                rs = ps.executeQuery();
                // cannot remove an item that has not been added
                if(!rs.next()){
                    //System.out.println("IM not supposed to be HERE ");
                    return false;
                }
                //storing the database ID for the restaurant to remove
                int dbids = rs.getInt("restaurantID");
                if (listname.equals("Favorites")) {
                    //checking that the specified user has the specified recipe in the Favorites list
                    ps = conn.prepareStatement("SELECT r.rID FROM restfavorites r WHERE r.rID = ? AND r.userID = ?");
                } else if (listname.equals("Do Not Show")) {
                    //checking that the specified user has the specified recipe in the Do Not Show list
                    ps = conn.prepareStatement("SELECT r.rID FROM restdonotshow r WHERE r.rID = ? AND r.userID = ?");
                } else if (listname.equals("To Explore")) {
                    //checking that the specified user has the specified recipe in the To Explore list
                    ps = conn.prepareStatement("SELECT r.rID FROM resttoexplore r WHERE r.rID = ? AND r.userID = ?");
                }
                ps.setInt(1, dbids);
                ps.setInt(2, userID);
                rs = ps.executeQuery();
                //Did not exist in the specified restaurant list
                if(!rs.next()){
                    //Cannot delete what you do not have restaurant edition
                    System.out.println(dbids + " " + userID);
                    return false;
                }
                if (listname.equals("Favorites")) {
                    //checking that the specified user has the specified recipe in the Favorites list
                    ps = conn.prepareStatement("DELETE FROM restfavorites WHERE rID = ? AND userID = ?");
                } else if (listname.equals("Do Not Show")) {
                    //checking that the specified user has the specified recipe in the Do Not Show list
                    ps = conn.prepareStatement("DELETE FROM restdonotshow WHERE rID = ? AND userID = ?");
                } else if (listname.equals("To Explore")) {
                    //checking that the specified user has the specified recipe in the To Explore list
                    ps = conn.prepareStatement("DELETE FROM resttoexplore WHERE rID = ? AND userID = ?");
                }
                ps.setInt(1, dbids);
                ps.setInt(2, userID);
                ps.executeUpdate();
                return true;
            }
        }catch(SQLException e){
            System.out.println("SQLException in function \"validate\"");
            e.printStackTrace();
        }
        return false;

    }



    public Boolean updateLists(int userID, Boolean add, String listname, Info i) {
        Boolean isRecipe = i.getClass().equals(RecipeInfo.class);
        if (add) {
            Boolean succ = addToList(userID, isRecipe, listname, i);
            //have to return true and not the actual value because executeUpdate returns before can return a bool
            return getLists(userID, listname).contains(i) && succ;
        } else {
            Boolean succ = removeFromList(userID, isRecipe, listname, i);
            //have to return true and not the actual value because executeUpdate returns before can return a bool
            return !getLists(userID, listname).contains(i) && succ;
        }
    }

    public ArrayList<Searches> getPrevSearch(int userID) {
        ArrayList<Searches> searchHistory = new ArrayList<Searches>();
        try {
            ps = conn.prepareStatement("SELECT p.userID, p.searchTerm, p.specradius, p.expectRes FROM previoussearch p WHERE p.userID = ?");
            ps.setInt(1, userID);
            rs = ps.executeQuery();
            while (rs.next()) {
                String searchTerm = rs.getString("searchTerm");
                int specifiedRadius = rs.getInt("specradius");
                int expectedResults = rs.getInt("expectRes");
                searchHistory.add(new Searches(searchTerm, specifiedRadius, expectedResults));
            }
            return searchHistory;
        }catch(SQLException e){
            System.out.println("SQLException in function \"validate\"");
            e.printStackTrace();
        }
        return searchHistory;
    }

    public Boolean addPrevSearch(int userID, String testSearch, int radius, int results) {
        try {
            ps = conn.prepareStatement("SELECT p.userID, p.searchTerm, p.specradius, p.expectRes FROM previoussearch p WHERE p.userID = ? AND p.searchTerm =? AND p.specradius= ? AND p.expectRes = ?");
            ps.setInt(1, userID);
            ps.setString(2, testSearch);
            ps.setInt(3, radius);
            ps.setInt(4, results);
            rs = ps.executeQuery();
            if (rs.next()) {
               return false;
            }
            ps = conn.prepareStatement("INSERT INTO previoussearch(userID, searchTerm, specradius, expectRes) VALUES(?,?,?,?)");
            ps.setInt(1, userID);
            ps.setString(2, testSearch);
            ps.setInt(3, radius);
            ps.setInt(4, results);
            ps.executeUpdate();
            return true;
        }catch(SQLException e){
            System.out.println("SQLException in function \"validate\"");
            e.printStackTrace();
        }
        return false;
    }
}
