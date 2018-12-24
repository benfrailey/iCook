/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package icook;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Ben Frailey
 */
public class MainModel {
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private ArrayList<Recipe> loadedRecipes = new ArrayList<>();
    private JSONArray recipeList = new JSONArray();
    private Time originalTime;

    public void initializeEvents(){
        JSONParser parser = new JSONParser();
        File file = new File("./recipes.json");
        try {
            file.createNewFile();
        } catch (IOException ex) {
            displayExceptionAlert(ex);
        }
        try (FileReader reader = new FileReader("./recipes.json")){
                
                ArrayList<Recipe> temp = new ArrayList<>();
                Object obj = parser.parse(reader);
                
                recipeList = (JSONArray) obj;
               
                recipeList.forEach(recipe -> {
                    recipes.add(parseObject((JSONObject) recipe));
                    
                });

            } catch (FileNotFoundException ex) {
                displayExceptionAlert(ex);
            } catch (IOException ex) { 
                displayExceptionAlert(ex);
            } catch (ParseException ex) { 
        } 
    }
    
    public HashMap<String, Integer> parseInstructions(Recipe recipe){
        HashMap<String, Integer> hashMap = new HashMap<>();
        for(String instruction : recipe.getInstructions()){
            if(instruction == null || instruction.isEmpty()) return null;

            StringBuilder sb = new StringBuilder();
            boolean found = false;
            for(char c : instruction.toCharArray()){
                if(Character.isDigit(c)){
                    sb.append(c);
                    found = true;
                } else if(found){
            // If we already found a digit before and this char is not a digit, stop looping
                    break;                
                }
            }
            
            if(!sb.toString().equals(""))
                hashMap.put(instruction, Integer.parseInt(sb.toString()));
            else if(instruction.contains("boil"))
                hashMap.put(instruction, 6);
            else
                hashMap.put(instruction, 0);
        }
        return hashMap;
    }
    
    public void addRecipe(String name, String ingredients, String instructions){
        String[] ingredientArray = ingredients.split("\n");
        String[] instructionArray = instructions.split("\n");
        
        ArrayList<String> ingredientList = new ArrayList<>();
        ArrayList<String> instructionList = new ArrayList<>();
        
        List<String> ingredientConverter = Arrays.asList(ingredientArray);
        List<String> instructionConverter = Arrays.asList(instructionArray);
        
        ingredientList.addAll(ingredientConverter);
        instructionList.addAll(instructionConverter);
        
        Recipe recipe = new Recipe(name, ingredientList, instructionList);
        
        //recipes.add(recipe);
        loadedRecipes.add(recipe);
        recipeList.add(recipe.convertToJSON());
        updateRecipes();
    }
    
    public void loadRecipes(ArrayList<String> recipeStrings){
        ArrayList<Recipe> newRecipes = new ArrayList<>();
        for(String recipeString : recipeStrings){
            for(Recipe recipe : recipes){
                if(recipeString.equals(recipe.getName())){
                    newRecipes.add(recipe);
                    break;
                }
            }
        }
        
        loadedRecipes.addAll(newRecipes);
    }
    
    public Text fixText(String textString){
        int length = textString.length();
        int count = 0;
        
        if(length <= 60){
            return new Text(textString);
        }
        
        else{
        String newText = "";
        for(int i = 0; i + 70 < length; i+= 70){
            newText = newText.concat("\n" + textString.substring(i, i + 70));
            count = i + 70;
        }
        newText = newText.concat("\n" + textString.substring(count));
        newText = newText.substring(1);
        return new Text(newText + ":");
        }
    }
    
    public HashMap<String, Time> finalizeInstructions(HashMap<Recipe, HashMap<String, Integer>> recipeMap, int hour, int minute){
        HashMap<String, Time> finalMap = new HashMap<>();
        originalTime = new Time(hour, minute);
        
        for(Recipe recipe : recipeMap.keySet()){
            Time time  = new Time(hour, minute);
            HashMap<String, Integer> instructionMap = recipeMap.get(recipe);
            ArrayList<String> instructionArray = recipe.getInstructions();
            Collections.reverse(instructionArray);
                
            for(String instruction : instructionArray){
                int instructionTime = instructionMap.get(instruction);
                
                time.subtractTime(instructionTime);
                finalMap.put(instruction, new Time(time.getHour(), time.getMinute()));
            }
            Collections.reverse(instructionArray);
        }
        return finalMap;
    }
    
