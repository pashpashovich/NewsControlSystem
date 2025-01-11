//package ru.clevertec.e2e;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import ru.clevertec.cache.Cache;
//import ru.clevertec.domain.News;
//
//import java.util.UUID;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
//@Testcontainers
//class NewsE2ETest {
//
//    @MockitoBean
//    private Cache<UUID, News> cache;
//
//    @Container
//    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.3")
//            .withDatabaseName("test")
//            .withUsername("user")
//            .withPassword("password");
//
//    @DynamicPropertySource
//    static void databaseProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgres::getJdbcUrl);
//        registry.add("spring.datasource.username", postgres::getUsername);
//        registry.add("spring.datasource.password", postgres::getPassword);
//    }
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Test
//    @WithMockUser(authorities = "ADMIN")
//    void testNewsLifecycleWithAuth() {
//        String createNewsRequest = """
//                    {
//                        "title": "Some Title",
//                        "text": "Some Text"
//                    }
//                """;
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer ");
//        HttpEntity<String> entity = new HttpEntity<>(createNewsRequest, headers);
//
//        ResponseEntity<String> createResponse = restTemplate.exchange(
//                "/news",
//                HttpMethod.POST,
//                entity,
//                String.class
//        );
//
//        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//    }
//
//}