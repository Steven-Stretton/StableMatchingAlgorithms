package uk.ac.bham.mbtiimplementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class will be used to create Person objects to be used within the stable-matching algorithm.
 */
public class Person {

    //General information and questionnaire responses
    private String fullName;
    private String[][] responses;
    private int number; //refer to the Persons number throughout the algorithm

    //Matching algorithm variables
    private List<Person> individualPool;            //The complete pool of other individuals used in the matching algorithm

    //Personality trait scores for calculating personality type
    private int extroversion;
    private int introversion;
    private int sensing;
    private int intuition;
    private int thinking;
    private int feeling;
    private int judging;
    private int perceiving;
    private String personalityType;                 //Holds their M.B.T.I compatibility type
    private List<String> preferenceList;            //This is a list of most-compatible TYPES for this person, NOT the people themselves
    /*
    CONSTRUCTOR
     */
    public Person(int number, String fullName, String[][] responses) {
        this.number = number;
        this.fullName = fullName;
        this.responses = responses;
        setPersonalityType();
        setRankingPreference();
    }
    /**
     * This constructor is used for instatiating Person objects from the large dataset discussed in report.
     * @param number : the data tested is anonymous, therefore assign an integer value to each member
     * @param personalityType : personaity type is pre-calculated and provided in data
     */
    public Person(int number, String name, String personalityType) {
        this.number = number;
        this.fullName = name;
        this.personalityType = personalityType;
        setRankingPreference(); //calculates the order in which they are compatible with other personality types
    }

