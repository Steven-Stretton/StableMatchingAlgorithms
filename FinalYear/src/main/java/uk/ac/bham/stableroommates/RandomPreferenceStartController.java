package uk.ac.bham.stableroommates;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.stage.Screen;
import uk.ac.bham.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomPreferenceStartController {
    @FXML
    Button BackButton;
    @FXML
    Button GO_Btn;
    @FXML
    Slider personSlider;
    @FXML
    public void backButtonActivated(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/uk/ac/bham/LandingPage.fxml"));
        Main.getStage().setTitle("Home Page");
        Parent root = loader.load();
        BackButton.getScene().setRoot(root);
    }
    @FXML
    public void goBtnActivated(ActionEvent event) throws IOException{
        //get the group size of random preferences
        int sizeOfGroup = (int) personSlider.getValue();
        String[][] randomMatrix = generateRandomMatrix(sizeOfGroup);
        Main.window.setHeight(1075);
        Main.window.setWidth(1525);
        Main.getStage().setTitle("Stable Matching Visualiser - Randomised");
        //**Load the new Scene**
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/uk/ac/bham/Visualgo.fxml"));
        VisualgoController randomVisualise = new VisualgoController(randomMatrix, "Random Preferences");
        loader.setController(randomVisualise);
        Parent root = loader.load();
        GO_Btn.getScene().setRoot(root);
        Main.window.centerOnScreen();   //Center the Stage to screen
    }

    public String[][] generateRandomMatrix(int sizeOfGroup) {
        String[][] output = new String[sizeOfGroup][];
        //
        for (int i = 0; i < sizeOfGroup; i++) {
            //Create a list of preferences
            List<String> list = new ArrayList<String>();
            for (int j = 0; j < sizeOfGroup; j++) {
                //Add each other member, apart from yourself
                if (j != i) list.add(Integer.toString(j+1));
            }
            //Shuffle the list to randomise the preference
            Collections.shuffle(list);
            //Add the proposer to the end of the list
            list.add(Integer.toString(i+1));
            //Convert the list back to an array in order to be compatible with algorithm logic
            String[] arr = list.toArray(new String[0]);
            //Add the array to output to represent person i's preferences
            output[i] = arr;
        }
        return output;
    }

}
