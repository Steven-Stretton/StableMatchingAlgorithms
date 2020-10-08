package uk.ac.bham.stableroommates;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import uk.ac.bham.Main;

import java.io.IOException;

public class CreateGroupController {

    @FXML
    Button GO_Btn;
    @FXML
    TextField GroupNameInput;
    @FXML
    Button BackButton;
    @FXML
    Label ErrorMessage;

    @FXML
    public void goBtnActivated(ActionEvent event) throws IOException {
        if (!GroupNameInput.getText().trim().isEmpty() && !GroupNameInput.getText().trim().equals("") && GroupNameInput.getText().trim().matches("^[a-zA-Z ']*$")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uk/ac/bham/vanillaimplementation/PopulateGroup.fxml"));
            Main.getStage().setTitle("Populate Group");
            Parent root = loader.load();
            PopulateGroupController popGroupController = loader.getController();
            popGroupController.setGroupName(GroupNameInput.getText());
            GO_Btn.getScene().setRoot(root);
        } else {
            ErrorMessage.setVisible(true);
        }
    }

    @FXML
    public void backButtonActivated(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/uk/ac/bham/LandingPage.fxml"));
        Main.getStage().setTitle("Home Page");
        Parent root = loader.load();
        BackButton.getScene().setRoot(root);
    }

    @FXML
    public void onEnter(ActionEvent ae) throws IOException {
        if (!GroupNameInput.getText().trim().isEmpty() && !GroupNameInput.getText().trim().equals("") && GroupNameInput.getText().trim().matches("^[a-zA-Z ']*$")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uk/ac/bham/vanillaimplementation/PopulateGroup.fxml"));
            Main.getStage().setTitle("Populate Group");
            Parent root = loader.load();
            PopulateGroupController popGroupController = loader.getController();
            popGroupController.setGroupName(GroupNameInput.getText());
            GO_Btn.getScene().setRoot(root);
        } else {
            ErrorMessage.setVisible(true);
        }
    }
}
