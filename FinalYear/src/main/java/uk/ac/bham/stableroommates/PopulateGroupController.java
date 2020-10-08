package uk.ac.bham.stableroommates;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PopulateGroupController {

    private int counterDOWN = 8;
    private int counter = 0;

    @FXML
    TextField AddMemberNameInput;
    @FXML
    Button AddMember;
    @FXML
    Button SaveGroup;
    @FXML
    TextArea GroupMemberList;
    @FXML
    Label GroupName_Lbl;
    @FXML
    Label ErrorMessageMaxMembers;

    public void setGroupName(String name) {
        GroupName_Lbl.setText(name);
    }

    @FXML
    public void addMemberBtnActivated(ActionEvent event) throws IOException {
        if (counterDOWN > 0) {
            //check that the user has entered a name
            if (!AddMemberNameInput.getText().trim().isEmpty() && !AddMemberNameInput.getText().trim().equals("") && AddMemberNameInput.getText().trim().matches("^[a-zA-Z0-9]*$")) {
                GroupMemberList.setText(GroupMemberList.getText().concat(AddMemberNameInput.getText().trim() + "\n")); //add member to the current member list
                AddMemberNameInput.setText(""); //reset the textfield input
                counterDOWN--;
                counter++;
            }
        } else {
            ErrorMessageMaxMembers.setVisible(true);
        }
    }
    @FXML
    public void onEnter(ActionEvent ae){
        if (counterDOWN > 0) {
            //check that the user has entered a name
            if (!AddMemberNameInput.getText().trim().isEmpty() && !AddMemberNameInput.getText().trim().equals("") && AddMemberNameInput.getText().trim().matches("^[a-zA-Z0-9]*$")) {
                GroupMemberList.setText(GroupMemberList.getText().concat(AddMemberNameInput.getText().trim() + "\n")); //add member to the current member list
                AddMemberNameInput.setText(""); //reset the textfield input
                counterDOWN--;
                counter++;
            }
        } else {
            ErrorMessageMaxMembers.setVisible(true);
        }
    }
    @FXML
    public void saveGroupBtnActivated(ActionEvent event) throws IOException {

        if (counter < 4) {
            ErrorMessageMaxMembers.setText("Minimum members required: 4");
            ErrorMessageMaxMembers.setVisible(true);
        } else if (counter % 2 != 0) {
            ErrorMessageMaxMembers.setText("You must enter an even number of members!");
            ErrorMessageMaxMembers.setVisible(true);
        } else {
            Scanner scn = new Scanner(GroupMemberList.getText());
            List<String> membersInput = new ArrayList<>();
            while (scn.hasNext()) {
                membersInput.add(scn.next());
            }
            scn.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uk/ac/bham/vanillaimplementation/EnterPreferences.fxml"));
            //Create a controller instance
            EnterPreferencesController enterPref = new EnterPreferencesController(membersInput, GroupName_Lbl.getText());
            //Set it in the FXMLLoader
            loader.setController(enterPref);
            Parent root = loader.load();
            SaveGroup.getScene().setRoot(root);
        }
    }
}
