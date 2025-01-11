package ru.clevertec.integration.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 8082)
class NewsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(authorities = "JOURNALIST")
    void shouldCreateAndGetNews() throws Exception {
        String requestBody = """
                {
                  "title": "Integration Test Title",
                  "text": "Integration Test Text"
                }
                """;

        String createdNewsResponse = mockMvc.perform(post("/news")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Integration Test Title"))
                .andExpect(jsonPath("$.text").value("Integration Test Text"))
                .andReturn().getResponse().getContentAsString();

        String newsId = extractIdFromResponse(createdNewsResponse);

        mockMvc.perform(get("/news/{newsId}", newsId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Integration Test Title"))
                .andExpect(jsonPath("$.data.text").value("Integration Test Text"))
                .andExpect(jsonPath("$.status").value(true));
    }

    private String extractIdFromResponse(String jsonResponse) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonResponse);
        return node.get("id").asText();
    }

    @Test
    @WithMockUser(authorities = "JOURNALIST")
    void shouldDeleteNews() throws Exception {
        String requestBody = """
                {
                  "title": "To Delete",
                  "text": "This news will be deleted"
                }
                """;

        String createdNewsResponse = mockMvc.perform(post("/news")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String newsId = extractIdFromResponse(createdNewsResponse);

        mockMvc.perform(delete("/news/{newsId}", newsId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/news/{newsId}", newsId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "JOURNALIST")
    void shouldSearchNews() throws Exception {
        String firstNewsRequest = """
                {
                  "title": "Breaking News",
                  "text": "Some breaking news content"
                }
                """;

        String secondNewsRequest = """
                {
                  "title": "Sports Update",
                  "text": "Latest sports news"
                }
                """;

        mockMvc.perform(post("/news")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstNewsRequest))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/news")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondNewsRequest))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/news/search")
                        .param("query", "Breaking")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].title").value("Breaking News"))
                .andExpect(jsonPath("$.message").value("Результаты поиска новостей"));
    }

    @Test
    @WithMockUser(authorities = "JOURNALIST")
    void shouldGetNewsWithComments() throws Exception {
        String newsRequest = """
                {
                  "title": "News with Comments",
                  "text": "This news will have comments"
                }
                """;
        String createdNewsResponse = mockMvc.perform(post("/news")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newsRequest))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String newsId = extractIdFromResponse(createdNewsResponse);

        stubFor(WireMock.get(WireMock.urlPathMatching("/comments/[a-f0-9\\-]{36}"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "data": {
                                    "content": [
                                      {
                                        "id": "226bdc42-2b0a-4821-8dfa-84b1dc518ddf",
                                        "text": "This is a test comment",
                                        "username": "Test User",
                                        "createdAt": "2024-12-25T11:00:00"
                                      }
                                    ],
                                    "page": {
                                      "size": 20,
                                      "number": 0,
                                      "totalElements": 3,
                                      "totalPages": 1
                                    }
                                  },
                                  "status": true,
                                  "message": "Получены комментарии к новости"
                                }
                                """)
                        .withStatus(200)));

        mockMvc.perform(get("/news/{newsId}/comments", newsId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.comments").isArray())
                .andExpect(jsonPath("$.data.comments[0].text").value("This is a test comment"))
                .andExpect(jsonPath("$.data.comments[0].username").value("Test User"))
                .andExpect(jsonPath("$.message").value("Новость с комментариями получена"));
    }

    @Test
    @WithMockUser(authorities = "JOURNALIST")
    void shouldReturnNotFoundForNonExistentNewsId() throws Exception {
        String nonExistentNewsId = "123e4567-e89b-12d3-a456-426614174000";

        mockMvc.perform(get("/news/{newsId}", nonExistentNewsId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format("Новость с ID %s не найдена", nonExistentNewsId)));
    }
}
