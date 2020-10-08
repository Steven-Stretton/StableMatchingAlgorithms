package uk.ac.bham;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application {

    public static Stage window;

    public static void main(String[] args) {
        launch(args); }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        Parent root = null;
        try{
            root = FXMLLoader.load(getClass().getResource("LandingPage.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        window.setScene(scene);
        window.setTitle("Home Page");
        window.show();
    }

    public static Stage getStage() {
        return window;
    }
    //**** CONTROLLER FOR LANDING PAGE ****//
    @FXML
    Button MBTI_Btn;
    @FXML
    Button EnterPref_Btn;
    @FXML
    Button RandomPref_Btn;
    @FXML
    Button StableMarriage_Btn;
    @FXML
    Button LandingPageInfo;
    @FXML
    public void MBTI_run(ActionEvent event) throws IOException {
        Parent mbtiRoot;
        mbtiRoot = FXMLLoader.load(getClass().getResource("MBTIconnect.fxml"));
        Scene mbtiScene = new Scene(mbtiRoot);
        window.setScene(mbtiScene);
        window.setTitle("M.B.T.I Implementation");
        window.centerOnScreen();
    }
    @FXML
    public void EnterPref_run(ActionEvent event) throws IOException {
        Parent enterPrefRoot;
        enterPrefRoot = FXMLLoader.load(getClass().getResource("/uk/ac/bham/vanillaimplementation/CreateGroup.fxml"));
        Scene enterPrefScene = new Scene(enterPrefRoot);
        window.setScene(enterPrefScene);
        window.setTitle("Create Group");
        window.centerOnScreen();
    }
    @FXML
    public void RandomPref_run(ActionEvent event) throws IOException {
        Parent randomPrefRoot;
        randomPrefRoot = FXMLLoader.load(getClass().getResource("/uk/ac/bham/RandomPreferenceStart.fxml"));
        Scene randomPrefScene = new Scene(randomPrefRoot);
        window.setScene(randomPrefScene);
        window.setTitle("Random Preferences");
        window.centerOnScreen();
    }
    @FXML
    public void LandingPageInfo_run(ActionEvent event) throws IOException {
        Stage infoPage = new Stage();
        Parent infoRoot;
        infoRoot = FXMLLoader.load(getClass().getResource("LandingPageInfo.fxml"));
        Scene infoPageScene = new Scene(infoRoot);
        infoPage.setScene(infoPageScene);
        infoPage.setTitle("Information Page");
        infoPage.centerOnScreen(); //HERE
        infoPage.show();
    }
    @FXML
    public void StableMarriage_run(ActionEvent event) throws IOException {
        Parent smRoot;
        smRoot = FXMLLoader.load(getClass().getResource("/uk/ac/bham/StableMarriageConnect.fxml"));
        Scene smScene = new Scene(smRoot);
        window.setScene(smScene);
        window.setTitle("Stable Marriage Algorithm");
        window.centerOnScreen(); //HERE
    }
    @FXML
    Hyperlink IrvingLink;
    public void IrvingLink_run(ActionEvent event) throws IOException {
        File file = new File("src/main/resources/roomates.pdf");
        HostServices hostServices = getHostServices();
        hostServices.showDocument(file.getAbsolutePath());
    }
}
