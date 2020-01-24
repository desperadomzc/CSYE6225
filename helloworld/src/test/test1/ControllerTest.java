package test1;


import com.me.controller.UserController;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:hibernate.cfg.xml","file:/home/mzc/IdeaProjects/helloworld/web/WEB-INF/applicationContext.xml","file:/home/mzc/IdeaProjects/helloworld/web/WEB-INF/dispatcher-servlet.xml"})
public class ControllerTest {

    private MockMvc mock;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Before
    public void before(){
        mock = MockMvcBuilders.standaloneSetup(new UserController()).build();
    }

    @Test
    public void testCreateUser() throws Exception {
        String url = "/v1/user";
        JSONObject j = new JSONObject();

        //invalid
        j.put("password","123");
        j.put("last_name","ooo");
        j.put("first_name","ooo");
        j.put("email_address","123sun.com");
        //other fields should be ignored
        j.put("account_created","anything");

        MvcResult mvcRes = mock.perform(MockMvcRequestBuilders.post(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(j.toString().getBytes())
                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

}
