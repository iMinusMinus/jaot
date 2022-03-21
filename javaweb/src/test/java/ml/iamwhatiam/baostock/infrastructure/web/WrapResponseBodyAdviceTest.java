package ml.iamwhatiam.baostock.infrastructure.web;

import ml.iamwhatiam.baostock.ApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;


public class WrapResponseBodyAdviceTest extends ApplicationTest {

    @Resource
    private WebApplicationContext wac;

    // Spring AOT doesn't support @MockBean, @SpyBean
//    @MockBean
//    private BaoStockApi baoStockApi;

    @Test
    public void testNoLogin() throws Exception{
        String json = "{\"code\": 0,\"data\": {\"errorCode\": \"10001001\", \"errorMsg\": \"用户未登录\"}}";
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        mvc.perform(MockMvcRequestBuilders.get("/stock/k/sz.000001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(json, false));
    }

    @Test
    public void testThrow() throws Exception {
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        mvc.perform(MockMvcRequestBuilders.get("/stock/k/sz.000001").queryParam("endDate", "20220319"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(-1));
    }

}
