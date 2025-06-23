package br.edu.ifsul.cstsi.tads_ricardo_bibli.api.cliente;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldGetAllClientes() throws Exception {
        mockMvc.perform(get("/api/v1/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void shouldGetClienteById() throws Exception {
        // First create a client to ensure we have one to retrieve
        ClientePostDto postDto = new ClientePostDto(
                "Test Client",
                25,
                "1234567890",
                "Test Address",
                "ALUNO"
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andReturn();

        ClienteDto createdClient = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                ClienteDto.class
        );

        // Now retrieve the client by ID
        mockMvc.perform(get("/api/v1/clientes/" + createdClient.codigo()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(createdClient.codigo()))
                .andExpect(jsonPath("$.nome").value("Test Client"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void shouldReturnNotFoundForNonExistentClienteId() throws Exception {
        mockMvc.perform(get("/api/v1/clientes/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldGetClientesByNome() throws Exception {
        // First create a client with a unique name
        String uniqueName = "UniqueTestName" + System.currentTimeMillis();
        ClientePostDto postDto = new ClientePostDto(
                uniqueName,
                25,
                "1234567890",
                "Test Address",
                "ALUNO"
        );

        mockMvc.perform(post("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated());

        // Now search for clients by that unique name
        mockMvc.perform(get("/api/v1/clientes/nome/" + uniqueName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].nome").value(uniqueName));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldGetClientesByTipo() throws Exception {
        // Create a client of type ALUNO
        ClientePostDto postDto = new ClientePostDto(
                "Test Aluno",
                25,
                "1234567890",
                "Test Address",
                "ALUNO"
        );

        mockMvc.perform(post("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated());

        // Now search for clients by type ALUNO
        mockMvc.perform(get("/api/v1/clientes/tipo/Aluno"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].tipo").value("Aluno"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldCreateCliente() throws Exception {
        ClientePostDto postDto = new ClientePostDto(
                "New Test Client",
                30,
                "9876543210",
                "New Test Address",
                "PAI_DE_ALUNO"
        );

        mockMvc.perform(post("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("New Test Client"))
                .andExpect(jsonPath("$.tipo").value("PaiDeAluno"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldRejectInvalidClienteCreation() throws Exception {
        // Missing required fields
        ClientePostDto invalidDto = new ClientePostDto(
                "",  // Empty name (invalid)
                0,   // Invalid age
                "abc", // Invalid phone
                "",  // Empty address
                "INVALID_TYPE" // Invalid type
        );

        mockMvc.perform(post("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldUpdateClientePartially() throws Exception {
        // First create a client
        ClientePostDto postDto = new ClientePostDto(
                "Update Test Client",
                25,
                "1234567890",
                "Test Address",
                "ALUNO"
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andReturn();

        ClienteDto createdClient = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                ClienteDto.class
        );

        // Now update just the name and age
        ClientePatchDto patchDto = new ClientePatchDto(
                "Updated Name",
                35,
                null,
                null
        );

        mockMvc.perform(patch("/api/v1/clientes/" + createdClient.codigo())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Updated Name"))
                .andExpect(jsonPath("$.idade").value(35))
                .andExpect(jsonPath("$.telefone").value("1234567890")) // Unchanged
                .andExpect(jsonPath("$.endereco").value("Test Address")); // Unchanged
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldDeleteCliente() throws Exception {
        // First create a client
        ClientePostDto postDto = new ClientePostDto(
                "Delete Test Client",
                25,
                "1234567890",
                "Test Address",
                "ALUNO"
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andReturn();

        ClienteDto createdClient = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                ClienteDto.class
        );

        // Now delete the client
        mockMvc.perform(delete("/api/v1/clientes/" + createdClient.codigo()))
                .andExpect(status().isNoContent());

        // Verify it's deleted by trying to get it
        mockMvc.perform(get("/api/v1/clientes/" + createdClient.codigo()))
                .andExpect(status().isNotFound());
    }
}