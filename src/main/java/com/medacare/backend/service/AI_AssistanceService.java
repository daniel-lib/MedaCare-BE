package com.medacare.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;

import com.medacare.backend.model.User;

import jakarta.annotation.PostConstruct;

import com.medacare.backend.dto.AiAssistantResponseData;
import com.medacare.backend.model.Patient;
import org.springframework.beans.factory.annotation.Value;

@Service
public class AI_AssistanceService {
  private final PatientService patientService;
  private final AuthenticationService authenticationService;

  private RestClient restClient;

  @Value("${gemini.key}")
  private String GEMINI_KEY;

  @Value("${gemini.url}")
  private String GEMINI_URL;

  public AI_AssistanceService(PatientService patientService,
      AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
    this.patientService = patientService;
  }

  public String adviseForSearch(@RequestBody String prompt) {

    restClient = RestClient.builder()
        .baseUrl(GEMINI_URL + "?key=" + GEMINI_KEY)
        .defaultHeader("Content-Type", "application/json")
        .build();

    String symptoms = prompt.isBlank() ? "" : "The patient has the following symptoms: " + prompt + ".";
    Patient patientProfile = patientService.getCurrentUserPatientProfile();
    String roleInstruction = "Assume the role of a doctor. The patient is looking for specialist. give them advice on what"
        +
        " kind of specialist they might need based on the following context.";
    String summarizedMedicalHistory = "The patient had reported that they have medical history of "
        + patientProfile.getMedicalHistory();
    String profile = "The patient is " + patientProfile.getAge() + " years old and has the following medical history: "
        +
        patientProfile.getMedicalHistory() + ". " + symptoms;
    String finalPrompt = roleInstruction + profile + summarizedMedicalHistory + prompt
        + " What kind of specialist do you think the patient should see? Please don't ask follow up questions. Don't act out of character. Don't ask for more information. Just give them the advice they need. Don't act out the character of a doctor.";
    String body = """
                    {
          "contents": [{
            "parts": [{
              "text": "%s"
            }]
          }]
        }
                    """.formatted(finalPrompt);

    return restClient.post()
        .body(body)
        .retrieve()
        .toEntity(AiAssistantResponseData.class)
        .getBody().getCandidates().get(0).getContent().getParts().get(0).getText();
    // .body(String.class);

  }

  public String prompt(@RequestBody String prompt) {
    String roleInstruction = "Assume the role of a doctor. Answer the following question. ";

    String finalPrompt = roleInstruction + prompt;
    String body = """
                    {
          "contents": [{
            "parts": [{
              "text": "%s"
            }]
          }]
        }
                    """.formatted(finalPrompt);

    return restClient.post()
        .body(body)
        .retrieve()
        .toEntity(AiAssistantResponseData.class)
        .getBody().getCandidates().get(0).getContent().getParts().get(0).getText();

  }

  public String ask(String prompt) {
    return prompt(prompt);
  }

}