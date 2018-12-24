/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icook;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author benfrailey
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    AnchorPane vizPane;
    
    @FXML
    VBox recipeVBox;
    
    @FXML
    Button createSchedule;
        
    private MainModel model = new MainModel();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        model.initializeEvents();
        update(model.getLoadedRecipes());
        vizPane.prefHeightProperty().bind(recipeVBox.heightProperty());
        vizPane.prefWidthProperty().bind(recipeVBox.widthProperty());
    }    
    
    @FXML
    private void handleAddRecipeAction(ActionEvent event) throws IOException {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Add Recipe...");
//        alert.setHeaderText("Select whether you're copy/pasting or uploading a picture");
//        alert.setContentText("Choose your option.");
//                
//        //user chooses to add either a festival or a concert
//        ButtonType buttonTypeOne = new ButtonType("Text");
//        ButtonType buttonTypeTwo = new ButtonType("Image");
//        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
//
//        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
//        
//        //adds either a concert or festival based on user input
//        Optional<ButtonType> result = alert.showAndWait();
//        if (result.get() == buttonTypeOne){
//            enterRecipe();
//        } else if (result.get() == buttonTypeTwo) {
//            readImage();
//        } else {
//            
//    // ... user chose CANCEL or closed the dialog
//        }
        enterRecipe();
    }
    
    @FXML
    public void handleLoadRecipeAction(ActionEvent event){
        Dialog<ButtonType> dialog = new Dialog<>();
        ArrayList<Recipe> recipes = model.getRecipes();
        VBox recipeVBox = new VBox();
        
        //formats input window
        dialog.setTitle("Load Recipe");
        dialog.setHeaderText("Select a recipe or recipes below and press accept to load a recipe.");
        
        ButtonType addButtonType = new ButtonType("Add Recipe(s)", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        for(Recipe recipe : recipes){
            recipeVBox.getChildren().add(new CheckBox(recipe.getName()));
        }

        //disables addbutton until final field is filled in

        GridPane grid = new GridPane();
        grid.add(recipeVBox, 0, 0);
        dialog.getDialogPane().setContent(grid);
        
        //displays dialog
        Optional<ButtonType> result = dialog.showAndWait();
        
        //tests result if user didn't press cancel
        if(result.get() != ButtonType.CANCEL){
            //if a field is blank gives error
            Iterator<Node> iterator = recipeVBox.getChildren().iterator();
            ArrayList<String> checkBoxNames = new ArrayList<>();
            while(iterator.hasNext()){
                CheckBox checkBox = (CheckBox) iterator.next();
                if(checkBox.isSelected()){
                    checkBoxNames.add(checkBox.getText());
                }
            }
            model.loadRecipes(checkBoxNames);
            update(model.getLoadedRecipes());
        }
    }
    
    @FXML
    public void handleRemoveRecipeAction(ActionEvent event){
        Dialog<ButtonType> dialog = new Dialog<>();
        ArrayList<Recipe> recipes = model.getRecipes();
        VBox recipeVBox = new VBox();
        
        //formats input window
        dialog.setTitle("Remove Recipe");
        dialog.setHeaderText("Select a recipe or recipes below and press accept to remove it from memory.");
        
        ButtonType addButtonType = new ButtonType("Remove Recipe(s)", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        for(Recipe recipe : recipes){
            recipeVBox.getChildren().add(new CheckBox(recipe.getName()));
        }

        //disables addbutton until final field is filled in

        GridPane grid = new GridPane();
        grid.add(recipeVBox, 0, 0);
        dialog.getDialogPane().setContent(grid);
        
        //displays dialog
        Optional<ButtonType> result = dialog.showAndWait();
        
        //tests result if user didn't press cancel
        if(result.get() != ButtonType.CANCEL){
            //if a field is blank gives error
            Iterator<Node> iterator = recipeVBox.getChildren().iterator();
            ArrayList<String> checkBoxNames = new ArrayList<>();
            while(iterator.hasNext()){
                CheckBox checkBox = (CheckBox) iterator.next();
                if(checkBox.isSelected()){
                    checkBoxNames.add(checkBox.getText());
                }
            }
            model.removeRecipeFromMemory(checkBoxNames);
            update(model.getLoadedRecipes());
        }
    }
    
    @FXML
    public void handleCreateScheduleAction(ActionEvent event) throws IOException{
        ArrayList<Recipe> recipes = model.getLoadedRecipes();
        if(recipes.isEmpty()){
            eventError(1);
        }
        
        else{
            recipeVBox.getChildren().clear();
            recipeVBox.setSpacing(20);

            recipeVBox.setPadding(new Insets(20, 150, 10, 10));

            HashMap<Recipe, HashMap<String, Integer>> recipeMap = new HashMap<>();
            ArrayList<Integer> recipeTimes = new ArrayList<>();

            HBox timeHBox = new HBox();

            LimitedTextField hourText = new LimitedTextField(2);
            hourText.setPrefWidth(35);
            hourText.setAlignment(Pos.CENTER_RIGHT);

            LimitedTextField minuteText = new LimitedTextField(2);
            minuteText.setPrefWidth(35);
            minuteText.setAlignment(Pos.CENTER_LEFT);

            timeHBox.getChildren().addAll(new Text("Dinner Time: "), hourText, new Text(":"), minuteText);
            timeHBox.setAlignment(Pos.CENTER);

            recipeVBox.getChildren().add(timeHBox);

            for(Recipe recipe : recipes){
                recipeMap.put(recipe, model.parseInstructions(recipe));
            }

            for(Recipe recipe : recipes){
                for(String instruction : recipe.getInstructions()){
                    BorderPane borderPane = new BorderPane();
                    borderPane.setLeft(model.fixText(instruction));
                    borderPane.setRight(new TextField(recipeMap.get(recipe).get(instruction).toString()));
                    
                    recipeVBox.getChildren().add(borderPane);
                }
            }
            
            HashMap<String, Integer> instructionMap = new HashMap<>();
            
            Button finalizeButton = new Button("Finalize Schedule");
            finalizeButton.prefWidthProperty().bind(vizPane.widthProperty());
            finalizeButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if(hourText.getText().equals("") || minuteText.getText().equals("") || Integer.parseInt(hourText.getText()) > 12 || Integer.parseInt(hourText.getText()) < 1 || Integer.parseInt(minuteText.getText()) > 59 || Integer.parseInt(minuteText.getText()) < 0){
                        eventError(2);
                }
                    else{
                        ObservableList<Node> nodes = recipeVBox.getChildren();
                        for(int i = 1; i < nodes.size() - 1; i++){
                            BorderPane borderPane = (BorderPane) nodes.get(i);
                            ObservableList<Node> borders = borderPane.getChildren();
                            TextField textField = (TextField) borders.get(1);
                            
                            recipeTimes.add(Integer.parseInt(textField.getText()));
                        }
                        int j = 0;
                        for(Recipe recipe : recipes){
                            recipeMap.get(recipe).clear();
                            
                            ArrayList<String> instructions = recipe.getInstructions();
                                                        
                            for(int i = 0; i < instructions.size(); i++){
                                recipeMap.get(recipe).put(instructions.get(i), recipeTimes.get(j));
                                j++;
                            }
                        }
                        handleFinalizeScheduleAction(recipeMap, Integer.parseInt(hourText.getText()), Integer.parseInt(minuteText.getText()));
                    }}});
            
        
            recipeVBox.getChildren().add(finalizeButton);
        }
    }
    
    public void handleFinalizeScheduleAction(HashMap<Recipe, HashMap<String, Integer>> recipeMap, int hour, int minute){
        HashMap<String, Time> finalMap = model.finalizeInstructions(recipeMap, hour, minute);
        recipeVBox.getChildren().clear();
        recipeVBox.setSpacing(0);
        
        ArrayList<Recipe> recipes = model.getLoadedRecipes();
        
        ArrayList<String> instructionList = model.sortTimes(finalMap);
        String recipeNames = "";
        for(Recipe recipe : recipes){
            recipeNames = recipeNames.concat(recipe.getName() + ", ");
        }
        recipeNames = recipeNames.substring(0, recipeNames.lastIndexOf(","));
        
        recipeVBox.getChildren().add(new Text(recipeNames + "\nIngredients:"));
        
        for(Recipe recipe : recipes){
            for(String ingredient : recipe.getIngredients())
                recipeVBox.getChildren().add(new Text(ingredient));
        } 
        
        recipeVBox.getChildren().add(new Text());
        
            for(String instruction : instructionList){
                BorderPane borderPane = new BorderPane();
                String minutes = String.format("%02d", finalMap.get(instruction).getMinute());
                borderPane.setLeft(new Text(finalMap.get(instruction).getHour() + ":" + minutes));
                borderPane.setRight(model.fixText(instruction));
            
            recipeVBox.getChildren().add(borderPane);
            }
            
        Button saveButton = new Button("Save");
        saveButton.prefWidthProperty().bind(vizPane.widthProperty());
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    save(recipeVBox);
                }
        });
        recipeVBox.getChildren().addAll(new Text(), saveButton);
    }
    
    
    public void enterRecipe(){
        Dialog<ButtonType> dialog = new Dialog<>();

        //formats input window
        dialog.setTitle("Add Recipe");
        dialog.setHeaderText("Fill in the information below to add a new recipe");
        
        ButtonType addButtonType = new ButtonType("Add Recipe", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameText = new TextField();
        nameText.setPromptText("Recipe Name");

        TextArea ingredientText = new TextArea();
        ingredientText.setPromptText("Ingredients");

        TextArea instructionText = new TextArea();
        instructionText.setPromptText("Instructions");

        grid.add(new Label("Recipe Name:"), 0, 0);
        grid.add(nameText, 1, 0);
        grid.add(new Label("Ingredients:"), 0, 1);
        grid.add(ingredientText, 1, 1);
        grid.add(new Label("Instructions:"), 0, 2);
        grid.add(instructionText, 1, 2);

        
        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        //disables addbutton until final field is filled in
        instructionText.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);
        
        //displays dialog
        Optional<ButtonType> result = dialog.showAndWait();
        
        //tests result if user didn't press cancel
        if(result.get() != ButtonType.CANCEL){
            //if a field is blank gives error
            if(nameText.getText().equals("") || ingredientText.getText().equals("") || instructionText.getText().equals("")){
                eventError(0);
            }
            
            else{
            model.addRecipe(nameText.getText(), ingredientText.getText(), instructionText.getText());
            update(model.getLoadedRecipes());
            }
        }
    }
    
    public void update(ArrayList<Recipe> recipes){
        recipeVBox.getChildren().clear(); 
        recipeVBox.getChildren().add(createSchedule);
        
        //for each event, create an accordion 
        for(Recipe recipe : recipes){
            VBox accordionVBox = new VBox();
                            
            Button removeButton = new Button("Remove Recipe");
            removeButton.prefWidthProperty().bind(vizPane.widthProperty().subtract(25));
            removeButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    model.removeRecipe(recipe);
                    update(recipes);
            }});
            
            accordionVBox.getChildren().add(removeButton);
            
                //translates artists from arraylist to string
                ArrayList<String> ingredients = recipe.getIngredients();
                ArrayList<String> instructions = recipe.getInstructions();
                
                accordionVBox.getChildren().add(new Text("Ingredients:"));
                
                for(String ingredient : ingredients){
                    accordionVBox.getChildren().add(model.fixText(ingredient));
                }
                
                accordionVBox.getChildren().add(new Text("\nInstructions"));
                for(String instruction : instructions){
                    accordionVBox.getChildren().add(model.fixText(instruction));
                }


            //adds all the info into the accordion
            recipeVBox.getChildren().add(new Accordion(new TitledPane(recipe.getName(), accordionVBox)));
        }
    }
    
    public void eventError(int error){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        
        if(error == 0){
            alert.setTitle("Recipe Creation Error!");
            alert.setHeaderText("Please try again!");
            alert.setContentText("Unable to create recipe because information may have not been entered correctly.");
        }
        
        if(error == 1){
            alert.setTitle("Create Schedule Error!");
            alert.setHeaderText("Please try again!");
            alert.setContentText("Unable to create schedule because no recipes have been loaded");
        }
        
        if(error == 2){
            alert.setTitle("Finalize Schedule Error!");
            alert.setHeaderText("Please Try Again");
            alert.setContentText("Unable to finalize schedule because dinner time was entered incorrectly");
        }
        alert.showAndWait();
    }
    
    public void save(VBox recipeVBox){
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) vizPane.getScene().getWindow();
        
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        
        File file = fileChooser.showSaveDialog(stage);
        
        FileWriter writer = null;
        
        if(file != null) {
            
            try {
                writer = new FileWriter(file);
                
                writer.write(model.convertToString(recipeVBox));
                
            } catch( IOException ex) {
                MainModel.displayExceptionAlert(ex);
            } catch (Exception ex) {
                MainModel.displayExceptionAlert(ex);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException ex) {
                        MainModel.displayExceptionAlert(ex);
                    }
                }
            }
        }
    }
    
//    public void readImage(){
//        String pictureString = "";
//        
//        
//        FileChooser chooser = new FileChooser();
//        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg");
//        chooser.getExtensionFilters().add(filter);
//        
//        File file = chooser.showOpenDialog(null);
//
//        
//    }   
}