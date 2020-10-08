package uk.ac.bham.stablemarriage;

import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.*;

/**
 * This class is the logic behind the Stable Marriage Problem (Gale-Shapley)
 * This will be used for matching experienced programmers to non-experienced programmers.
 * This can create matching's which will ensure that experienced members and inexperienced members are mixed.
 *
 * @author StevenStretton
 */
public class SMAlgorithm {
    //Holds the information of Preferences
    private String[] members;
    private String[][] preferenceMatrix;
    private Integer[][] numericalPMatrix;
    private int unmatched = 0; //number of members unmatched
    private int setSize;
    private SMVisualisationController controller;

    //Constructor for instantiating object from CSV file
    public SMAlgorithm(String[] members, Integer[][] numericalPMatrix, SMVisualisationController controller, String[][] preferenceMatrix) throws IOException {
        this.members = members;
        this.preferenceMatrix = preferenceMatrix;
        this.numericalPMatrix = numericalPMatrix;
        this.setSize = numericalPMatrix.length/2;
        this.controller = controller;
        this.runStableMarriage();
    }
    /**
     * This method performs the Stable Marriage Algorithm.
     * This code is adapted from: geeksforgeeks.org/stable-marriage-problem
     *
     */
    public void runStableMarriage() {
        //Stores inexperienced members, which have been matched to an experienced partner.
        //If the value is -1 this indicates that the ith experienced member is unpartnered.
        int experiencedMembersPartners[] = new int[setSize];
        //Stores availability of inexperienced members.
        //If the ith index is false, this indicates that the ith inexperienced member is unpartnered
        boolean inexperiencedAvailability[] = new boolean[setSize];
        Arrays.fill(experiencedMembersPartners, -1); // Initialize experienced members as being free (unpartnered)
        unmatched = setSize;

        while (!checkComplete()) {  // While there is at least 1 unmatched person (not completed matchings)
            int inexperiencedMember;
            for (inexperiencedMember = 0; inexperiencedMember < setSize; inexperiencedMember++) {
                if (inexperiencedAvailability[inexperiencedMember] == false) {
                    break;
                }
            }
            controller.NarrationBox.setText(controller.NarrationBox.getText().concat("Selecting " + members[inexperiencedMember] + "\n"));
            for (int experiencedMember = 0 ; experiencedMember < setSize && inexperiencedAvailability[inexperiencedMember] == false ; experiencedMember++) {
                int nextPreferred = numericalPMatrix[inexperiencedMember][experiencedMember];
                controller.NarrationBox.setText(controller.NarrationBox.getText().concat(" -> Choosing " + members[nextPreferred] + "\n"));
                if (experiencedMembersPartners[nextPreferred-setSize] == -1) {
                    controller.NarrationBox.setText(controller.NarrationBox.getText().concat(" -> " + members[nextPreferred] + " is not stable, engage to " + members[inexperiencedMember] + "\n\n"));
                    experiencedMembersPartners[nextPreferred-setSize] = inexperiencedMember;
                    //drawline inexperienced --> next preferred
                    controller.drawLine(inexperiencedMember, nextPreferred-setSize, Color.YELLOW, 2);
                    inexperiencedAvailability[inexperiencedMember] = true;
                    unmatched--;
                } else {
                    int currentEngagement = experiencedMembersPartners[nextPreferred-setSize];
                    if (experiencedMemberPrefersCurrentOverNew(numericalPMatrix, nextPreferred, inexperiencedMember, currentEngagement) == false) {
                        controller.NarrationBox.setText(controller.NarrationBox.getText().concat(" -> " + members[nextPreferred] + " is more stable with " + members[inexperiencedMember] + " than " + members[currentEngagement] + "  - stabilising again\n\n"));
                        experiencedMembersPartners[nextPreferred-setSize] = inexperiencedMember;
                        controller.drawLine(inexperiencedMember, nextPreferred-setSize, Color.YELLOW, 2); //draw new engagement
                        inexperiencedAvailability[inexperiencedMember] = true; //new assignment now becomes unavailable
                        controller.drawLine(currentEngagement, nextPreferred-setSize, Color.web("#1F2833"), 2); //erase previous engagement
                        inexperiencedAvailability[currentEngagement] = false; //previous assignment now becomes available
                    } else {
                        controller.NarrationBox.setText(controller.NarrationBox.getText().concat(" -> " + members[nextPreferred] + " is more stable with their current partner, " + members[currentEngagement] + " than " + members[inexperiencedMember] + "  - reject proposal\n"));
                    }
                }
            }
        }
        for (int i = 0 ; i < setSize ; i++) {
            controller.drawLine(experiencedMembersPartners[i], i, Color.GREEN, 2);
        }
    }

    boolean experiencedMemberPrefersCurrentOverNew(Integer prefer[][], int experiencedMember, int inexperienced1, int inexperienced2) {
        //Check whether experiencedMember prefers inexperienced1 over current assignment
        for (int i = 0; i < setSize; i++) {
            //If current engagement comes before inexperienced1 in the experiencedMember's list
            //Then experiencedMember prefers their current assignment.
            if (prefer[experiencedMember][i] == inexperienced2) {
                return true;
            }
            //If new inexperienced Member comes before current assignment in experiencedMember's list
            //Then experiencedMember prefers new member
            if (prefer[experiencedMember][i] == inexperienced1) {
                return false;
            }
        }
        return false;
    }
    /**
     * Checks whether all of the matching's have been completed.
     * @return boolean : true if the matching's are completed (every person has a partner)
     *                   false if the matching's are incomplete (some person is still available)
     */
    boolean checkComplete() {
        if (unmatched > 0) {
            return false;
        } else {
            return true;
        }
    }
}
