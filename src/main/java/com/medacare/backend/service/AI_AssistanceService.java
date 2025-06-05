package com.medacare.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;

import com.medacare.backend.model.User;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

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
@RequiredArgsConstructor
public class AI_AssistanceService {
  private final AuthenticationService authenticationService;
  private final Map<Long, String> previousConversation;
  private final Map<Long, String> previousPhysicianConversation;

  private RestClient restClient;

  @Value("${gemini.key}")
  private String GEMINI_KEY;

  @Value("${gemini.url}")
  private String GEMINI_URL;

  public String adviseForSearch(@RequestBody String prompt, Patient currentPatient) {

    restClient = RestClient.builder()
        .baseUrl(GEMINI_URL + "?key=" + GEMINI_KEY)
        .defaultHeader("Content-Type", "application/json")
        .build();

    String symptoms = prompt.isBlank() ? "" : "The patient says: " + prompt + ". ";
    Patient patientProfile = currentPatient;
    String roleInstruction = """
        Assume the role of a doctor. The patient is looking for specialist.
        give them advice on what type of specialist they might need based on the following context.""";

    String reminder = """
         as a last note, Don't act out of character. Don't act out the character of a doctor.
         Just give them the advice they need. But be friendly. Don't jump straight to diagnosis
         if they don't request it engage in basic small talk appropriate in a physician-patient relationship.
         Respond directly to any question or input from the patient, whether it is a formal case,
        a brief query, or a casual prompt. Always remain concise, accurate, & helpful. If they say hello, say hello back.
        """;
    // String summarizedMedicalHistory = "The patient had reported that they have
    // medical history of "
    // + patientProfile.getMedicalHistory();
    String profile = symptoms + " The patient is " + patientProfile.getAge()
        + " years old and has the following medical history: "
        +
        patientProfile.getMedicalHistory() + ". "
        + " only use this information to give them the advice they need.";
    String finalPrompt = previousConversation.containsKey(patientProfile.getId())
        ? "If context is needed to deal with the patient, previous conversation with you was as follows: "
            + previousConversation.get(patientProfile.getId())
            + " and please the previous conversation in mind without explicitly acknowledging it if the patient is asking for a follow up question"+
            "if patient is just having a casual conversation, don't acknowledge the previous conversation. just go with the flow."
        : "";
    finalPrompt += roleInstruction + profile /* + summarizedMedicalHistory + prompt */
    // + " What kind of specialist do you think the patient should see?"
        + reminder;
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

  public String consult(@RequestBody String prompt) {

    System.out.println("Prompt: " + prompt);

    restClient = RestClient.builder()
        .baseUrl(GEMINI_URL + "?key=" + GEMINI_KEY)
        .defaultHeader("Content-Type", "application/json")
        .build();

    String topic = prompt.isBlank() ? "" : "The physician says: " + prompt + ". ";
    User physician = authenticationService.getCurrentUser();
    String roleInstruction = "You are a highly knowledgeable AI medical assistant designed to help" +
        " a physicians by answering questions, offering advice, and providing consultation on medical " +
        "topics about patients or themselves. Respond directly to any question or input from the physician, whether it is a formal case, "
        +
        "a brief query, or a casual prompt. Always remain concise, accurate, and clinically helpful..";

    String finalPrompt = previousPhysicianConversation.containsKey(physician.getId())
        ? "for context, previous conversation with you was as follows: "
            + previousPhysicianConversation.get(physician.getId())
            + " and please acknowledge the previous conversation if the physician is asking for a follow up question"
        : "";
    finalPrompt += roleInstruction + topic
        + " PS. Don't act out of character. Don't act out the character's actions." +
        "Don't talk about your speciality. You're just there as a consultant." +
        "don't talk about what you are doing. just state your fact based opinion." +
        "Just answer their question, give them the advice, or consultation they need." +
        " Notice to the subject of their enquiry. The physician might be looking for second opition. or they might be" +
        " looking advice about their own issue. notice the pronoun & the person they are talking about." +
        " the physician might be looking for medical advice for themselves or their patients. don't immediately assume it's about a patient";
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

    if (previousPhysicianConversation.containsKey(physician.getId())) {
      stringBuilder.append(previousPhysicianConversation.get(physician.getId()));
      stringBuilder.append(" and then ");
    }

    if (!prompt.isBlank()) {
      stringBuilder.append("patient said: " + prompt);
      previousPhysicianConversation.put(physician.getId(), stringBuilder.toString());
    }

    return restClient.post().body(body).retrieve().toEntity(AiAssistantResponseData.class).getBody().getCandidates()
        .get(0).getContent().getParts().get(0).getText();

  }

}