    public ArrayList<String> sortTimes(HashMap<String, Time> finalMap){
        
        Collection<Time> timesCollection = finalMap.values();
        ArrayList<Time> times = new ArrayList<>();
        times.addAll(timesCollection);
        
        Set<String> instructionCollection = finalMap.keySet();
        ArrayList<String> instructions = new ArrayList<>();
        instructions.addAll(instructionCollection);           
        
        Collections.sort(times, (a, b) -> a.minutesFromOriginal(originalTime) < b.minutesFromOriginal(originalTime) ? -1 : a.minutesFromOriginal(originalTime) == b.minutesFromOriginal(originalTime) ? 0 : 1);

        Time[] timeArray = times.toArray(new Time[times.size()]);
        String[] instructionArray = new String[timeArray.length - 1];
           
        HashMap<Integer, String> sortedMap = new HashMap<>();
        for(String instruction : instructions){
           Time time = finalMap.get(instruction);
           
           int index = times.indexOf(time);
           sortedMap.put(index, instruction);
        }
        
        ArrayList<String> instructionList = new ArrayList<>();  
        
        for(int i = 0; i < sortedMap.size(); i++){
            instructionList.add(sortedMap.get(i));
        }
        Collections.reverse(instructionList);
        
        
        return instructionList;
    }
            
        
    
    public ArrayList<Recipe> getRecipes(){
        return recipes;
    }
    
    public ArrayList<Recipe> getLoadedRecipes(){
        return loadedRecipes;
    }
    
    public void updateRecipes(){
        FileWriter file;
                                
        try{
            file = new FileWriter("./recipes.json");
            file.write(recipeList.toJSONString());
            file.flush();
        } catch (IOException ex) {
            displayExceptionAlert(ex);
        }   
    }
    
    public void removeRecipe(Recipe recipe){
        loadedRecipes.remove(recipe);
        //recipeList.remove(recipe.convertToJSON());
    }
    
    public void removeRecipeFromMemory(ArrayList<String> recipeStrings){
        ArrayList<Recipe> removedRecipes = new ArrayList<>();
        for(String recipeString : recipeStrings){
            for(Recipe recipe : recipes){
                if(recipeString.equals(recipe.getName())){
                    removedRecipes.add(recipe);
                    break;
                }
            }
        }
        
        for(Recipe recipe : removedRecipes){
            loadedRecipes.remove(recipe);
            recipes.remove(recipe);
            recipeList.remove(recipe.convertToJSON());
        }
        
        updateRecipes();
    }
    
    public Recipe parseObject(JSONObject event){        
        JSONObject recipeObject = (JSONObject) event.get("recipe");
        Gson gson = new Gson();
        
        if(recipeObject != null){
            String name = (String) recipeObject.get("name");
            String json = (String) recipeObject.get("ingredients");
            ArrayList<String> ingredients = gson.fromJson(json, ArrayList.class);
            
            json = (String) recipeObject.get("instructions");
            ArrayList<String> instructions = gson.fromJson(json, ArrayList.class);
 
            return new Recipe(name, ingredients, instructions);
        }
        
        //returns null if recipeObject is null
        else return null;
    }
    
    public static void displayExceptionAlert(Exception ex){
        TextArea alertTextArea;
        
        Alert alert = new Alert(Alert.AlertType.ERROR); 
        alert.setTitle("Exception Dialog"); 
        alert.setHeaderText(ex.getClass().getCanonicalName());
        alert.setContentText(ex.getMessage());
        
        StringWriter sw = new StringWriter(); 
        PrintWriter pw = new PrintWriter(sw);
        
        ex.printStackTrace(pw);
        String exceptionText = sw.toString(); 
        
        
        Label label = new Label("The exception stacktrace was:"); 
        
        
        alertTextArea = new TextArea(exceptionText);
        alertTextArea.setEditable(true);
        alertTextArea.setWrapText(true);
        
        alertTextArea.setMaxWidth(Double.MAX_VALUE); 
        alertTextArea.setMaxHeight(Double.MAX_VALUE); 
        
        GridPane.setVgrow(alertTextArea, Priority.ALWAYS);
        GridPane.setHgrow(alertTextArea, Priority.ALWAYS);
        
        GridPane expContent = new GridPane(); 
        expContent.add(label, 0, 0); 
        expContent.add(alertTextArea, 0, 1); 
        
        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }
}