package servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import info.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "ListServlet", urlPatterns = "/Lists")
public class ListServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    //GET method used to fetch contents of a list
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        String listName = request.getParameter("list"); //See what list was requested
        PrintWriter respWriter = response.getWriter();
        Gson gson = new Gson();
        if(!listName.equals("Grocery") && !listName.equals("Favorites") && !listName.equals("To Explore") && !listName.equals("Do Not Show")) //Check if list is valid
        {
            respWriter.println(gson.toJson(new Message("Invalid List!")));
            respWriter.close();
            return;
        }
        List<Info> list = (List<Info>)session.getAttribute(listName); //Cast stored list to correct type

        if(listName.equals("Grocery")) {
            List<String> groceryList = new ArrayList<>();
            for (Info i : list) {
                for(int l =0 ; l < ((RecipeInfo)i).ingredients.size();l++ ){
                    groceryList.add((((RecipeInfo) i).checked.get(l)?"C":"N")+((RecipeInfo) i).ingredients.get(l));
                }
            }

            respWriter.println(gson.toJson(new Message(listName,groceryList))); //convert to JSON before sending it to the response
            respWriter.close();
            return;
        }

        respWriter.println(gson.toJson(new Message(listName,list))); //convert to JSON before sending it to the response
        respWriter.close();
    }

    //POST method used to add and remove items from a list
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        String reqBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator())); //Java 8 magic to collect all lines from a BufferedReadder, in this case the request.
        Gson gson = new Gson();
        PrintWriter respWriter = response.getWriter();
        try
        {
            Message reqMessage = gson.fromJson(reqBody, Message.class); //Parse outer Message object from JSON
            Message reqListAndItem = gson.fromJson((String)reqMessage.body, Message.class); //Parse inner Message object from json
            String listName = reqListAndItem.header; //Get name of list to modify from the inner Message

            if(!listName.equals("Grocery") && !listName.equals("Favorites") && !listName.equals("To Explore") && !listName.equals("Do Not Show")) //Check validity
                throw new Exception("Invalid list name." + listName);
            String infoJson = (String)reqListAndItem.body; //Get Info object of item to add/remove as a JSON string

            //Interact with the raw JSON to determine the type of the object via unique fields
            JsonObject info = new JsonParser().parse(infoJson).getAsJsonObject();
            Type infoType;
            // for implementing the database we would not need the next three lines
            if(info.has("prepTime")) infoType = RecipeInfo.class;
            else if(info.has("placeID")) infoType = RestaurantInfo.class;
            else throw new Exception("Unknown item type.");

            Info item = gson.fromJson(infoJson, infoType); //Parse Info object from JSON
            //we would not need the following line
            List<Info> list = (List<Info>) session.getAttribute(listName); //Get the requested list from session
            //Switch on requested action
            Database db = new Database();
            int userID = (int) session.getAttribute("userID");
            switch(reqMessage.header)
            {
                case "addItem":
                    // for implementing the db
                    db.updateLists(userID, true, listName, item);
                    if(!list.contains(item)) list.add(item); //Check this is a new item for the list before adding
                    respWriter.println(gson.toJson(new Message("Added to list "+listName)));
                    break;

                case "removeItem":
                    // for implementing the db
                    db.updateLists(userID, false, listName, item);
                    list.remove(item);
                    respWriter.println(gson.toJson(new Message("Removed from list "+listName)));
                    break;
                case "resetLists":
                    session.invalidate(); //Note: This is for debuggin only; the page will break if this is called and a new search is not immediately made
                    break;
                case "checkGrocery":
                    System.out.println("Checking");
                    String ingred = item.name;
                    System.out.println(ingred);
                    Boolean flag = false;
                    for(int k =0; k < list.size() && !flag; k++){
                        for(int m =0; m < ((RecipeInfo)list.get(k)).ingredients.size(); m++){
                            if(((RecipeInfo) list.get(k)).ingredients.get(m).equals(ingred)){
                                System.out.println("did something");
                                db.updateLists(userID, false, "Grocery", list.get(k));
                                ((RecipeInfo) list.get(k)).checked.set(m, true);
                                db.updateLists(userID, true, "Grocery", list.get(k));
                                flag = true;
                                break;
                            }
                        }
                    }
                    session.setAttribute("Grocery", list);
                    respWriter.println(new Gson().toJson(new Message("Checked")));
                    break;
                case "uncheckGrocery":
                    String ingrid = item.name;
                    Boolean flage = false;
                    for(int k =0; k < list.size() && !flage; k++){
                        for(int m =0; m < ((RecipeInfo)list.get(k)).ingredients.size(); m++){
                            if(((RecipeInfo) list.get(k)).ingredients.get(m).equals(ingrid)){
                                db.updateLists(userID, false, "Grocery", list.get(k));
                                ((RecipeInfo) list.get(k)).checked.set(m, false);
                                db.updateLists(userID, true, "Grocery", list.get(k));
                                flage = true;
                                break;
                            }
                        }
                    }
                    session.setAttribute("Grocery", list);
                    respWriter.println(new Gson().toJson(new Message("Unchecked")));
                    break;
                case "reorderList":
                    RecipeInfo ri = (RecipeInfo) item;
                    String order = ri.name;
                    int position = ri.pos;
                    Info mover = list.get(position);
                    if (order.equals("Up")){
                        list = SortLists.moveItemUp(list, mover);
                    } else if (order.equals("Down")){
                        list = SortLists.moveItemDown(list, mover);
                    }
                    session.setAttribute(listName, list);
                    db.changeOrder(userID, listName, order.equals("Up"), position);
                    respWriter.println(gson.toJson(new Message("Moved item " + order)));
                    break;
                default:
                    throw new Exception("Invalid action.");
            }
        } catch(Exception e) { //Handle exceptions
            e.printStackTrace();
            respWriter.println(gson.toJson(new Message("Invalid Response!\n"+e.getMessage())));
            respWriter.close();
        }
        respWriter.close();
    }
}
