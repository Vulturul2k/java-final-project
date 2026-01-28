package app.controller;

import app.service.OfferService;
import app.service.dto.CreateOfferFromContextDto;
import app.service.dto.OfferDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OfferController.class)
@AutoConfigureMockMvc(addFilters = false)
class OfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OfferService offerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getOffersReceivedByAuthenticatedUser_ShouldReturnOk() throws Exception {
        when(offerService.getOffersReceivedByName(anyString())).thenReturn(List.of());

        mockMvc.perform(get("/offers/received/me")
                .principal(() -> "user@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    void getOffersReceived_ShouldReturnOk() throws Exception {
        when(offerService.getOffersReceivedByName("admin@example.com")).thenReturn(List.of());

        mockMvc.perform(get("/offers/received")
                .param("username", "admin@example.com")
                .principal(() -> "admin@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    void respondToOffer_ShouldReturnOk() throws Exception {
        UUID offerId = UUID.randomUUID();
        OfferDto responseDto = new OfferDto();
        responseDto.setId(offerId);
        responseDto.setStatus("ACCEPTED");

        when(offerService.respondToOffer(any(UUID.class), anyString(), anyString()))
                .thenReturn(responseDto);

        mockMvc.perform(put("/offers/{id}/status", offerId)
                .param("status", "ACCEPTED")
                .principal(() -> "user@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    void createOfferFromAuthenticatedUser_ShouldReturnOk() throws Exception {
        CreateOfferFromContextDto dto = new CreateOfferFromContextDto();
        dto.setReceiverEmail("receiver@example.com");
        dto.setOfferedBookTitles(List.of("Book1"));
        dto.setRequestedBookTitles(List.of());
        dto.setOfferType("EXCHANGE");

        OfferDto responseDto = new OfferDto();
        responseDto.setId(UUID.randomUUID());

        when(offerService.createFromAuthenticatedUser(any(CreateOfferFromContextDto.class), anyString()))
                .thenReturn(responseDto);

        mockMvc.perform(post("/offers/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .principal(() -> "user@example.com"))
                .andExpect(status().isOk());
    }
}
