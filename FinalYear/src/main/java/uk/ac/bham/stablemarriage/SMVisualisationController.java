package uk.ac.bham.stablemarriage;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import uk.ac.bham.Main;

import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;

public class SMVisualisationController implements Initializable {

    String[][] preferenceMatrix;
    Integer[][] numericalPMatrix;
    String[] members;
    Group group = new Group();

    @FXML
    Label groupNameLabel;
    @FXML
    VBox TablePaneVbox;
    @FXML
    TextArea NarrationBox;
    @FXML
    ScrollPane GraphScroll;

    public SMVisualisationController (String[][] preferenceMatrix, Integer[][] numericalPMatrix, String[] members) {
        this.preferenceMatrix = preferenceMatrix;
        this.numericalPMatrix = numericalPMatrix;
        this.members = members;
    }
    @FXML
    public void homeBtnActivated(ActionEvent event) throws IOException {
        Parent root;
        root = FXMLLoader.load(getClass().getResource("/uk/ac/bham/LandingPage.fxml"));
        Scene home = new Scene(root);
        Main.getStage().setScene(home);
        Main.window.setHeight(600);
        Main.window.setWidth(1000);
        Main.getStage().setTitle("Home Page");
        Main.window.centerOnScreen();
    }
    @FXML
    public void startBtnActivated(ActionEvent event) throws InterruptedException, IOException {
        NarrationBox.setText("");
        NarrationBox.setText("Algorithm starting...\n");
        SMAlgorithm run = new SMAlgorithm(members, numericalPMatrix, this, preferenceMatrix);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        groupNameLabel.setText("Stable Marriage");
        TableView<ObservableList<String>> table = createTableView(preferenceMatrix);
        table.setStyle("-fx-control-inner-background: #1F2833;");
        TablePaneVbox.getChildren().add(table);
        createGraphNodes(members);
    }

    private TableView<ObservableList<String>> createTableView(String[][] dataArray) {

        TableView<ObservableList<String>> tableView = new TableView<>();
        tableView.setStyle("-fx-max-height: 250;");
        tableView.setItems(buildData(dataArray));
        for (int i = 0; i < dataArray[0].length; i++) {
            final int prefNumber = i;
            final TableColumn<ObservableList<String>, String> column = new TableColumn<>("Preference " + (prefNumber + 1));
            column.setStyle("-fx-alignment: CENTER");
            column.setSortable(false);
            column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(prefNumber)));
            tableView.getColumns().add(column);
        }
       return tableView;
    }

    private ObservableList<ObservableList<String>> buildData(String[][] dataArray) {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        for (String[] row : dataArray) {
            data.add(FXCollections.observableArrayList(row));
        }
        return data;
    }

    private void createGraphNodes(String[] members) {
        Pane pane = new Pane(group);

        HBox inexperienced = new HBox();
        inexperienced.setLayoutX(150);
        inexperienced.setLayoutY(200);
        inexperienced.setSpacing(20);

        HBox experienced = new HBox();
        experienced.setLayoutX(150);
        experienced.setLayoutY(400);
        experienced.setSpacing(20);

        for (int i = 0 ; i < members.length / 2 ; i++) {
            Label nameLabel = new Label(members[i]);
            nameLabel.setStyle("-fx-border-color: white;" + "-fx-background-color:  #1F2833;");
            nameLabel.setPadding(new Insets(5,5,5,5));
            inexperienced.getChildren().add(nameLabel);
        }
        for (int i = members.length / 2, j = 0; i < members.length ; i++) {
            Label nameLabel = new Label(members[i]);
            nameLabel.setStyle("-fx-border-color: white;" + "-fx-background-color:  #1F2833;");
            nameLabel.setPadding(new Insets(5,5,5,5));
            j++;
            experienced.getChildren().add(nameLabel);
        }
        group.getChildren().add(inexperienced);
        group.getChildren().add(experienced);
        GraphScroll.setContent(pane);
    }

    public void drawLine(int inexperienced, int experienced, Color colour, int strokeWidth) {
        double[] coordinates = new double[4];

        Node inexperiencedHbox = group.getChildren().get(0);
        if (inexperiencedHbox instanceof HBox) {

            Node label = ((HBox)inexperiencedHbox).getChildren().get(inexperienced);
            Bounds boundOfBox = inexperiencedHbox.getBoundsInParent();
            Bounds boundsInParent = label.localToParent(label.getBoundsInParent());

            double StartX = boundsInParent.getMinX() + boundOfBox.getMinX() - label.getLayoutX() + 20;
            coordinates[0] = StartX;
            double StartY = boundsInParent.getMinY() + boundOfBox.getMinY() + 20;
            coordinates[1] = StartY;
        }

        Node experiencedHbox = group.getChildren().get(1);
        if (experiencedHbox instanceof HBox) {

            Node label = ((HBox)experiencedHbox).getChildren().get(experienced);
            Bounds boundOfBox = experiencedHbox.getBoundsInParent();
            Bounds boundsInParent = label.localToParent(label.getBoundsInParent());

            double FinishX = boundsInParent.getMinX() + boundOfBox.getMinX() - label.getLayoutX() + 20;
            coordinates[2] = FinishX;
            double FinishY = boundsInParent.getMinY() + boundOfBox.getMinY() + 20;
            coordinates[3] = FinishY;
        }
        Line line = new Line();
        line.setStrokeWidth(strokeWidth);
        line.setStroke(colour);
        line.setStartX(coordinates[0]);
        line.setStartY(coordinates[1]);
        line.setEndX  (coordinates[2]);
        line.setEndY  (coordinates[3]);
        line.setViewOrder(1);
        group.getChildren().add(line);
    }
}
