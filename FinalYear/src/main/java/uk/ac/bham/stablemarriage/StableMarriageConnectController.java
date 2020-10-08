package uk.ac.bham.stablemarriage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import uk.ac.bham.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class StableMarriageConnectController {

    @FXML
    TextField pathnameInput;
    @FXML
    Button GO_Btn;
    @FXML
    Button BackButton;
    @FXML
    Label fileErrorLabel;

    @FXML
    public void goBtnActivated(ActionEvent event) throws IOException {
        String path = pathnameInput.getText().trim();
        File file = new File(path);
        if (file.isFile()) {
            readData(path);
            sortMembers();
            generatePMatrix();
            mapToNumbers(members);
            numericalPMatrix = createNumericMatrix();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uk/ac/bham/Visualgo.fxml"));
            SMVisualisationController controller = new SMVisualisationController(preferenceMatrix, numericalPMatrix, members);
            loader.setController(controller);
            Parent root = loader.load();
            GO_Btn.getScene().setRoot(root);
            Main.window.setTitle("Stable Marriage Visualisation");
            Main.window.setHeight(1075);
            Main.window.setWidth(1525);
            Main.getStage().centerOnScreen();

        } else {
            fileErrorLabel.setText("File has not been recognised");
        }
    }
    @FXML
    public void backButtonActivated(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/uk/ac/bham/LandingPage.fxml"));
        Main.getStage().setTitle("Home Page");
        Parent root = loader.load();
        BackButton.getScene().setRoot(root);
        Main.window.setHeight(600);
        Main.window.setWidth(1000);
        Main.window.centerOnScreen();
    }


    String[] members;
    Integer[] experience;
    String[] inexperienced;
    String[] experienced;
    String[][] preferenceMatrix;
    Integer setSize;
    Integer[] memberNumbers;
    //Mapping the string values (names) of each person to a numerical value to be used throughout the algorithm
    Map<String, Integer> nameToNumericValueMap;
    Integer[][] numericalPMatrix;
    /**
     * This method is used for reading the data given from a CSV file.
     * @param CSVpath : Insert copy of file pathname
     * @throws IOException
     */
    public void readData(String CSVpath) throws IOException {
        int groupSize = 0;
        BufferedReader in = new BufferedReader(new FileReader(CSVpath));
        String line;
        ArrayList<String> membersList = new ArrayList<>();
        ArrayList<Integer> experienceList = new ArrayList<>();
        while ((line = in.readLine()) != null) {
            String[] values = line.split(",");
            //Get each persons experience and add it to the totalExperience.
            groupSize++;
            membersList.add(values[0]);
            experienceList.add(Integer.parseInt(values[1]));
        }
        members = new String[membersList.size()];
        experience = new Integer[experienceList.size()];
        //Create two sets which will be used in the matching algorithm
        inexperienced = new String[groupSize/2];
        experienced   = new String[groupSize/2];
        //Add each of the members and their experience to respective arrays
        members = membersList.toArray(new String[membersList.size()]);
        experience = experienceList.toArray(new Integer[experienceList.size()]);

        //Create membersNumbers so we can refer to each member by number, rather than name
        int counter = 0;
        Integer[] memberNumbers = new Integer[membersList.size()];
        for (String ignored : members) {
            memberNumbers[counter] = counter;
            counter++;
        }
    }

    /**
     * This method sorts the members that are contained within the csv file.
     * This will allow us to create two distinct sets of experience / inexperienced programmers.
     */
    public void sortMembers() {
        for (int i = 1; i < experience.length; i++) {
            int currentExp = experience[i];
            String currentName = members[i];
            int j = i - 1;
            while (j >= 0 && currentExp < experience[j]) {
                experience[j + 1] = experience[j];
                members[j + 1] = members[j];
                j--;
            }
            experience[j + 1] = currentExp;
            members[j + 1] = currentName;
        }
        for (int i = 0; i < members.length; i++) {
            if (i < members.length / 2) {
                inexperienced[i] = members[i];
            } else {
                experienced[i - (members.length / 2)] = members[i];
            }
        }
    }

    /**
     * This method generates a randomised PMatrix, in which each inexperienced person has
     * random preference selection for the experienced persons. Each experienced person
     * also has a list of the inexperienced members randomly generated for them.
     */
    public void generatePMatrix() {
        //Creates a preference matrix
        preferenceMatrix = new String[members.length][members.length/2];
        setSize = preferenceMatrix.length/2;
        for (int i = 0 ; i < preferenceMatrix.length ; i++) {
            if (i < setSize) {
                List<String> list = new ArrayList<>();
                for (int j = 0; j < members.length / 2; j++) {
                    list.add(members[members.length / 2 + j]);
                }
                Collections.shuffle(list);
                String[] arr = list.toArray(new String[0]);
                preferenceMatrix[i] = arr;
            } else {
                ArrayList<String> list = new ArrayList<>();
                for (int j = 0 ; j < members.length / 2; j++) {
                    list.add(members[j]);
                }
                Collections.shuffle(list);
                String[] arr = list.toArray(new String[0]);
                preferenceMatrix[i] = arr;
            }
        }
    }



    /**
     * This method will map the names of the preference matrix, and assign each member a number.
     * Each member will receive a unique identification number from 0 - n
     * @param members : List of members being used in the matching
     */
    private void mapToNumbers(String[] members) {
        nameToNumericValueMap = new TreeMap<>();
        for (int i = 0; i < members.length; i++) {
            //insert name(string) and integer value
            nameToNumericValueMap.put(members[i], i);
        }
    }

    private Integer[][] createNumericMatrix() {
        Integer[][] output = new Integer[members.length][setSize];
        for (int i = 0; i < members.length; i++) {
            for (int j = 0; j < setSize; j++) {
                int number = nameToNumericValueMap.get(preferenceMatrix[i][j]);
                output[i][j] = number;
            }
        }
        return output;
    }

    private String getKeysFromValue(Map<String, Integer> map, Integer value){
        String name = "";
        for(String members : map.keySet()){
            if(map.get(members).equals(value)) {
                name = members;
            }
        }
        return name;
    }
}
