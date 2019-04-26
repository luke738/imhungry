package servlet;

import com.google.gson.Gson;
import info.Info;
import info.Searches;
import info.RecipeInfo;
import info.RestaurantInfo;

import java.sql.*;
import java.util.*;

public class Database
{
    private Connection conn;
    private Statement st;
    private PreparedStatement ps;
    private ResultSet rs;
    private DatabaseHelper helper;

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

        helper = new DatabaseHelper(ps, rs, conn);

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
    public void changeOrder(int userID, String listname, Boolean isUp, int position){
        move(userID, listname, position, isUp);

    }
    private int getrecipeID(int position, String listname, int userID){
        try {
            if (listname.equals("Favorites")) {
                ps = conn.prepareStatement("SELECT rr.rID FROM imhungry.recipefavorites rr  WHERE rr.userID = ?  AND rr.pos = ?");
            } else if (listname.equals("Do Not Show")) {
                ps = conn.prepareStatement("SELECT rr.rID FROM imhungry.recipedonotshow rr  WHERE rr.userID = ?  AND rr.pos = ?");
            } else if (listname.equals("To Explore")) {
                ps = conn.prepareStatement("SELECT rr.rID FROM imhungry.recipetoexplore rr  WHERE rr.userID = ?  AND rr.pos = ?");
            }
            ps.setInt(1, userID);
            ps.setInt(2, position);
            rs = ps.executeQuery();
            while(rs.next()) {
                return rs.getInt("rID");
            }
            return -1;
        }
        catch (SQLException e) {
            System.out.println("SQLException in function \"validate\"");
            e.printStackTrace();
        }
        return -100;
    }

    private int getrestID(int position, String listname, int userID){
        try {
            if (listname.equals("Favorites")) {
                ps = conn.prepareStatement("SELECT rr.rID FROM imhungry.restfavorites rr  WHERE rr.userID = ?  AND rr.pos = ?");
            } else if (listname.equals("Do Not Show")) {
                ps = conn.prepareStatement("SELECT rr.rID FROM imhungry.restdonotshow rr  WHERE rr.userID = ?  AND rr.pos = ?");
            } else if (listname.equals("To Explore")) {
                ps = conn.prepareStatement("SELECT rr.rID FROM imhungry.resttoexplore rr  WHERE rr.userID = ?  AND rr.pos = ?");
            }
            ps.setInt(1, userID);
            ps.setInt(2, position);
            rs = ps.executeQuery();
            //meaning it is not a restaurant
            while(rs.next()) {
                return rs.getInt("rID");
            }
            return -10;
        }
        catch (SQLException e) {
            System.out.println("SQLException in function \"validate\"");
            e.printStackTrace();
        }
        return -100;
    }

    private int updatePos(int position, String listname, int userID, int rID, Boolean isRecipe){
        try {
            if(listname.equals("Favorites") && isRecipe) {
                ps = conn.prepareStatement("UPDATE recipefavorites rr SET rr.pos = ? WHERE rr.userID = ? AND rr.rID = ?");
            }
            else if(listname.equals("Do Not Show")&& isRecipe) {
                ps = conn.prepareStatement("UPDATE recipedonotshow rr SET rr.pos = ? WHERE rr.userID = ? AND rr.rID = ?");
            }
            else if(listname.equals("To Explore")&& isRecipe) {
                ps = conn.prepareStatement("UPDATE recipetoexplore rr SET rr.pos = ? WHERE rr.userID = ? AND rr.rID = ?");
            }
            if(listname.equals("Favorites") && !isRecipe) {
                ps = conn.prepareStatement("UPDATE restfavorites rr SET rr.pos = ? WHERE rr.userID = ? AND rr.rID = ?");
            }
            else if(listname.equals("Do Not Show")&& !isRecipe) {
                ps = conn.prepareStatement("UPDATE restdonotshow rr SET rr.pos = ? WHERE rr.userID = ? AND rr.rID = ?");
            }
            else if(listname.equals("To Explore")&& !isRecipe) {
                ps = conn.prepareStatement("UPDATE resttoexplore rr SET rr.pos = ? WHERE rr.userID = ? AND rr.rID = ?");
            }
            ps.setInt(1, position);
            ps.setInt(2, userID);
            ps.setInt(3, rID);
            ps.executeUpdate();

        }
        catch (SQLException e) {
            System.out.println("SQLException in function \"validate\"");
            e.printStackTrace();
        }
        return -100;
    }


