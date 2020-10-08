package uk.ac.bham.mbtiimplementation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import uk.ac.bham.Main;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class MBTIConnectController {
    @FXML //Back button for returning to Home Page
    Button BackButton;
    @FXML //Go button for entering the spreadsheet data/ csv file and continue to next page
    Button GO_Btn;
    @FXML //The input text box which can receive a spreadsheet URL link or alternatively a csv file
    TextField spreadsheetInput;
    @FXML //The range specified within the Google Sheet
    TextField rangeInput;
    //When the back button is activated
    @FXML
    public void backButtonActivated(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/uk/ac/bham/LandingPage.fxml"));
        Main.getStage().setTitle("Home Page");
        Main.window.setHeight(600);
        Main.window.setWidth(1000);
        Parent root = loader.load();
        BackButton.getScene().setRoot(root);
    }
    //When the go button is activated
    @FXML
    public void goBtnActivated(ActionEvent event) throws IOException, GeneralSecurityException {
        //check to see if input is .CSV file
        if (!spreadsheetInput.getText().trim().isEmpty() && !spreadsheetInput.getText().trim().equals("") && rangeInput.getText().trim().isEmpty() && spreadsheetInput.getText().trim().contains("csv")) {
            MBTIData largeDataInstance = new MBTIData(spreadsheetInput.getText().trim());
            String[][] pMatrix = largeDataInstance.getPersonMatrix();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uk/ac/bham/MBTIVisualgo.fxml"));
            MBTIVisualisationController controller = new MBTIVisualisationController(pMatrix);
            loader.setController(controller);
            Parent root = loader.load();
            GO_Btn.getScene().setRoot(root);
            Main.window.setTitle("MBTI Visualisation");
            Main.window.setHeight(1075);
            Main.window.setWidth(1525);
            Main.getStage().centerOnScreen();
        //else check to see if input is Google Sheet link
        } else if (!spreadsheetInput.getText().trim().isEmpty() && !spreadsheetInput.getText().trim().equals("") && !rangeInput.getText().trim().isEmpty() && !rangeInput.getText().trim().equals("")) {
            MBTIData instance = new MBTIData(spreadsheetInput.getText().trim(), rangeInput.getText().trim());
            String[][] pMatrix = instance.getPersonMatrix();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uk/ac/bham/MBTIVisualgo.fxml"));
            MBTIVisualisationController controller = new MBTIVisualisationController(pMatrix);
            loader.setController(controller);
            Parent root = loader.load();
            GO_Btn.getScene().setRoot(root);
            Main.window.setTitle("MBTI Visualisation");
            Main.window.setHeight(1075);
            Main.window.setWidth(1525);
            Main.getStage().centerOnScreen();
        }
    }
}
