package uk.ac.bham.stableroommates;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import uk.ac.bham.Main;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EnterPreferencesController implements Initializable {

    public List<String> members; //the members which have been added to this group
    public String groupName;

    public String[][] pMatrix;
    public int outer = 0;
    public int inner = 0;

    @FXML
    HBox SelectName = new HBox();

    @FXML
    HBox SelectPartners = new HBox();

    @FXML
    Button SavePreferences;


    public EnterPreferencesController(List<String> members, String groupName) {
        this.members = members;
        this.pMatrix = new String[members.size()][members.size()];
        this.groupName = groupName;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        for (int i = 0; i < members.size(); i++) {
            Button button = new Button();
            button.setText(members.get(i));
            button.setStyle("-fx-background-color: #1F2833; -fx-border-color: #66fcf1; -fx-font-size: 18; -fx-font-family: System; -fx-text-fill: #c5c6c7;");
            button.setOnAction(event -> {
                SelectPartners.getChildren().clear();
                this.pMatrix[outer][pMatrix.length-1] = button.getText();
                button.setDisable(true);
                SelectName.setDisable(true);
                SelectPartners.setDisable(false);
                generateOtherButtons(button.getText());
            });
            SelectName.getChildren().add(button);
        }
    }

    public void generateOtherButtons(String selector) {
        for (int i = 0; i < members.size(); i++) {
            Button Obutton = new Button();
            Obutton.setText(members.get(i));
            if (Obutton.getText().equals(selector)) {
                Obutton.setDisable(true);
            }
            Obutton.setStyle("-fx-background-color: #1F2833; -fx-border-color: #66fcf1; -fx-font-size: 18; -fx-font-family: System; -fx-text-fill: #c5c6c7;");
            Obutton.setOnAction(event -> {
                Obutton.setDisable(true);
                try {
                    if (inner < pMatrix.length - 1) {
                        this.pMatrix[outer][inner] = Obutton.getText();
                        inner++;
                        if (inner == pMatrix.length - 1) {
                            outer++;
                            inner = 0;
                            SelectPartners.setDisable(true);
                            SelectName.setDisable(false);
                        }
                    }
                    //Enable the Save Preferences button - all preferences have been selected
                    if (outer == members.size() && inner == 0){
                        SavePreferences.setDisable(false);
                    }
                }catch (IndexOutOfBoundsException ex) {
                }
            });
            SelectPartners.getChildren().add(Obutton);
        }
    }
    /**
     * This method will save the entered members and their preferences.
     * Launch the algorithm visualiser scene.
     * Instantiate controller with the inputted data.
     *
     * @param event : Clicking the Save Preferences Button
     * @throws IOException
     */
    @FXML
    public void savePreferencesBtnActivated(ActionEvent event) throws IOException {
        SavePreferences.getScene().getWindow().setHeight(1075);
        SavePreferences.getScene().getWindow().setWidth(1525);
        Main.getStage().setTitle("Stable Matching Visualiser");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/uk/ac/bham/Visualgo.fxml"));
        VisualgoController visualController = new VisualgoController(pMatrix, groupName);
        loader.setController(visualController);
        Parent root = loader.load();
        SavePreferences.getScene().setRoot(root);
        Main.getStage().centerOnScreen();
    }
}