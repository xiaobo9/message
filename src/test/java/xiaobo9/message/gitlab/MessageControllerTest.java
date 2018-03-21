package xiaobo9.message.gitlab;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Matchers.anyString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MessageController.class)
@AutoConfigureRestDocs
public class MessageControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private MessageController controller;
    
    @MockBean
    private MessageService service;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .apply(documentationConfiguration(this.restDocumentation))
                .build();
    }

    /**
     * {@link MessageController#register(String)}
     * Succeed: 输入完整正确的参数
     *
     * @throws Exception the exception
     */
    @Test
    public void register() throws Exception {
        String id = "id";
        Mockito.when(service.register(anyString())).thenReturn(id);

        this.mockMvc
                .perform(get("/message/register?url=\"test\""))
                .andExpect(status().isOk())
                .andDo(document("register",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("url").description("参数：gitlab 项目地址")
                        ),
                        responseFields(
                                fieldWithPath("token").description("生成的token"),
                                fieldWithPath("url").description("结果：gitlab 项目地址")
//                                fieldWithPath("userList[].id").description("用户id")
                        )))
        ;

    }

    /**
     * {@link MessageController#register(String)}
     * Succeed: 输入完整正确的参数
     *
     * @throws Exception the exception
     */
    @Test
    public void registerError() throws Exception {
        String id = "id";
        Mockito.when(service.register(anyString())).thenReturn(id);

        this.mockMvc
                .perform(get("/message/register?url=\"test\""))
                .andExpect(status().isOk())
                .andDo(document("register",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("url").description("gitlab 项目地址")
                        ),
                        responseFields(
                                fieldWithPath("token").description("生成的token"),
                                fieldWithPath("url").description("gitlab 项目地址")
//                                fieldWithPath("userList[].id").description("用户id")
                        )))
        ;

    }
}