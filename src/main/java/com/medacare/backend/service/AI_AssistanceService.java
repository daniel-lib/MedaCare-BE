package com.medacare.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;

import com.medacare.backend.model.User;

import jakarta.annotation.PostConstruct;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medacare.backend.dto.AiAssistantResponseData;
import com.medacare.backend.model.Patient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;

@Service
@Scope("singleton")
public class AI_AssistanceService {
  private final AuthenticationService authenticationService;
  private final Map<Long, String> previousConversation;

  private RestClient restClient;

  @Value("${gemini.key}")
  private String GEMINI_KEY;

  @Value("${gemini.url}")
  private String GEMINI_URL;

  public AI_AssistanceService(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
    this.previousConversation = new HashMap<>();
  }

  public String adviseForSearch(@RequestBody String prompt, Patient currentPatient) {

    restClient = RestClient.builder()
        .baseUrl(GEMINI_URL + "?key=" + GEMINI_KEY)
        .defaultHeader("Content-Type", "application/json")
        .build();

    String symptoms = prompt.isBlank() ? "" : "The patient says: " + prompt + ". ";
    Patient patientProfile = currentPatient;
    String roleInstruction = "Assume the role of a doctor. The patient is looking for specialist. give them advice on what"
        +
        " kind of specialist they might need based on the following context.";
    // String summarizedMedicalHistory = "The patient had reported that they have
    // medical history of "
    // + patientProfile.getMedicalHistory();
    String profile = "The patient is " + patientProfile.getAge() + " years old and has the following medical history: "
        +
        patientProfile.getMedicalHistory() + ". " + symptoms;
    String finalPrompt = previousConversation.containsKey(patientProfile.getId())
        ? "for context, previous conversation with you was as follows: "
            + previousConversation.get(patientProfile.getId())
            + " and please aknowledge the previous conversation if the patient is asking for a follow up question"
        : "";
    finalPrompt += roleInstruction + profile /* + summarizedMedicalHistory + prompt */
        + " What kind of specialist do you think the patient should see?"
        + " Don't act out of character.Don't act out the character of a doctor. Just give them the advice they need.";
    String body = """
                    {
          "contents": [{
            "parts": [{
              "text": "%s"
            }]
          }]
        }
                    """.formatted(finalPrompt);
    StringBuilder stringBuilder = new StringBuilder();

    System.out.println("QUESTION:: " + previousConversation.containsKey(patientProfile.getId()));
    System.out.println(previousConversation.get(patientProfile.getId()));

    if (previousConversation.containsKey(patientProfile.getId())) {
      stringBuilder.append(previousConversation.get(patientProfile.getId()));
      stringBuilder.append(" and then ");
    }

    if (!prompt.isBlank()) {
      stringBuilder.append("patient said: " + prompt);
      previousConversation.put(patientProfile.getId(), stringBuilder.toString());
    }

    // previousConversation.put(patientProfile.getId(), finalPrompt);
    System.out.println("PREVIOUSLY ON::");
    System.out.println(previousConversation.get(patientProfile.getId()));

    return restClient.post().body(body).retrieve().toEntity(AiAssistantResponseData.class).getBody().getCandidates()
        .get(0).getContent().getParts().get(0).getText();
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

  public List<String> searchSpecialization(String prompt) {

    restClient = RestClient.builder()
        .baseUrl(GEMINI_URL + "?key=" + GEMINI_KEY)
        .defaultHeader("Content-Type", "application/json")
        .build();

    String symptoms = prompt.isBlank() ? "" : "Here is information about the patient: " + prompt + ". ";

    String finalPrompt = symptoms +
        " List specialization recommended for the patient sorted by relevance using this JSON schema: " +
        "Specialization = {\\\"specialization_name\\\": str}. Return: list[Specialization]";
    String body = """
                    {
          "contents": [{
            "parts": [{
              "text": "%s"
            }]
          }],
          "generationConfig": { "response_mime_type": "application/json" }
        }
                    """.formatted(finalPrompt);

    String response = restClient.post().body(body).retrieve().toEntity(String.class).getBody();

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      AiAssistantResponseData responseData = objectMapper.readValue(response, AiAssistantResponseData.class);

      // Extracting JSON list of specializations
      String jsonContent = responseData.getCandidates()
          .get(0)
          .getContent()
          .getParts()
          .get(0)
          .getText();

      // Paring JSON content to extract list of specializations
      List<Map<String, String>> specializationObjects = objectMapper.readValue(jsonContent,
          new TypeReference<List<Map<String, String>>>() {
          });
      List<String> specializations = specializationObjects.stream()
          .map(specialization -> specialization.get("specialization_name"))
          .toList();
      return specializations;
  
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to parse the response from LLM");
    }
  }

}