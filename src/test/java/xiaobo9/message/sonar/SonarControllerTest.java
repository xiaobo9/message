package xiaobo9.message.sonar;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import xiaobo9.message.sonar.entity.UserSonarResult;
import xiaobo9.message.sonar.repository.UserSonarResultRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SonarController.class)
@ExtendWith({RestDocumentationExtension.class})
@AutoConfigureRestDocs
class SonarControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private SonarController controller;

    @MockBean
    private UserSonarResultRepository userSonarResultRepository;

    @MockBean
    private SonarQubeService sonarQubeService;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .apply(documentationConfiguration(restDocumentation))
                .build();
        System.out.println("setUp");
    }

    @Test
    public void test_checkResult() throws Exception {
        List<UserSonarResult> resultList = Lists.newArrayList();
        Mockito.when(userSonarResultRepository.findByEmailAndCheckId(anyString(), anyString())).thenReturn(resultList);

        this.mockMvc.perform(get("/sonar/checkResult?author=abc&checkId=abc"))
                .andExpect(status().isOk());
    }
}