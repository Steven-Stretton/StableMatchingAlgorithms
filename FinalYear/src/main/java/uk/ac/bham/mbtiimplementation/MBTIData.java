package uk.ac.bham.mbtiimplementation;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;
//Gets OAuth working with the Google Sheets API
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
//Dependencies cover working with the Sheets API
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
/*
 * This class is used for making requests to the Google Sheets API.
 * Gradle is used for importing the Google API dependencies to the program.
 * This class will read the data from the Google Sheet which is connected via the Spreadsheet_ID,
 * and will create Person objects with the data contained within the Google Sheet.
 * Each Person object created in this class will be added to an ArrayList of Person objects,
 * where they will later be used to calculate personality types and create a stable matching.
 * Parts of this code were used from this source:
 * https://developers.google.com/sheets/api/quickstart/java
 * This class was developed with help from the following tutorial:
 * https://www.youtube.com/watch?v=8yJrQk9ShPg&t=175s
 */
class MBTIData {

    private List<Person> individualPool = new ArrayList<>();
    private String[][] personMatrix;
    private String SPREADSHEET_ID;
    private Sheets sheetsService;
    private int counter = 1;

    private void setSheetsService(Sheets sheet){ this.sheetsService  = sheet   ;}
    private Sheets getSheetsService()         { return this.sheetsService     ;}
    private String getSPREADSHEET_ID()         { return this.SPREADSHEET_ID    ;}
    private List<Person> getIndividualPool()   { return this.individualPool    ;}
    String[][] getPersonMatrix()       { return this.personMatrix      ;}
    /*
    Constructor for taking a Google Sheet url as input with range
     */
    MBTIData(String SPREADSHEET_ID, String range) throws IOException, GeneralSecurityException {
        this.SPREADSHEET_ID = SPREADSHEET_ID   ;
        retrieveGoogleSheets(range);
    }
    /*
    Alternative Constructor for taking a CSV file as input
     */
    MBTIData(String csvPath) throws IOException {
        retrieveCSV(csvPath);
    }
    /*
    Authorise credentials
    */
    private Credential authorise() throws IOException, GeneralSecurityException {
        InputStream in = MBTIData.class.getResourceAsStream("/credentials.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JacksonFactory.getDefaultInstance(), new InputStreamReader(in)
        );
        List<String> scopes = Collections.singletonList(SheetsScopes.SPREADSHEETS);
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                                                                    JacksonFactory.getDefaultInstance(), clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver())
                .authorize("user");
    }
    /*
    Get sheets service
     */
    private Sheets buildSheetsService() throws IOException, GeneralSecurityException{
        Credential credential = authorise();
        String APPLICATION_NAME = "Google Sheets API Quickstart";
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    /*
    Retrieve the data from Google Sheets, and for each row of data create a new Person object
     */
    private void retrieveGoogleSheets(String Range) throws IOException, GeneralSecurityException {
        setSheetsService(buildSheetsService());
        ValueRange answers = getSheetsService().spreadsheets().values()
                .get(getSPREADSHEET_ID(), Range)
                .execute();
        List<List<Object>> values = answers.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No spreadsheet data found!");
        } else {
            //This for loop goes through the spreadsheet data and adds the responses of each individual to a two-dimensional array.
            //This is done in O(n^2) - nested for loops
            for (List row : values) {
                String[][] responses = new String[50][1]; //two-dimensional array stores responses: constant read/write time [Q][R]
                for (int i = 4; i < 54; i++) {           //starts from 4 due to structure of spreadsheet (4th column onwards(50 questions))
                    responses[i - 4][0] = row.get(i).toString().substring(0, 1); //filling response with first character 'A, B, C'
                }
                Person person = new Person(counter,row.get(1).toString() + " " + row.get(2).toString(), responses);    //Created person
                counter++;
                getIndividualPool().add(person); //add each person to the pool of individuals
            }
        }
        personMatrix = new String[individualPool.size()][];
        for (Person person : getIndividualPool()) {
            person.setIndividualPool(getIndividualPool()); //each person object needs to have access to all potential partners
            List<String> list = person.generateCompatiblePartners(); //so we can calculate each persons compatible partner ranking
            list.add(person.getFullName());
            String[] listAsArray = list.toArray(new String[0]);
            personMatrix[person.getNumber()-1] = listAsArray;
        }
    }
    /*
    Retrieve the data from the CSV file, and for each row of data create a new Person object
     */
    private void retrieveCSV(String csvPath) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(csvPath));
        String line;
        while ((line = in.readLine()) != null) {
            String[] values = line.split(",");
            Person person = new Person(Integer.parseInt(values[0]), values[0], values[1]);
            getIndividualPool().add(person);
        }
        personMatrix = new String[individualPool.size()][];
        for (Person person : getIndividualPool()) {
            person.setIndividualPool(getIndividualPool());
            List<String> list = person.generateCompatiblePartners();
            list.add(person.getFullName());
            String[] listAsArray = list.toArray(new String[0]);
            personMatrix[person.getNumber()-1] = listAsArray;
        }
    }
}
