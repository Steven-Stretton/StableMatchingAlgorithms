package uk.ac.bham.stableroommates;

import javafx.animation.PauseTransition;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import uk.ac.bham.mbtiimplementation.MBTIVisualisationController;
import java.util.*;
/**
 * This program is used for solving the stable roommates problem.
 * The program will take a personMatrix, and the GUI controller, and will perform the computations on the data.
 * This program derives from Irving's SR Algorithm (1984) and is implemented from the existing pseudocode that can be found here:
 * https://uvacs2102.github.io/docs/roomates.pdf
 *
 */
public class SRAlgorithm {
    //The number of members used in this matching
    private int numberOfPersons;
    //Mapping the string values (names) of each person to a numerical value to be used throughout the algorithm
    private Map<String, Integer> nameToNumericValueMap;
    //A matrix containing each person's preferences (in order of persons 1 to n)
    private String[][] matrixString;
    //A matrix containing each person's preferences listed as an integer, rather than their String name
    private Integer[][] matrixNumber;
    //Where each person's preferences will be displayed numerically as 'best' to 'worst' partners
    private Integer[][] rank;
    //Vectors are used to keep track of each members available partners
    private Integer[] leftmost;
    private Integer[] second;
    private Integer[] rightmost;
    //A copy of the personMatrixNumber, which will be used to track preferences for each person after each stage
    private List<Integer>[] reducedPersonMatrix;
    //Will help keep track of whether a solution is possible, and will become false if at any point this changes
    private boolean solutionPossible = true;
    //Will become true when a stable solution has been found
    private boolean solutionFound = false;
    //Used in the second phase of the algorithm, where we have a person with more than one partner available
    public int firstUnmatched = 0;
    //Used in the pCycle, so we can identify when a preference cycle has been found
    public int firstRepeat;
    //Array holds all of the P&Q Members in Phase 2
    private List<Integer> pCycle = new ArrayList<>();
    private List<Integer> qCycle = new ArrayList<>();
    private Integer[] cyclePosition;
    //MVC - The GUI (View)'s controller (Controller) that is linked with this class (Model)
    private VisualgoController gui;
    private MBTIVisualisationController mbtigui;
    /**
     * Constructor for creating a new instance of the stable roommates problem with the given input.
     * The GUI controller for visualising the algorithm is also passed to update the GUI throughout the process.
     * Each list has the proposer rank themselves at the last position on the list.
     * Initialises the variables needed to solve the problem, then begins the first phase.
     *
     * @param personMatrix : the Matrix containing all of the preferences for each person
     * @param controller   : an instance of VisualgoController which will update the corresponding GUI
     */
    public SRAlgorithm(String[][] personMatrix, VisualgoController controller) {
        matrixString = personMatrix; //take the String version of the matrix as input to the algorithm
        numberOfPersons = personMatrix.length; //initialise the size of the set
        mapToNumbers(); //maps each members name to a number so we can create a numerical matrix
        matrixNumber = createNumericMatrix(); //initialising the copy of matrixString into integers
        initialiseRankMatrix(matrixNumber); //rank matrix initialise to size[n][n]
        initialiseVectors(personMatrix.length); //initialise vectors to size[n]
        reducedPersonMatrix = new ArrayList[numberOfPersons]; //an arraylist for each member
        gui = controller; //link the visualiser GUI to this code so that we can update the interface
        cyclePosition = new Integer[this.numberOfPersons];
        phase_1_reduce(proposals());

        System.out.println(Arrays.deepToString(reducedPersonMatrix));

        find();
        getSolution();
    }
    /**
     * Alternative constructor for the MBTI GUI
     */
    public SRAlgorithm(String[][] personMatrix, MBTIVisualisationController controller) {
        matrixString = personMatrix; //take the String version of the matrix as input to the algorithm
        numberOfPersons = personMatrix.length; //initialise the size of the set
        mapToNumbers(); //maps each members name to a number so we can create a numerical matrix
        matrixNumber = createNumericMatrix(); //initialising the copy of matrixString into integers
        initialiseRankMatrix(matrixNumber); //rank matrix initialise to size[n][n]
        initialiseVectors(personMatrix.length); //initialise vectors to size[n]
        reducedPersonMatrix = new ArrayList[numberOfPersons]; //an arraylist for each member
        mbtigui = controller;
        cyclePosition = new Integer[this.numberOfPersons];
        phase_1_reduce(proposals());

        find(); //move this out of the constructor [here]
        getSolution(); //move this out of the constructor too [here]
    }
    /**
     * The narration box used to output the steps of the algorithm
     */
    private TextArea getNarrationBox() {
        if (gui != null) {
            return gui.NarrationBox;
        } else {
            return mbtigui.NarrationBox;
        }
    }
    /**
     * Method draws a line to the GUI to represent the edges between nodes
     */
    public void drawLine(int start, int finish, Color colour, int strokeWidth) {
        if (gui != null) {
            gui.drawLine(start, finish, colour, strokeWidth);
        } else {
            mbtigui.drawLine(start, finish, colour, strokeWidth);
        }
    }
    /**
     * Since the members are collected through the GUI input and are in the form of Strings, in order to replicate
     * the Pascal implementation, we need to assign numerical values to each person.
     * This method will take the existing personMatrix containing each member and assign each member an integer.
     * Here, each integer will be stored with the persons original String name that was provided.
     */
    private void mapToNumbers() {
        nameToNumericValueMap = new TreeMap<>();
        for (int i = 0; i < numberOfPersons; i++) {
            //Each persons name is stored in the last index of their personMatrix.
            nameToNumericValueMap.put(matrixString[i][numberOfPersons - 1], i);
        }
    }
    /**
     * This method takes the string[][] of persons names and converts them into their corresponding number which
     * has been mapped previously in mapToNumbers. This is so we can run the data through the algorithm as a
     * numerical matrix, rather than a matrix of strings.
     *
     * @return output : a numerical version of the original String[][] personMatrixString
     */
    private Integer[][] createNumericMatrix() {
        Integer[][] output = new Integer[numberOfPersons][numberOfPersons];
        for (int i = 0; i < numberOfPersons; i++) {
            for (int j = 0; j < numberOfPersons; j++) {
                int number = nameToNumericValueMap.get(matrixString[i][j]);
                output[i][j] = number;
            }
        }
        return output;
    }
    /**
     * This method initialises the vectors which store each persons potential partners.
     * These arrays are used to hold the index position of each persons best, second-best and worst partners remaining.
     * Leftmost (best), Second (second-best), Rightmost (worst)
     */
    private void initialiseVectors(int numberOfPersons) {
        rightmost = new Integer[numberOfPersons];
        second = new Integer[numberOfPersons];
        leftmost = new Integer[numberOfPersons];
        for (int i = 0; i < numberOfPersons; i++) {
            rightmost[i] = numberOfPersons - 1; //Set rightmost to be the maximum index on preference list which is themselves (sentinel included)
            second   [i] = 1; //Set the second index (second partner) to be the second on preference list
            leftmost [i] = 0; //Set the left index (best partner) to be the first person on the preference list
        }
    }
    /**
     * This method is used for initialising the rank that represents each persons preference, for each of the other n-1 members
     */
    private void initialiseRankMatrix(Integer[][] personMatrixNumber) {
        rank = new Integer[numberOfPersons][numberOfPersons]; //matrix is of size [n][n] and of person_type (Integer)
        for (int i = 0; i < numberOfPersons; i++) {
            for (int j = 0; j < numberOfPersons; j++) {
                rank[i][personMatrixNumber[i][j]] = j;
            }
        }
    }
    /**
     * Executes the first phase of the algorithm, as described in Irving's paper.
     *
     * @param solnPossible : calculated at the proposal stage, if solution isn't possible, return null.
     * @return reducedPMatrix : return an array of ArrayLists which hold, for each person their reduced preference lists of possible partners
     */
    private void phase_1_reduce(boolean solnPossible) {
         if (solnPossible == false) { //during the proposal phase we find that somebody cannot make a proposal
            getNarrationBox().setText(getNarrationBox().getText().concat("- [No stable matching is possible!] -\n"));
            } else {
            createReducedPersonMatrix(); //create a reduced preference matrix
            checkSolutionFound(); //check if solution is found after the first phase of algorithm
        }
    }
    /*
     * This method creates a reduced person matrix, where each of the remaining 'possible partners' are added.
     *
     */
    private void createReducedPersonMatrix() {
        for (int i = 0; i < numberOfPersons ; i++) {
            //instantiate an empty ArrayList to hold all of the individuals preferences
            reducedPersonMatrix[i] = new ArrayList<>();
            for (int j = 0; j < numberOfPersons; j++) {
                //do not include any potential partner that is less desirable than the current rightmost partner already.
                //do not include any potential partner who would not accept this person (i) over their current partner.
                if (!(j > rightmost[i] || rightmost[matrixNumber[i][j]] < rank[matrixNumber[i][j]][i])) {
                    reducedPersonMatrix[i].add(matrixNumber[i][j]);
                }
            }
        }
    }
    /**
     * This stage of the algorithm will perform the initial proposals.
     * Here each member will make proposals to their next best potential partner.
     * This phase will terminate when: 1. everybody has made a proposal - solution possible
     *                                 2. someone has been rejected by everyone - solution not possible.
     * @return boolean : return true if a solution is possible, else false
     */
    private boolean proposals() {
        addText("Algorithm starting...");
        addText("\n- [START OF PROPOSAL STAGE] -\n");

        Set<Integer> set_proposed_to = new TreeSet<>();

        int person = 0;
        int proposer = 0;
        int next_choice = 0;
        int next_choice_current;

        while (person < numberOfPersons) { //we have to loop through each person in the set
            proposer = person; //each person becomes the proposer
            addText("Select " + getKeysFromValue(nameToNumericValueMap, proposer) + ".\n");
            outerloop:
            do { //make proposals
                //the next choice in the proposers list
                next_choice = getNext(proposer);
                //the rank of the proposer within the next choices' preference list
                int proposer_rank = getRank(next_choice, proposer);
                //Following lines update GUI
                addText("-> " + getKeysFromValue(nameToNumericValueMap, proposer) + "'s next choice is " + getKeysFromValue(nameToNumericValueMap, next_choice) + ".\n");
                PauseTransition proposeLines = new PauseTransition(Duration.seconds(1));
                int finalCurrentProposer = proposer;
                int finalProposersNextChoice = next_choice;
                proposeLines.setOnFinished((e) -> drawLine(finalCurrentProposer, finalProposersNextChoice, Color.YELLOW.darker(), 4));
                proposeLines.play();
                //get the current partner of next choice
                next_choice_current = getCurrent(next_choice);
                addText("-> " + getKeysFromValue(nameToNumericValueMap, next_choice) + "'s current partner is " + getKeysFromValue(nameToNumericValueMap, next_choice_current) + ".\n");

                System.out.println("The next choice rates the proposer as : " + proposer_rank);
                System.out.println("The next choice's current partner is rated as : " + getRank(next_choice, next_choice_current));

                //while the proposer is less favourable than the current partner of their next choice
                while (proposer_rank > getRank(next_choice, next_choice_current)) {
                    System.out.println("proposer less favourable\n");
                    //update GUI with rejection details
                    addText("-> " + getKeysFromValue(nameToNumericValueMap, next_choice) + " rejects " + getKeysFromValue(nameToNumericValueMap, proposer) + ".\n");
                    PauseTransition removeLines = new PauseTransition(Duration.seconds(3));
                    int finalProposersNextChoice1 = next_choice;
                    int finalCurrentProposer1 = proposer;
                    removeLines.setOnFinished((e) -> drawLine(finalProposersNextChoice1, finalCurrentProposer1,  Color.web("#1F2833"), 6));
                    removeLines.play();
                    //the proposers next choice rejects the proposer, therefore the proposers leftmost (best-person) now is incremented to the next preferred partner
                    leftmost[proposer] += 1;
                    addText("Select " + getKeysFromValue(nameToNumericValueMap, proposer) + ".\n");
                    //update next choice
                    next_choice = getNext(proposer);
                    //debug: run check here to see whether sentinel is the next choice of the proposer
                    if (detectSentinel(proposer, next_choice)) {
                        break outerloop; //if so, no stable-matching possible, break.
                    }
                    addText("-> " + getKeysFromValue(nameToNumericValueMap, proposer) + "'s next choice is " + getKeysFromValue(nameToNumericValueMap, next_choice) + ".\n");
                    //next choice is updated, so also update the current partner of the next choice
                    next_choice_current = getCurrent(next_choice);
                    addText("-> " + getKeysFromValue(nameToNumericValueMap, next_choice) + "'s current partner is " + getKeysFromValue(nameToNumericValueMap, next_choice_current) + ".\n");
                }
                System.out.println("proposer more favourable\n");

                //when the proposer is more favourable than their next choices' current partner...
                //reduce the list of next choice from the right to the left (trying to hold most desirable partner)
                rightmost[next_choice] = getRank(next_choice, proposer);
                addText("-> " + getKeysFromValue(nameToNumericValueMap, next_choice) + " accepts " + getKeysFromValue(nameToNumericValueMap, proposer) + " and rejects " + getKeysFromValue(nameToNumericValueMap, next_choice_current) + ".\n");
                PauseTransition removeLines = new PauseTransition(Duration.seconds(3));
                int finalProposersNextChoice0 = next_choice;
                int finalCurrentProposer0 = proposer;
                removeLines.setOnFinished((e) -> drawLine(finalProposersNextChoice0, finalCurrentProposer0, Color.web("#1F2833"), 6));
                removeLines.play();
                //the propoersNextChoice's current partner now needs to to propose to another member, as they have been rejected
                proposer = next_choice_current;
            } while (set_proposed_to.contains(next_choice));
            set_proposed_to.add(next_choice);
            ++person;
        }
        addText("- [END OF PROPOSAL STAGE] -\n\n");
        if (proposer == next_choice) {
            solutionPossible = true;
        }
        return solutionPossible;
    }
    /**
     * This method will detect whether a person has been rejected by everybody else.
     * This means that the algorithm will terminate after Phase 1 (during the proposals)
     * See Page 580
     * If the proposers next choice is himself, this means he has exhausted all of his proposals
     * @param proposer : the current proposer
     * @param next_choice : the proposers next choice
     */
    private boolean detectSentinel(int proposer, int next_choice) {
        if (proposer == next_choice) {
            solutionPossible = false;
            addText(getKeysFromValue(nameToNumericValueMap, proposer) + " has been rejected by everybody.\n");
            return true;
        } else {
            solutionPossible = true;
            return false;
        }
    }
    /**
     * This method is used for updating the text within the narration box.
     * @param string : the string which you wish to add to the narration box in the GUI
     */
    private void addText(String string) {
        getNarrationBox().setText(getNarrationBox().getText().concat(string));
    }
    /**
     * This method will run after the first phase of the algorithm.
     * Firstly, it will look through the reduced preference matrix (after proposals have been accepted and rejected)
     * It will use helper methods to detect whether or not there is an individual within the group that has not
     * achieved a stable (mutual) matching, in which they have proposed to the same person that proposed to themselves.
     */
    private void find() {
        //whilst we know that there exists a possible solution, but the solution has not yet been found, further reduction needed
        System.out.println("Line 308: Solution Possible: " + solutionPossible + " Solution Found: " + solutionFound);
        while (solutionPossible && !solutionFound) {
            resetCycles(); //may not be the first time that the p and q values are used
            System.out.println("Line 315: PCycle: " + pCycle);
            System.out.println("Line 315: QCycle: " + qCycle);

            findFirstUnmatched();

            checkSolutionFound(); //if cycle is empty then solution found = true
            printCycles(); //print empty cycles to the gui
            if(firstUnmatched > numberOfPersons) {
                solutionFound = true;
            } else {
                seekCycle(); //Fill in the p and q cycles for Phase 2 of the Algorithm
                phase_2_reduce();
                checkSolutionPossible(); //After reduction - is solution still possible?
            }
        }
    }
    /**
     * This method will identify a cycle in the preferences.
     * We add the first unmatched person to the pCycle.
     * Then, we repeatedly:
     *      Insert p's second preference into q
     *      Insert q's last preference into p
     *   Until the initial pMember is inserted into p.
     */
    private void seekCycle() {
        //the firstUnmatched person
        System.out.println("FU: " + firstUnmatched);
        int pMember = firstUnmatched;
        System.out.println("PMember: " + pMember);
        int count = 0;
        //loop until pMember is repeated
        while(!(pCycle.contains(pMember))) {
            pCycle.add(pMember);
            printCycles();
            cyclePosition[pMember] = count; //saves the position of the member in the cycle
            count++;
            try {
                int qMember = reducedPersonMatrix[pMember].get(1); //get the second available partner for this person
                qCycle.add(qMember); //place the pMember's second available partner into qCycle
                printCycles();
                pMember = reducedPersonMatrix[qMember].get(reducedPersonMatrix[qMember].size() - 1); //get the latest qMembers LEAST preferable available
            } catch(IndexOutOfBoundsException e) {
                solutionPossible = false;
            }
        }
        firstRepeat = pMember;
    }
    /**
     * If each person only has one available partner after the first phase of reduction, then a stable-matching
     * has already been found, and Phase 2 of the algorithm is unnecessary. However, if a member of the group
     * does have more than one available partner then Phase 2 of the algorithm is needed to further reduce the list.
     * The firstUnmatched variable will be updated so that we can identify preference cycles.
     */
    private void findFirstUnmatched() {
        for (int i = firstUnmatched; i < reducedPersonMatrix.length; i++) {
            if (reducedPersonMatrix[i].size() != 1) {
                firstUnmatched = i;
                System.out.println(getKeysFromValue(nameToNumericValueMap, i));
                addText(matrixString[i][matrixString.length-1] + " has multiple potential partners!\n" );
                addText("Checking for preference cycle...\n\n");
                return;
            }
        }
        solutionFound = true;
    }
    /**
     * This method performs phase 2 of the algorithm
     **/
    private void phase_2_reduce() {
        addText("- [START PHASE 2] -\n");
        int firstPersonPositionInCycle = cyclePosition[firstRepeat];
        int firstInCycle = firstPersonPositionInCycle + 1;
        //iterate through the pCycle
        for (int i = firstInCycle; i < pCycle.size(); i++) {
            diagonalRejection(i, i - 1); //each p rejects q-1
            addText(getKeysFromValue(nameToNumericValueMap, pCycle.get(i)) + " rejects " + getKeysFromValue(nameToNumericValueMap, qCycle.get(i - 1)) + "\n");
            if(checkSolutionPossible() == false) { //is solution still possible after this rejection?
                addText(getKeysFromValue(nameToNumericValueMap, pCycle.get(i)) + " has exhausted all possible partners.\n\n");
                return;
            } else {
                //get leftmost partner of proposer
                int nextChoice = reducedPersonMatrix[pCycle.get(i)].get(0);
                removeAfter(nextChoice, i); //remove everyone in nextChoice's list after i
            }
        }
        diagonalRejection(firstPersonPositionInCycle, qCycle.size() - 1);
        addText(getKeysFromValue(nameToNumericValueMap, pCycle.get(firstPersonPositionInCycle)) + " rejects " + getKeysFromValue(nameToNumericValueMap, qCycle.get(qCycle.size() - 1)) + "\n");
        removeAfter(reducedPersonMatrix[pCycle.get(firstPersonPositionInCycle)].get(0), firstPersonPositionInCycle);
        if(cyclePosition[firstRepeat] > 0) {
            firstUnmatched = pCycle.get(cyclePosition[firstRepeat] - 1);
        }else {
            findFirstUnmatched();
        }
    }
    /**
     * This method removes all of the members after the given person (removeAfter) in the preference list of (member)
     *
     * @param member : the person who's preference list we are reducing
     * @param removeAfter : the person who we want to appear last on 'members' preference list
     */
    private void removeAfter(int member, int removeAfter) {
        try {
            while(reducedPersonMatrix[member].get(reducedPersonMatrix[member].size()-1) != pCycle.get(removeAfter)) {
                reducedPersonMatrix[member].remove(reducedPersonMatrix[member].size()-1);
            }
        } catch (IndexOutOfBoundsException ex) {
            solutionPossible = false;
        }
    }
    /**
     * This method is used for removing preference cycles within the second phase of the algorithm
     * Therefore, p[i] will reject q[i-1]
     *
     * @param p : current person in cycle P
     * @param q : current person in cycle Q
     */
    private void diagonalRejection(int p, int q) {
        if (checkSolutionFound() == false) {
            reducedPersonMatrix[pCycle.get(p)].remove(qCycle.get(q));
            reducedPersonMatrix[qCycle.get(q)].remove(pCycle.get(p));
        }
    }
    /**
     * This method retrieves the name (key: string) which has been mapped to the persons number (value: int)
     * This method is used so that at any point in the algorithm we can print out the name of the persons being used.
     *
     * @param map : containing the mapped names of each member and their respective integer value
     * @param value : the members number, who's name we're trying to find
     * @return name : return the name of the members number
     */
    private String getKeysFromValue(Map<String, Integer> map, Integer value){
        String name = "";
        for(String members : map.keySet()){
            if(map.get(members).equals(value)) {
                name = members;
            }
        }
        return name;
    }
    /**
     * This method will check if a solution is possible considering the current state of the personMatrix.
     */
    private boolean checkSolutionPossible() {
        if(!solutionPossible) {
            return false;
        } else {
            for(int i = 0; i < numberOfPersons; i++) {
                if (reducedPersonMatrix[i].isEmpty() || reducedPersonMatrix[i].get(0) == i) { //reducedPMatrix is null
                    solutionPossible = false;
                    return false;
                }
            }
            return true;
        }
    }
    /**
     * This method will iterate through each persons preference list to check the size of the remaining
     * potential partners. If each preference list is of size = 1 then a stable matching has been found.
     * @return
     */
    private boolean checkSolutionFound() {
        for(int i = 0; i < numberOfPersons; i++) {
            if(reducedPersonMatrix[i].size() != 1) {
                solutionFound = false;
                return false;
            }
        }
        solutionFound = true;
        return true;
    }
    /**
     * This method will check after the algorithm has terminated whether a solution has been found.
     * If the solution has been found the GUI will be updated with the results of the matching.
     */
    private void getSolution() {
        if (solutionFound) {
            PauseTransition wait = new PauseTransition(Duration.seconds(4));
            wait.setOnFinished((e) -> {
                addText("\nSolution:\n");
                for (int i = 0; i < this.numberOfPersons; i++) {
                    addText(getKeysFromValue(nameToNumericValueMap, i) + " <-> " + getKeysFromValue(nameToNumericValueMap, reducedPersonMatrix[i].get(0)) + "\n");
                    drawLine(i, reducedPersonMatrix[i].get(0), Color.GREEN, 5);
                }
            });
            wait.play();
        } else {
            addText("- [No stable matching is possible!] -\n");
        }
    }
    /**
     * This method is used for printing the p and q cycles to the narration box output in the GUI.
     */
    private void printCycles() {
        List<String> pNames = new ArrayList<>();
        List<String> qNames = new ArrayList<>();
        for (int personNumber : pCycle) {
            pNames.add(getKeysFromValue(nameToNumericValueMap, personNumber));
        }
        for (int personNumber : qCycle) {
            qNames.add(getKeysFromValue(nameToNumericValueMap, personNumber));
        }
        addText("P : " + pNames.toString() + "\n");
        addText("Q : " + qNames.toString() + "\n\n");
    }
    /**
     * Resets the values of the p and q arrays to search for a new cycle
     */
    private void resetCycles() {
        pCycle.clear();
        qCycle.clear();
    }
    /*
    Method to retrieve the next choice of the proposer
     */
    public Integer getNext(int proposer) {
        int nextChoice = matrixNumber[proposer][leftmost[proposer]];
        return nextChoice;
    }
    /*
    Method to retrieve the rank of the next choice from proposer
     */
    public Integer getRank(int x, int y) {
        int nextChoiceRank = rank[x][y];
        return nextChoiceRank;
    }
    /*
    Method to retrieve the current partner of person
     */
    public Integer getCurrent(int person) {
        int currentPartner = matrixNumber[person][rightmost[person]];
        return currentPartner;
    }
}
