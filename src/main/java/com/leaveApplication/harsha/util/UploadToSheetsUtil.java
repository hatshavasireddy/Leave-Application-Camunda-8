package com.leaveApplication.harsha.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

@Service
public class UploadToSheetsUtil {
	
	private static final String APPLICATION_NAME = "Leave Request";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
	private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		InputStream in = UploadToSheetsUtil.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if (in == null) {
			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
						.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
						.setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}
	
	public void uploadDataToSheet(String employeeEmailId,String managerEmailId,String startDate,String endDate,String managerComment) throws IOException, GeneralSecurityException {
	    // Build a new authorized API client service.
	    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
	    final String spreadsheetId = "1PHJv7bpipB9yeMEmx_rkXf9zhGebjpTOwjxEY8OqJTY";
	    final String range = "Sheet1!A:E";
	    Sheets service =
	        new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
	            .setApplicationName(APPLICATION_NAME)
	            .build();
	    AppendValuesResponse result = null;
	    try {
	    	List<Object> cellValues = new ArrayList<>();
	    	cellValues.add(0, employeeEmailId);
	    	cellValues.add(1, managerEmailId);
	    	cellValues.add(2, startDate);
	    	cellValues.add(3, endDate);
	    	cellValues.add(4, managerComment);
	    	List<List<Object>> values = new ArrayList<List<Object>>();
	    	values.add(cellValues);
	    	System.out.println(values.toString());
	    	ValueRange body = new ValueRange()
	          .setValues(values);
	      result = service.spreadsheets().values().append(spreadsheetId, range, body)
	          .setValueInputOption("RAW")
	          .execute();
	      System.out.printf(result.getTableRange());
	    } catch (GoogleJsonResponseException e) {
	      // TODO(developer) - handle error appropriately
	      GoogleJsonError error = e.getDetails();
	      if (error.getCode() == 404) {
	        System.out.printf("Spreadsheet not found with id '%s'.\n", spreadsheetId);
	      } else {
	        throw e;
	      }
	    }
	  }
}
