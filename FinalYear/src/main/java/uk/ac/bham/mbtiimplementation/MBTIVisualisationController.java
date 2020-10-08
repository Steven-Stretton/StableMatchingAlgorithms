package uk.ac.bham.mbtiimplementation;

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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import uk.ac.bham.Main;
import uk.ac.bham.stableroommates.SRAlgorithm;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class MBTIVisualisationController implements Initializable {
    //Group contains nodes and edges
    private Group group = new Group();
    //PersonMatrix contains String values for each person (names)
    private String[][] personMatrix;
    //TablePane is used for initialising the TableView, and TableView will be added to this Pane
    @FXML
    private
    AnchorPane TablePane;
    //NarrationBox is used for displaying each working step of the algorithm in text
    @FXML
    public TextArea NarrationBox;
    //ScrollPane is used for navigating around when there are a large number of members
    @FXML
    private
    ScrollPane GraphScroll;
    //Constructor for GoogleSheets instance
    MBTIVisualisationController(String[][] pMatrix) {
        this.personMatrix = pMatrix;
    }
    //The first method to be run within this class
    //Checks whether the given input is a CSV file or a Google Sheets link
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (personMatrix != null) {
            TableView<ObservableList<String>> table = createTableView(personMatrix);
            table.prefHeightProperty().bind(TablePane.heightProperty());
            table.prefWidthProperty().bind(TablePane.widthProperty());
            table.setStyle("-fx-control-inner-background: #1F2833;");
            TablePane.getChildren().add(table);
            createGraphNodes(personMatrix);
        }
    }
    /**
     * Alternative method for createTableView which will take an Integer[][] instead of String[][]
     * @param personMatrixNumber : two-dimensional array of preference lists
     * @return TableView : table containing all of the personMatrixNumber information
     */
    private TableView<ObservableList<Integer>> createTableViewNumber(Integer[][] personMatrixNumber) {
        TableView<ObservableList<Integer>> tableView = new TableView<>();
        tableView.setItems(buildDataNumber(personMatrixNumber));
        for (int i = 0; i < personMatrixNumber[0].length; i++) {
            final int prefNumber = i;
            if (i == personMatrixNumber[0].length-1) {
                final TableColumn<ObservableList<Integer>, Integer> column = new TableColumn<>("Name");
                column.setStyle("-fx-alignment: CENTER");
                column.setSortable(false);
                column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(prefNumber)));
                tableView.getColumns().add(column);
            } else {
                final TableColumn<ObservableList<Integer>, Integer> column = new TableColumn<>("Preference " + (prefNumber + 1));
                column.setStyle("-fx-alignment: CENTER");
                column.setSortable(false);
                column.setCellValueFactory(
                        param -> new ReadOnlyObjectWrapper<>(param.getValue().get(prefNumber))
                );
                tableView.getColumns().add(column);
            }
        }
        return tableView;
    }
    /**
     * Method for createTableView which will take a String[][]
     * @param personMatrix : two-dimensional array of preference lists
     * @return TableView : table containing all of the personMatrix information
     */
    private TableView<ObservableList<String>> createTableView(String[][] personMatrix) {
        TableView<ObservableList<String>> tableView = new TableView<>();
        tableView.setItems(buildData(personMatrix));

        for (int i = 0; i < personMatrix[0].length; i++) {
            final int prefNumber = i;
            if (i == personMatrix[0].length-1) {
                final TableColumn<ObservableList<String>, String> column = new TableColumn<>("Name");
                column.setStyle("-fx-alignment: CENTER");
                column.setSortable(false);
                column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(prefNumber)));
                tableView.getColumns().add(column);
            } else {
                final TableColumn<ObservableList<String>, String> column = new TableColumn<>("Preference " + (prefNumber + 1));
                column.setStyle("-fx-alignment: CENTER");
                column.setSortable(false);
                column.setCellValueFactory(
                        param -> new ReadOnlyObjectWrapper<>(param.getValue().get(prefNumber))
                );
                tableView.getColumns().add(column);
            }
        }
        return tableView;
    }
    /*
        This method is used for creating nodes for the graph visualisation for Google Sheets data
     */
    private void createGraphNodes(String[][] pMatrix) {
        Pane pane = new Pane(group); //container
        for (String[] matrix : pMatrix) { //for every person in the preference matrix
            StackPane stackPane = new StackPane(); //create a stackpane
            Random random = new Random();
            int g = random.nextInt(252);
            int b = random.nextInt(252);
            //add label to stack pane, stack label on top of circle
            Label name = new Label(matrix[pMatrix.length - 1]);
            name.setStyle("-fx-font-weight: bold;" + "-fx-text-fill: black;" + "-fx-font-size: 10;");
            Circle circle = new Circle();
            circle.setFill(Color.rgb(227, g, b));
            circle.setStroke(Color.BLACK);
            circle.setRadius(30);
            stackPane.getChildren().addAll(circle, name); //add circle to stack pane
            //set layout for stackpane, so that the nodes are randomly distributed within the bounds
            stackPane.setLayoutX(random.nextInt(1200));
            stackPane.setLayoutY(random.nextInt(1200));
            group.getChildren().add(stackPane);
        }
        GraphScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        GraphScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        GraphScroll.setContent(pane);
    }
    /*
    This method is used for creating nodes for the graph visualisation for CSV data
     */
    private void createGraphNodesNumber(Integer[][] personMatrixNumber) {
        Pane pane = new Pane(group); //container
        for (Integer[] integers : personMatrixNumber) { //for every person in the preference matrix
            StackPane stackPane = new StackPane(); //create a stackpane
            Random random = new Random();
            int g = random.nextInt(252);
            int b = random.nextInt(252);
            //add label to stack pane, stack label on top of circle
            Label name = new Label(Integer.toString(integers[personMatrixNumber.length - 1]));
            name.setStyle("-fx-font-weight: bold;" + "-fx-text-fill: black;" + "-fx-font-size: 10;");
            Circle circle = new Circle();
            circle.setFill(Color.rgb(227, g, b));
            circle.setStroke(Color.BLACK);
            circle.setRadius(30);
            stackPane.getChildren().addAll(circle, name); //add circle to stack pane
            //set layout for stackpane, so that the nodes are randomly distributed within the bounds
            stackPane.setLayoutX(random.nextInt(1200));
            stackPane.setLayoutY(random.nextInt(1200));
            group.getChildren().add(stackPane);
        }
        GraphScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        GraphScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        GraphScroll.setContent(pane);
    }
    /**
     * ObservableList is required to be used with TableView.
     * Add each row from the preference list into an observable list
     * A data row is added for each String[] in dataArray
     * @param dataArray
     * @return
     */
    private ObservableList<ObservableList<String>> buildData(String[][] dataArray) {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        for (String[] row : dataArray) {
            data.add(FXCollections.observableArrayList(row));
        }
        return data;
    }
    /**
     * Alternative method to buildData which takes an Integer[][] instead of String[][]
     * @param dataArray : Holds the preference list matrix
     * @return : ObservableList which can be used with TableView
     */
    private ObservableList<ObservableList<Integer>> buildDataNumber(Integer[][] dataArray) {
        ObservableList<ObservableList<Integer>> data = FXCollections.observableArrayList();
        for (Integer[] row : dataArray) {
            data.add(FXCollections.observableArrayList(row));
        }
        return data;
    }
    /*
    Method used for drawing a line between nodes.
     */
    public void drawLine(int start, int finish, Color colour, int strokeWidth) {
        double[] coordinates = new double[4];
        Node startNode = group.getChildren().get(start);
        if (startNode instanceof StackPane) {
            //getLayout() retrieves top-left point of StackPane, +30 for center of Circle node (radius)
            double StartX = startNode.getLayoutX() + 30;
            coordinates[0] = StartX;
            double StartY = startNode.getLayoutY() + 30;
            coordinates[1] = StartY;
        }
        Node finishNode = group.getChildren().get(finish);
        if (finishNode instanceof StackPane) {
            double FinishX = finishNode.getLayoutX() + 30;
            coordinates[2] = FinishX;
            double FinishY = finishNode.getLayoutY() + 30;
            coordinates[3] = FinishY;
        }
        Line line = new Line();
        line.setStrokeWidth(strokeWidth); //width of edge
        line.setStroke(colour); //colour of edge
        line.setStartX(coordinates[0]); //coordinates for starting and ending point
        line.setStartY(coordinates[1]);
        line.setEndX  (coordinates[2]);
        line.setEndY  (coordinates[3]);
        line.setViewOrder(1); //place edge below the view order of the node (no overlapping)
        group.getChildren().add(line); //add the edge to the group
    }
    //Method for when the start button is activated
    @FXML
    public void startBtnActivated(ActionEvent event) throws InterruptedException {
        NarrationBox.setText("");
        NarrationBox.setText("Algorithm starting...\n");
        SRAlgorithm run = new SRAlgorithm(personMatrix, this);
    }
    //Method for when the home button is activated
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
