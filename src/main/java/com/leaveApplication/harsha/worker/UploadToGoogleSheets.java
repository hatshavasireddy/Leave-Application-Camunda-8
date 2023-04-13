package com.leaveApplication.harsha.worker;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.leaveApplication.harsha.util.SendMailUtil;
import com.leaveApplication.harsha.util.UploadToSheetsUtil;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;

@Component
public class UploadToGoogleSheets {

	@Autowired
	UploadToSheetsUtil sheetsUpDate;
	
	@Autowired
	SendMailUtil mailSend;

	@ZeebeWorker(type = "uploadToSheets", autoComplete = true)
	public void handleJobUploadToSheets(final JobClient client, final ActivatedJob job)
			throws IOException, GeneralSecurityException {
		String managerComment = (String) job.getVariablesAsMap().get("result");
		String employeeEmailId = (String) job.getVariablesAsMap().get("employeeEmailId");
		String managerEmailId = (String) job.getVariablesAsMap().get("managerEmailId");
		String startDate = (String) job.getVariablesAsMap().get("startDate");
		String endDate = (String) job.getVariablesAsMap().get("endDate");
		sheetsUpDate.uploadDataToSheet(employeeEmailId, managerEmailId, startDate, endDate, managerComment);
	}

	@ZeebeWorker(type = "rejectMailSend", autoComplete = true)
	public void handleJobRejectMailSend(final JobClient client, final ActivatedJob job) {
		String managerComment = (String) job.getVariablesAsMap().get("managerComment");
		String employeeEmailId = (String) job.getVariablesAsMap().get("employeeEmailId");
		String managerEmailId = (String) job.getVariablesAsMap().get("managerEmailId");
		mailSend.mailConnect(employeeEmailId, managerEmailId, managerComment, "Leave Request Rejected");
	}

	@ZeebeWorker(type = "approveMailSend", autoComplete = true)
	public void handleJobApproveMailSend(final JobClient client, final ActivatedJob job) {
		String managerComment = (String) job.getVariablesAsMap().get("managerComment");
		String employeeEmailId = (String) job.getVariablesAsMap().get("employeeEmailId");
		String managerEmailId = (String) job.getVariablesAsMap().get("managerEmailId");
		mailSend.mailConnect(employeeEmailId, managerEmailId, managerComment, "Leave Request Approved");
	}

}
