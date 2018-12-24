/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package icook;

import com.google.gson.Gson;
import java.util.ArrayList;
import org.json.simple.JSONObject;

/**
 *
 * @author Ben Frailey
 */
public class Recipe {
    private String name;
    private ArrayList<String> ingredients;
    private ArrayList<String> instructions;
    
    public Recipe(String name, ArrayList<String> ingredients, ArrayList<String> instructions){
        this.name = name;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }
    
    public String getName(){
        return name;
    }
    public ArrayList<String> getIngredients(){
        return ingredients;
    }
    
    public ArrayList<String> getInstructions(){
        return instructions;
    }
    
    public JSONObject convertToJSON(){
        JSONObject recipeInfo = new JSONObject();
        Gson gson = new Gson();
        
        recipeInfo.put("name", name);
        recipeInfo.put("ingredients", gson.toJson(ingredients));
        recipeInfo.put("instructions", gson.toJson(instructions));

        
        JSONObject recipeObject = new JSONObject();
        recipeObject.put("recipe", recipeInfo);
        
        return recipeObject;
    }
}