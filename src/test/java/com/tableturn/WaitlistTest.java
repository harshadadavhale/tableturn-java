
package com.tableturn;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tableturn.party.JoinRequest;
import com.tableturn.party.PartyController;
import com.tableturn.party.PartyService;
import com.tableturn.party.PartyIds;
import com.tableturn.party.PartyRepository;
import com.tableturn.error.PartyTooBig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PartyController.class)
class WaitlistTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PartyService partyService;

    @Test
    void invalidRequestReturnsBadRequest() throws Exception {
        String body = """
                {
                  "name": "",
                  "covers": 0,
                  "phone": "invalid"
                }
                """;

        mockMvc.perform(post("/waitlist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void partyTooBigReturnsBadRequest() throws Exception {
        String body = """
                {
                  "name": "Kulkarni",
                  "covers": 7,
                  "phone": "+919876543210"
                }
                """;

        when(partyService.join(any(JoinRequest.class)))
                .thenThrow(new PartyTooBig(7, 6));

        mockMvc.perform(post("/waitlist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }
}