    public String toString()                   { return this.fullName                ;}
    /*
    GETTERS
     */
    int getNumber()                            { return this.number                  ;}
    String getPersonalityType()                { return this.personalityType         ;}
    String getFullName()                       { return this.fullName                ;}
    /*
    SETTERS
     */
    void setIndividualPool(List<Person> individualPool)              { this.individualPool = individualPool;                    }
    /*
    This method takes the M.B.T.I scoring sheet, and depending on the individuals responses to each question,
    assigns a value to a personality trait.
    */
    private void setPersonalityType() {
        String personalityType = "";
        if (responses[0][0] .equals("A")) { extroversion+= 2; } else { introversion += 2; }
        if (responses[1][0] .equals("A")) { sensing     += 2; } else { intuition    += 2; }
        if (responses[2][0] .equals("A")) { extroversion+= 2; } else { introversion += 2; }
        if (responses[3][0] .equals("A")) { feeling     += 1; } else { thinking     += 2; }
        if (responses[4][0] .equals("A")) { intuition   += 1; } else { sensing      += 1; }
        if (responses[5][0] .equals("A")) { extroversion+= 2; } else { introversion += 1; }
        if (responses[6][0] .equals("A")) { judging     += 1; } else { perceiving   += 1; }
        if (responses[7][0] .equals("A")) { judging     += 1; } else { perceiving   += 2; }
        if (responses[8][0] .equals("A")) { extroversion+= 2; } else { introversion += 1; }
        if (responses[9][0] .equals("A")) { sensing     += 1; } else { intuition    += 2; }
        if (responses[10][0].equals("A")) { judging     += 2; } else { perceiving   += 1; }
        if (responses[11][0].equals("A")) { sensing     += 1; } else { intuition    += 2; }
        if (responses[12][0].equals("A")) { extroversion+= 1; } else { introversion += 2; }
        if (responses[13][0].equals("A")) { feeling     += 1; } else { thinking     += 2; }
        if (responses[14][0].equals("A")) { sensing     += 1; }
        if (responses[15][0].equals("A")) { extroversion+= 2; } else { introversion += 2; }
        if (responses[16][0].equals("A")) { judging     += 2; } else { perceiving   += 2; }
        if (responses[17][0].equals("A")) { judging     += 1; } else { perceiving   += 1; }
        if (responses[18][0].equals("A")) { judging     += 1; } else { perceiving   += 1; }
        if (responses[19][0].equals("A")) { sensing     += 2; } else { intuition    += 2; }
        if (responses[20][0].equals("A")) { extroversion+= 2; } else { introversion += 2; }
        if (responses[21][0].equals("A")) { feeling     += 2; } else { thinking     += 2; }
        if (responses[22][0].equals("A")) { intuition   += 1; } else { sensing      += 2; }
        if (responses[23][0].equals("A")) { extroversion+= 1; } else { introversion += 1; }
        if (responses[24][0].equals("A")) { judging     += 1; } else { perceiving   += 1; }
        if (responses[25][0].equals("A")) { extroversion+= 1; }
        if (responses[26][0].equals("A")) { judging     += 2; } else { perceiving   += 2; }
        if (responses[27][0].equals("A")) { sensing     += 2; } else { intuition    += 1; }
        if (responses[28][0].equals("A")) { introversion+= 2; } else { extroversion += 2; }
        if (responses[29][0].equals("A")) { thinking    += 2; } else { feeling      += 1; }
        if (responses[30][0].equals("B")) { sensing     += 2; }
        if (responses[31][0].equals("A")) { thinking    += 1; } else { feeling      += 1; }
        if (responses[32][0].equals("B")) { thinking    += 2; }
        if (responses[33][0].equals("A")) { judging     += 2; } else { perceiving   += 2; }
        if (responses[34][0].equals("A")) { sensing     += 2; } else { intuition    += 1; }
        if (responses[35][0].equals("A")) { introversion+= 1; } else { extroversion += 2; }
        if (responses[36][0].equals("A")) { thinking    += 1; } else { feeling      += 2; }
        if (responses[37][0].equals("B")) { sensing     += 2; }
        if (responses[38][0].equals("A")) { thinking    += 1; } else { feeling      += 1; }
        if (responses[39][0].equals("A")) { feeling     += 1; } else { thinking     += 2; }
        if (responses[40][0].equals("A")) { judging     += 2; } else { perceiving   += 2; }
        if (responses[41][0].equals("A")) { sensing     += 1; } else { intuition    += 2; }
        if (responses[42][0].equals("A")) { introversion+= 1; } else { extroversion += 1; }
        if (responses[43][0].equals("A")) { thinking    += 1; } else { feeling      += 2; }
        if (responses[44][0].equals("B")) { sensing     += 2; }
        if (responses[45][0].equals("A")) { thinking    += 2; }
        if (responses[46][0].equals("A")) { feeling     += 1; } else { thinking     += 2; }
        if (responses[47][0].equals("A")) { sensing     += 1; } else { intuition    += 1; }
        if (responses[48][0].equals("A")) { thinking    += 2; } else { feeling      += 1; }
        if (responses[49][0].equals("A")) { thinking    += 2; }

        //Depending on each traits scores, the personality type is calculated
        if (introversion > extroversion || introversion == extroversion) { personalityType += "I"; } else { personalityType += "E"; }
        if (   intuition > sensing      ||    intuition == sensing     ) { personalityType += "N"; } else { personalityType += "S"; }
        if (    thinking > feeling                                     ) { personalityType += "T"; } else { personalityType += "F"; }
        if (  perceiving > judging      ||   perceiving == judging     ) { personalityType += "P"; } else { personalityType += "J"; }
        this.personalityType = personalityType;
    }
    /*
     * Sets the preference ranking for the individual depending on their personality types compatibility with others personality types.
     * These rankings are derived from the M.B.T.I compatibility scale which can be found at:
     * https://drive.google.com/drive/folders/1q4UZOaTYf6kySNG4mTU8QnEgGbUYcUDl
     */
    private void setRankingPreference() {
        List<String> preference;
        switch (personalityType) {
            case "INFP":
                preference = Arrays.asList("ENFJ","ENTJ","ENTP","INTP","INTJ","INFJ","ENFP","INFP","ESFP","ISFP","ESTJ","ISTJ","ESFJ","ISFJ","ESTP","ISTP");
                this.preferenceList = preference;
                break;
            case "ENFP":
                preference = Arrays.asList("INTJ","INFJ","INTP","INFP","ENTP","ENTJ","ENFJ","ENFP","ISTJ","ISFJ","ISTP","ISFP","ESFP","ESTP","ESTJ","ESFJ");
                this.preferenceList = preference;
                break;
            case "ENTP":
                preference = Arrays.asList("INFJ","INTJ","ENFJ","ENTJ","ENTP","INTP","INFP","ENFP","ISFP","ESTP","ESFP","ISTP","ESFJ","ESTJ","ISTJ","ISFJ");
                this.preferenceList = preference;
                break;
            case "INFJ":
                preference = Arrays.asList("ENFP","ENTP","INFP","INFJ","ENFJ","INTJ","ENTJ","INTP","ISFP","ESFP","ISTP","ESTP","ISFJ","ESFJ","ISTJ","ESTJ");
                this.preferenceList = preference;
                break;
            case "INTJ":
                preference = Arrays.asList("ENTP","ENFP","INTP","ENTJ","INTJ","ENFJ","INFJ","INFP","ESTP","ESFP","ISFP","ISTP","ISFJ","ESTJ","ESFJ","ISTJ");
                this.preferenceList = preference;
                break;
            case "ENFJ":
                preference = Arrays.asList("ISFP","INFP","INTJ","ENFP","INFJ","ENFJ","ENTP","INTP","ENTJ","ESFJ","ISTJ","ESTJ","ISFJ","ESFP","ISTP","ESTP");
                this.preferenceList = preference;
                break;
            case "ENTJ":
                preference = Arrays.asList("INFP","INTP","INTJ","ENTJ","INFJ","ENTP","ENFJ","ENFP","ISFP","ISTP","ESFJ","ISTJ","ISFJ","ESTJ","ESFP","ESTP");
                this.preferenceList = preference;
                break;
            case "INTP":
                preference = Arrays.asList("ESTJ","ENTJ","ENFJ","INFP","INFJ","ENTP","INTP","INTJ","ENFP","ISFP","ISTP","ESTP","ESFP","ISTJ","ESFJ","ISFJ");
                this.preferenceList = preference;
                break;
            case "ISFP":
                preference = Arrays.asList("ESTJ","ESFJ","ENFJ","INTJ","ENTJ","INTP","ENTP","ISFJ","ISTJ","ISFP","ESFP","ISTP","ESTP","INFP","ENFP","INFJ");
                this.preferenceList = preference;
                break;
            case "ESFP":
                preference = Arrays.asList("ISTJ","ISFJ","ESTJ","ESFJ","ENTJ","ENTP","INTJ","INTP","ESTP","ISFP","ESFP","ISTP","INFJ","ENFJ","INFP","ENFP");
                this.preferenceList = preference;
                break;
            case "ESTP":
                preference = Arrays.asList("ISTJ","ISFJ","INTJ","ENTJ","INTP","ENTP","ESFJ","ESTJ","ISTP","ESTP","ESFP","ISFP","ENFP","INFP","INFJ","ENFJ");
                this.preferenceList = preference;
                break;
            case "ISTP":
                preference = Arrays.asList("ESTJ","ESFJ","INTJ","ISFJ","ENTJ","ISTJ","INTP","ENTP","ISFP","ESFP","ESTP","ISTP","ENFJ","INFJ","ENFP","INFP");
                this.preferenceList = preference;
                break;
            case "ISFJ":
                preference = Arrays.asList("ESFP","ESTP","ISFJ","ESTJ","ESFJ","ISTJ","ISFP","ISTP","ENTJ","ENTP","INTJ","INTP","ENFJ","INFJ","ENFP","INFP");
                this.preferenceList = preference;
                break;
            case "ISTJ":
                preference = Arrays.asList("ESFP","ESTP","ESFJ","ISFJ","ESTJ","ISTJ","ISFP","ISTP","ENTJ","INTJ","INTP","ENTP","INFP","ENFP","INFJ","ENFJ");
                this.preferenceList = preference;
                break;
            case "ESFJ":
                preference = Arrays.asList("ISFP","ISTP","ESFJ","ESTJ","ISFJ","ISTJ","ENTJ","ESFP","ESTP","ENTP","INTJ","INTP","ENFP","INFJ","ENFJ","INFP");
                this.preferenceList = preference;
                break;
            case "ESTJ":
                preference = Arrays.asList("INTP","ISTP","ISFP","ISTJ","ISFJ","ESFJ","ESTJ","ENTJ","ESFP","ESTP","ENTP","INTJ","INFP","ENFJ","ENFP","INFJ");
                this.preferenceList = preference;
                break;
        }
    }
    /*
    This method will loop through the proposers' compatibility list and add the most compatible persons to the list first,
    with the least compatible persons to the end of the list. Where there is a tie, the persons that are added are the first
    persons iterated through with a compatible personality type (first-come-first-serve).
    Time Complexity: O(n^2)
    */
    ArrayList<String> generateCompatiblePartners() {
        ArrayList<String> compatiblePartners = new ArrayList<>();
        //loops through the proposers preference list, to find the best compatible trait
        for (String traitToFind : preferenceList) {
            for (Person receiver : individualPool) {
                //loops through individual pool to find person with the current most compatible trait
                if (receiver.getPersonalityType().equals(traitToFind) && !receiver.equals(this)) {
                    //add to the list (people will be added in order of their compatibility to this person)
                    compatiblePartners.add(receiver.getFullName());
                }
            }
        }
        return compatiblePartners;
    }
}
