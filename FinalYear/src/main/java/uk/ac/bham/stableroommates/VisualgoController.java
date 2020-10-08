package uk.ac.bham.stableroommates;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import uk.ac.bham.Main;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class VisualgoController implements Initializable {

    String[][] pMatrix;
    String groupName;

    @FXML
    Label groupNameLabel;
    @FXML
    VBox TablePaneVbox;
    @FXML
    TextArea NarrationBox;
    @FXML
    ScrollPane GraphScroll;
    Group group = new Group();

    public VisualgoController(String[][] pMatrix, String groupName) {
        this.pMatrix = pMatrix;
        this.groupName = groupName;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        groupNameLabel.setText(groupName);
        TableView<ObservableList<String>> table = createTableView(pMatrix);
        table.setStyle("-fx-control-inner-background: #1F2833;");
        table.setPrefHeight(220);
        TablePaneVbox.getChildren().add(table);
        createGraphNodes(pMatrix);
    }
    /**
     * This method is used for adding the nodes to the ScrollPane.
     * Each node will represent a member of the group, and edges will later be used to represent
     * proposals and matches between the members of the group.
     *
     * @param pMatrix : the groups preference matrix containing members and preferences
     */
    private void createGraphNodes(String[][] pMatrix) {
        /*
        The following three arrays represent the layout of the nodes in the ScrollPane.
        This is to ensure that the nodes do not overlap and are in a suitable layout in order
        to produce a clean visualisation of the edges between the nodes.
         */
        int[][] four = new int[][]    {{125 , 131},
                                       {375 , 131},
                                       {125 , 473},
                                       {375 , 473}};
        int[][] six = new int[][]     {{227 , 131},
                                       {362 , 131},
                                       {102  , 342},
                                       {497 , 342},
                                       {227 , 553},
                                       {362 , 553}};
        int[][] eight = new int[][]   {{240 , 50},
                                       {380 , 50},
                                       {100 , 245},
                                       {520 , 245},
                                       {100 , 385},
                                       {520 , 385},
                                       {240 , 580},
                                       {380 , 580}};
        Pane pane = new Pane(group);
        pane.setPrefHeight(GraphScroll.getPrefHeight());
        pane.setPrefWidth(GraphScroll.getPrefWidth());
        for (int i = 0; i < pMatrix.length; i++) {
                StackPane stackPane = new StackPane();
                Random random = new Random();
                int g = random.nextInt(252);
                int b = random.nextInt(252);
            Label name = new Label(pMatrix[i][pMatrix.length - 1]);
                name.setStyle("-fx-font-weight: bold;" + "-fx-text-fill: black;");
                Circle circle = new Circle();
                circle.setFill(Color.rgb(227, g, b));
                circle.setStroke(Color.BLACK);
                circle.setRadius(40);
                stackPane.getChildren().addAll(circle, name);

                if        (pMatrix.length == 4) {
                    stackPane.setLayoutX(four[i][0]);
                    stackPane.setLayoutY(four[i][1]);
                } else if (pMatrix.length == 6) {
                    stackPane.setLayoutX(six[i][0]);
                    stackPane.setLayoutY(six[i][1]);
                } else if (pMatrix.length == 8) {
                    stackPane.setLayoutX(eight[i][0]);
                    stackPane.setLayoutY(eight[i][1]);
                }
                group.getChildren().add(stackPane);
        }
        GraphScroll.setContent(pane);
    }
    /**
     * This method will take two numbers, which will represent the number of the members that are being linked
     *
     * @param start : represents the proposer
     * @param finish : represents the receiver
     */
    public void drawLine(int start, int finish, Color colour, int strokeWidth) {
        double[] coordinates = new double[4];
        Node startNode = group.getChildren().get(start);
        if (startNode instanceof StackPane) {
            //getLayout() retrieves top-left point of StackPane, +40 for center of Circle node (radius)
            double StartX = startNode.getLayoutX() + 40;
            coordinates[0] = StartX;
            double StartY = startNode.getLayoutY() + 40;
            coordinates[1] = StartY;
        }
        Node finishNode = group.getChildren().get(finish);
        if (finishNode instanceof StackPane) {
            double FinishX = finishNode.getLayoutX() + 40;
            coordinates[2] = FinishX;
            double FinishY = finishNode.getLayoutY() + 40;
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
    private ObservableList<ObservableList<String>> buildData(String[][] dataArray) {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        for (String[] row : dataArray) {
            data.add(FXCollections.observableArrayList(row));
        }
        return data;
    }
    /**
     * This method is used for initialising the tableView for visualising the algorithm and results.
     *
     * @param dataArray
     * @return
     */
    private TableView<ObservableList<String>> createTableView(String[][] dataArray) {
        TableView<ObservableList<String>> tableView = new TableView<>();
        tableView.setStyle("-fx-max-height: 250;");
        tableView.setItems(buildData(dataArray));
        for (int i = 0; i < dataArray[0].length; i++) {
            final int prefNumber = i;
            if (i == dataArray[0].length-1) {
                final TableColumn<ObservableList<String>, String> column = new TableColumn<>("Name ");
                column.setStyle("-fx-alignment: CENTER;" + "-fx-background-color: #a2a2a3;" + "-fx-text-fill: black;");
                column.setSortable(false);
                column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(prefNumber)));
                tableView.getColumns().add(column);
            } else {
                final TableColumn<ObservableList<String>, String> column = new TableColumn<>("Preference " + (prefNumber + 1));
                column.setStyle("-fx-alignment: CENTER");
                column.setSortable(false);
                column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(prefNumber)));
                tableView.getColumns().add(column);
            }
        }
        return tableView;
    }
    @FXML
    public void startBtnActivated(ActionEvent event) throws InterruptedException {
        SRAlgorithm runVanilla = new SRAlgorithm(pMatrix, this);
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
}

