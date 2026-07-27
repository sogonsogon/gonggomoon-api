package com.sogonsogon.gonggomoon;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import jakarta.servlet.DispatcherType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GonggomoonApiApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void errorDispatchDoesNotRequireAuthentication() throws Exception {
        int status = mockMvc.perform(
                get("/api/v1/error-dispatch-test")
                    .with(request -> {
                        request.setDispatcherType(DispatcherType.ERROR);
                        return request;
                    })
            )
            .andReturn()
            .getResponse()
            .getStatus();

        assertThat(status).isNotEqualTo(401);
    }
}