    public void move(int userID, String listname, int posit, Boolean moveUp){
        //position of the one affected the one we are moving
        int position;
        if(moveUp) {
            position = posit - 1;
        }
        else { //moveDown
            position = posit + 1;
        }
        // variable for the one above the info item we are moving
        Boolean isRecipe = false;
        // variable for the info item we are moving
        Boolean isRecipe2 = true;
        //first try restaurants
        //rID is the rID for the one we are adjusting because of the move
        int rID = getrestID(position, listname, userID);
        //if -10 then it is a recipe
        if(rID == -10) {
            isRecipe = true;
            rID = getrecipeID(position, listname, userID);
        }

        int movingrID = getrecipeID(posit, listname, userID);
        if(movingrID == -1) {
            isRecipe2 = false;
            movingrID = getrestID(posit, listname, userID);
        }

        // to update the position of the info item above the moving one before the move
        System.out.println("pos1: " + posit +"  pos2: " + position + " rID"+ rID + " movingID:" + movingrID + " movingUp" + moveUp);
        updatePos(posit, listname, userID, rID, isRecipe);
        // now update the position of the moving one after the move
        updatePos(position, listname, userID, movingrID, isRecipe2);
    }


    public ArrayList<Info> getLists(int userID, String listname) {
        ArrayList<Info> pList = new ArrayList<Info>();
        try {
            if(listname.equals("Favorites")) {
                ps = conn.prepareStatement("SELECT rec.rID, rec.userID, r.recipeIDapi, r.prepTime, r.rating, r.CookTime, r.ingredient, r.instructions, r.imageURL, r.rname, rec.rID, rec.pos FROM recipefavorites rec JOIN recipe r WHERE rec.userID=? AND rec.rID = r.recipID");
            }
            else if(listname.equals("To Explore")) {
                ps = conn.prepareStatement("SELECT rec.rID, rec.userID, r.recipeIDapi, r.prepTime, r.rating, r.CookTime, r.ingredient, r.instructions, r.imageURL, r.rname, rec.rID, rec.pos FROM recipetoexplore rec JOIN recipe r WHERE rec.userID=? AND rec.rID = r.recipID");
            }
            else if(listname.equals("Do Not Show")) {
                ps = conn.prepareStatement("SELECT rec.rID, rec.userID, r.recipeIDapi, r.prepTime, r.rating, r.CookTime, r.ingredient, r.instructions, r.imageURL, r.rname, rec.rID, rec.pos FROM recipedonotshow rec JOIN recipe r WHERE rec.userID=? AND rec.rID = r.recipID");
            }
            else if(listname.equals("Grocery")){
                ps = conn.prepareStatement("SELECT grow.grocID, grow.userID, grow.checked, grow.recipeID AS 'rID', r.recipeIDapi, r.prepTime, r.rating, r.CookTime, r.ingredient, r.instructions, r.imageURL, r.rname FROM groceries grow JOIN recipe r WHERE grow.userID=? AND grow.recipeID = r.recipID");
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
                RecipeInfo p;
                if(listname.equals("Grocery")){
                    String checkedString   = rs.getString("checked");
                    Boolean[] checkedArray = gson.fromJson(checkedString, Boolean[].class);
                    ArrayList<Boolean> checked = new ArrayList<Boolean>(Arrays.asList(checkedArray));
                    p = new RecipeInfo(rname, rating, recipeIDapi, prepTime, cookTime, ingredients, instructions,imageurl, dbid, checked);
                }
                else {
                    int pos = rs.getInt("pos");
                    p = new RecipeInfo(rname, rating, recipeIDapi, prepTime, cookTime, ingredients, instructions,imageurl, dbid, pos);
                }
                pList.add(p);
            }

            //return grocery list after go thru recipes because no restaurants in grocery list
            if(listname.equals("Grocery")){
                return pList;
            }
            if(listname.equals("Favorites")) {
                ps = conn.prepareStatement("SELECT rest.userID, rest.rID, r.rname, r.rating, r.placeID, r.address, r.priceL, r.driveTimeT, r.driveTimeV, r.phone, r.url,  rest.rID, rest.pos FROM restfavorites rest JOIN restaurant r WHERE rest.userID=? AND rest.rID = r.restaurantID");
            }
            else if(listname.equals("To Explore")) {
                ps = conn.prepareStatement("SELECT rest.userID, rest.rID, r.rname, r.rating, r.placeID, r.address, r.priceL, r.driveTimeT, r.driveTimeV, r.phone, r.url, rest.rID, rest.pos FROM resttoexplore rest JOIN restaurant r WHERE rest.userID=? AND rest.rID = r.restaurantID");
            }
            else if(listname.equals("Do Not Show")) {
                ps = conn.prepareStatement("SELECT rest.userID, rest.rID, r.rname, r.rating, r.placeID, r.address, r.priceL, r.driveTimeT, r.driveTimeV, r.phone, r.url, rest.rID, rest.pos FROM restdonotshow rest JOIN restaurant r WHERE rest.userID=? AND rest.rID = r.restaurantID");
            }

            ps.setInt(1, userID);
            rs = ps.executeQuery();
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
                int pos = rs.getInt("pos");
                String phone = rs.getString("phone");
                String url = rs.getString("url");
                RestaurantInfo p = new RestaurantInfo(restname, rating, placeID, restaddress, price,driveTimeT, driveTimeV, phone, url, dbid, pos);
                pList.add(p);
            }
            for(int i = 0; i < pList.size(); i++){
                System.out.println(pList.get(i).name +" "+ pList.get(i).pos);
            }
            System.out.println();
            // reorder based on position
            pList.sort(Comparator.comparingInt((Info i) -> i.pos));
            for(int i = 0; i < pList.size(); i++){
                System.out.println(pList.get(i).name  +" "+ pList.get(i).pos);
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
                if(listname.equals("Grocery")){

                }
                else if(rs.next()){
                    System.out.println("NOPE");
                    return false;
                }

                int highestPos = -1;
                if (listname.equals("Favorites")) {
                    highestPos = findHighestPos("Favorites", userID);
                    //checking that the specified user has the specified recipe in the Favorites list
                    ps = conn.prepareStatement("INSERT INTO recipefavorites(rID, userid, pos) VALUES(?,?,?)");
                } else if (listname.equals("Do Not Show")) {
                    highestPos = findHighestPos("Do Not Show", userID);
                    //checking that the specified user has the specified recipe in the Donotshow list
                    ps = conn.prepareStatement("INSERT INTO recipedonotshow(rID, userid, pos) VALUES(?,?,?)");
                } else if (listname.equals("To Explore")) {
                    highestPos = findHighestPos("To Explore", userID);
                    //checking that the specified user has the specified recipe in the to explore list
                    ps = conn.prepareStatement("INSERT INTO recipetoexplore(rID, userid, pos) VALUES(?,?,?)");
                } else if (listname.equals("Grocery")) {
                    //checking that the specified user has the specified recipe in the to explore list
                    ps = conn.prepareStatement("INSERT INTO groceries(recipeID, userID, checked) VALUES(?,?,?)");
                }
                ps.setInt(1, dbids);
                ps.setInt(2, userID);
                if (!listname.equals("Grocery")) {
                    ps.setInt(3, highestPos + 1);
                }
                else{
                    ps.setString(3, new Gson().toJson(((RecipeInfo) i).checked));
                }
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
                    return false;
                }
                int highestPos = -1;
                if (listname.equals("Favorites")) {
                    highestPos = findHighestPos("Favorites", userID);
                    ps = conn.prepareStatement("INSERT INTO restfavorites(rID, userid, pos) VALUES(?,?,?)");
                } else if (listname.equals("Do Not Show")) {
                    highestPos = findHighestPos("Do Not Show", userID);
                    ps = conn.prepareStatement("INSERT INTO restdonotshow(rID, userid, pos) VALUES(?,?,?)");
                } else if (listname.equals("To Explore")) {
                    highestPos = findHighestPos("To Explore", userID);
                    ps = conn.prepareStatement("INSERT INTO resttoexplore(rID, userid, pos) VALUES(?,?,?)");
                }
                ps.setInt(1, dbids);
                ps.setInt(2, userID);
                ps.setInt(3, highestPos+1);
                ps.executeUpdate();
                return true;
            }

        }catch(SQLException e){
            System.out.println("SQLException in function \"validate\"");
            e.printStackTrace();
        }
        return false;

    }

    public int findHighestPos(String listname, int userID){
        int highestPos = -1;
        try {
            // get the positions of recipes
            if (listname.equals("Favorites")) {
                ps = conn.prepareStatement("SELECT r.pos FROM recipefavorites r WHERE r.userID= ?");
            } else if (listname.equals("Do Not Show")) {
                ps = conn.prepareStatement("SELECT r.pos FROM recipedonotshow r WHERE r.userID= ?");
            } else if (listname.equals("To Explore")) {
                ps = conn.prepareStatement("SELECT r.pos FROM recipetoexplore r WHERE r.userID= ?");
            }
            ps.setInt(1, userID);
            rs = ps.executeQuery();
            // find the highest position in recipes
            while (rs.next()){
                int pos = rs.getInt("pos");
                if (pos > highestPos) {
                    highestPos = pos;
                }
            }
            // get the positions of restaurants
            if (listname.equals("Favorites")) {
                ps = conn.prepareStatement("SELECT r.pos FROM restfavorites r WHERE r.userID= ?");
            } else if (listname.equals("Do Not Show")) {
                ps = conn.prepareStatement("SELECT r.pos FROM restdonotshow r WHERE r.userID= ?");
            } else if (listname.equals("To Explore")) {
                ps = conn.prepareStatement("SELECT r.pos FROM resttoexplore r WHERE r.userID= ?");
            }
            ps.setInt(1, userID);
            rs = ps.executeQuery();
            // find the highest position in restaurants
            while (rs.next()) {
                int pos = rs.getInt("pos");
                if (pos > highestPos){
                    highestPos = pos;
                }
            }
            System.out.println("HIGH: "+highestPos);
            return highestPos;
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
                int dbids = 0;
                if(rs.next()) {
                    dbids = rs.getInt("recipID");
                }
                else {
                    return false;
                }
                if (listname.equals("Favorites")) {
                    //checking that the specified user has the specified recipe in the Favorites list
                    ps = conn.prepareStatement("SELECT r.rID, r.userID, r.pos FROM recipefavorites r WHERE r.rID = ? AND r.userID = ?");
                } else if (listname.equals("Do Not Show")) {
                    //checking that the specified user has the specified recipe in the Do Not Show list
                    ps = conn.prepareStatement("SELECT r.rID, r.userID, r.pos FROM recipedonotshow r WHERE r.rID = ? AND r.userID = ?");
                } else if (listname.equals("To Explore")) {
                    //checking that the specified user has the specified recipe in the To Explore list
                    ps = conn.prepareStatement("SELECT r.rID, r.userID, r.pos FROM recipetoexplore r WHERE r.rID = ? AND r.userID = ?");
                } else if (listname.equals("Grocery")) {
                    //checking that the specified user has the specified recipe in the Grocery List
                    ps = conn.prepareStatement("SELECT g.recipeID, g.userID FROM groceries g WHERE g.recipeID = ? AND g.userID = ?");
                }
                ps.setInt(1, dbids);
                ps.setInt(2, userID);
                rs = ps.executeQuery();
                int pos = -111;
                //Did not exist in the specified Recipe list
                if(!listname.equals("Grocery")){
                    if (rs.next()) {
                        pos = rs.getInt("pos");
                    }
                    else {
                        System.out.println("Cant delete what you dont have - recipe");
                        return false;
                    }
                }
                if (listname.equals("Favorites")) {
                    //checking that the specified user has the specified recipe in the Favorites list
                    ps = conn.prepareStatement("DELETE FROM recipefavorites WHERE rID = ? AND userID = ?");
                } else if (listname.equals("Do Not Show")) {
                    //checking that the specified user has the specified recipe in the Do Not Show list
                    ps = conn.prepareStatement("DELETE FROM recipedonotshow WHERE rID = ? AND userID = ?");
                } else if (listname.equals("To Explore")) {
                    //checking that the specified user has the specified recipe in the To Explore list
                    ps = conn.prepareStatement("DELETE FROM recipetoexplore WHERE rID = ? AND userID = ?");
                } else if (listname.equals("Grocery")) {
                    //checking that the specified user has the specified recipe in the Grocery list
                    ps = conn.prepareStatement("DELETE FROM groceries WHERE recipeID = ? AND userID = ?");

                }
                ps.setInt(1, dbids);
                ps.setInt(2, userID);
                if (listname.equals("Grocery")){
                    System.out.println(ps);
                }
                ps.executeUpdate();
                // update indices
                if (!listname.equals("Grocery") && findHighestPos(listname, userID)> pos){
                    // update the indices of elements after element that is deleted
                    updateIndicesAfterRemove(listname, pos, userID);
                }
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
                if (!rs.next()) {
                    return false;
                }
                //storing the database ID for the restaurant to remove
                int dbids = rs.getInt("restaurantID");
                if (listname.equals("Favorites")) {
                    //checking that the specified user has the specified recipe in the Favorites list
                    ps = conn.prepareStatement("SELECT r.rID, r.pos FROM restfavorites r WHERE r.rID = ? AND r.userID = ?");
                } else if (listname.equals("Do Not Show")) {
                    //checking that the specified user has the specified recipe in the Do Not Show list
                    ps = conn.prepareStatement("SELECT r.rID, r.pos FROM restdonotshow r WHERE r.rID = ? AND r.userID = ?");
                } else if (listname.equals("To Explore")) {
                    //checking that the specified user has the specified recipe in the To Explore list
                    ps = conn.prepareStatement("SELECT r.rID, r.pos FROM resttoexplore r WHERE r.rID = ? AND r.userID = ?");
                }
                ps.setInt(1, dbids);
                ps.setInt(2, userID);
                rs = ps.executeQuery();
                int position = 0;
                int rID = 0;
                if (rs.next()) {
                    position = rs.getInt("pos");
                    rID = rs.getInt("rID");
                }
                //Did not exist in the specified restaurant list
                else{
                // Cannot delete what you do not have restaurant edition
                    System.out.println(dbids + " " + userID);
                    return false;
                }
                if (listname.equals("Favorites")) {
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
                // update indices
                if(findHighestPos(listname, userID)> position){
                    updateIndicesAfterRemove(listname, position, userID);
                }
                ArrayList<Info> updateChecker = getLists(userID, listname);
                for (int k = 0; k < updateChecker.size(); k++) {
                    System.out.println("HERE " + updateChecker.get(k).name + " " + updateChecker.get(k).pos);
                }

                return true;

            }
        }catch(SQLException e){
            System.out.println("SQLException in function \"validate\"");
            e.printStackTrace();
        }
        return false;

    }

    private void updateIndicesAfterRemove(String listname, int pos, int userID) {
        try {
            String actualName = "";
            if (listname.equals("Favorites")){
                actualName = "favorites";
            } else if (listname.equals("Do Not Show")){
                actualName = "donotshow";
            } else if (listname.equals("To Explore")){
                actualName = "toexplore";
            }
            if (listname.equals("Favorites")) {
                ps = conn.prepareStatement("SELECT r.rID, r.pos FROM recipefavorites r WHERE r.userID= ? AND r.pos> ?");
            } else if (listname.equals("Do Not Show")) {
                ps = conn.prepareStatement("SELECT r.rID, r.pos FROM recipedonotshow r WHERE r.userID= ? AND r.pos> ?");
            } else if (listname.equals("To Explore")) {
                ps = conn.prepareStatement("SELECT r.rID, r.pos FROM recipetoexplore r WHERE r.userID= ? AND r.pos> ?");
            }
            ps.setInt(1, userID);
            ps.setInt(2, pos);
            rs = ps.executeQuery();
            int counter =0;
            ArrayList<Integer> allRID = new ArrayList<>();
            ArrayList<Integer> allpos = new ArrayList<>();
            while(rs.next()) {
                int riD = rs.getInt("rID");
                int position = rs.getInt("pos");
                allRID.add(riD);
                allpos.add(position);
                counter++;
            }
            for(int i = 0; i < counter; i++){
                ps = conn.prepareStatement("UPDATE recipe"+ actualName + "r SET r.pos = ? WHERE r.pos = ? AND r.rID =? AND r.userID = ?");
                ps.setInt(1, allpos.get(i)-1);
                ps.setInt(2, allpos.get(i));
                ps.setInt(3, allRID.get(i));
                ps.setInt(4, userID);
                ps.executeUpdate();
            }
            if (listname.equals("Favorites")) {
                ps = conn.prepareStatement("SELECT * FROM restfavorites r WHERE r.userID= ? AND r.pos> ?");
            } else if (listname.equals("Do Not Show")) {
                ps = conn.prepareStatement("SELECT * FROM restdonotshow r WHERE r.userID= ? AND r.pos> ?");
            } else if (listname.equals("To Explore")) {
                ps = conn.prepareStatement("SELECT * FROM resttoexplore r WHERE r.userID= ? AND r.pos>?");
            }
            counter =0;
            ArrayList<Integer> restallRID = new ArrayList<>();
            ArrayList<Integer> restallpos = new ArrayList<>();
            ps.setInt(1, userID);
            ps.setInt(2, pos);
            rs = ps.executeQuery();
            while(rs.next()) {
                int position = rs.getInt("pos");
                int riD = rs.getInt("rID");
                restallRID.add(riD);
                restallpos.add(position);
                counter++;
                System.out.println(counter);
            }
            for(int i = 0; i < counter; i++){
                ps = conn.prepareStatement("UPDATE rest"+ actualName + " r SET r.pos = ? WHERE r.pos = ? AND r.rID = ? AND r.userID=?");
                System.out.println("I AM HEREEEEEEEEEE for real for real");
                ps.setInt(1, restallpos.get(i)-1);
                ps.setInt(2, restallpos.get(i));
                ps.setInt(3, restallRID.get(i));
                ps.setInt(4, userID);
                ps.executeUpdate();
            }

        } catch(SQLException e){
            System.out.println("SQLException in function \"validate\"");
            e.printStackTrace();
        }

    }


    public Boolean updateLists(int userID, Boolean add, String listname, Info i) {
        Boolean isRecipe = i.getClass().equals(RecipeInfo.class);
        if (add) {
            Boolean succ = addToList(userID, isRecipe, listname, i);
            //have to return true and not the actual value because executeUpdate returns before can return a bool
            System.out.println("succ " + succ);
            return getLists(userID, listname).contains(i) && succ;
        } else {
            Boolean succ = removeFromList(userID, isRecipe, listname, i);
            //have to return true and not the actual value because executeUpdate returns before can return a bool
            if (listname.equals("Grocery")){
                System.out.println("succ: " + succ + " removed: " + !getLists(userID, listname).contains(i));
            }
            return !getLists(userID, listname).contains(i) && succ;
        }
    }

    public ArrayList<Searches> getPrevSearch(int userID) {
        ArrayList<Searches> searchHistory = new ArrayList<Searches>();
        try {
            ps = conn.prepareStatement("SELECT p.userID, p.searchTerm, p.url, p.specradius, p.expectRes FROM previoussearch p WHERE p.userID = ?");
            ps.setInt(1, userID);
            rs = ps.executeQuery();
            while (rs.next()) {
                String searchTerm = rs.getString("searchTerm");
                int specifiedRadius = rs.getInt("specradius");
                int expectedResults = rs.getInt("expectRes");
                String urlString = rs.getString("url");
                String[] urlArray = new Gson().fromJson(urlString, String[].class);
                ArrayList<String> url;
                if(urlArray!= null) {
                    url = new ArrayList<>(Arrays.asList(urlArray));
                }
                else {
                    url = null;
                }
                searchHistory.add(new Searches(searchTerm, specifiedRadius, expectedResults, url));
            }
            return searchHistory;
        }catch(SQLException e){
            System.out.println("SQLException in function \"validate\"");
            e.printStackTrace();
        }
        return searchHistory;
    }

    public Boolean addPrevSearch(int userID, String testSearch, int radius, int results, ArrayList<String> url) {
        try {
            ps = conn.prepareStatement("SELECT p.userID, p.searchTerm, p.specradius,p.url, p.expectRes FROM previoussearch p WHERE p.userID = ? AND p.searchTerm =? AND p.specradius= ? AND p.expectRes = ?");
            ps.setInt(1, userID);
            ps.setString(2, testSearch);
            ps.setInt(3, radius);
            ps.setInt(4, results);
            rs = ps.executeQuery();
            if (rs.next()) {
               return false;
            }
            ps = conn.prepareStatement("INSERT INTO previoussearch(userID, searchTerm, specradius, expectRes, url) VALUES(?,?,?,?,?)");
            ps.setInt(1, userID);
            ps.setString(2, testSearch);
            ps.setInt(3, radius);
            ps.setInt(4, results);
            ps.setString(5, new Gson().toJson(url));
            ps.executeUpdate();
            return true;
        }catch(SQLException e){
            System.out.println("SQLException in function \"validate\"");
            e.printStackTrace();
        }
        return false;
    }
}
