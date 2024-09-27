package fr.focusflow;

import fr.focusflow.Models.User;
import fr.focusflow.security.JwtTokenProvider;
import fr.focusflow.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class FocusflowApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @MockBean
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testUserCanLoginAndReceiveJWT() throws Exception {

        // mock récupération user en base
        String email = "toto@gmail.com";
        String password = passwordEncoder.encode("123456");
        User mockedUser = new User(email, password);
        when(userService.findByEmail(email)).thenReturn(Optional.of(mockedUser));

        // Requete JSON
        String userLoginJson = "{\"email\": \"" + email + "\", \"password\": \"123456\"}";


        //Assert
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userLoginJson))
                .andExpect(status().isOk())  // S'assurer que le statut est 200 OK
                .andExpect(jsonPath("$.token").isNotEmpty());  // Vérifier que le token est présent dans la réponse
    }

    @Test
    public void testUserCannotLoginWithInvalidCredentials() throws Exception {
        String invalidLoginJson = "{\"email\" : \"wrong@gmail.com\", \"password\" : \"wrongpassword\"}";

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidLoginJson))
                .andExpect(status().isUnauthorized());  // S'attendre à une erreur 401 Unauthorized
    }

    @Test
    public void testUserCanRegisternAndReceiveJWT() throws Exception {
        // Requête JSON
        String userRegisterJson = "{\"email\" : \"lulu@gmail.com\", \"password\" : \"lulu123\"}";

        when(userService.existByEmail("lulu@gmail.com")).thenReturn(false);

        // Effectuer la requête POST
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRegisterJson))
                .andExpect(status().isCreated());  // S'assurer que le statut est 201 Created

        // Capture des arguments passés à userService.save
        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);

        // Vérifier que la méthode save a été appelée avec l'email correct et capturer le mot de passe haché
        verify(userService).save(eq("lulu@gmail.com"), passwordCaptor.capture());

        // Comparer le mot de passe haché capturé
        String hashedPassword = passwordCaptor.getValue();
        Assertions.assertTrue(passwordEncoder.matches("lulu123", hashedPassword));
    }


    @Test
    public void testProtectedEndpointRequiresAuthenticationWithoutToken() throws Exception {

        mockMvc.perform(get("/api/protected"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testProtectedEndpointRequiresAuthenticationWithToken() throws Exception {

        String email = "toto@gmail.com";
        String password = passwordEncoder.encode("123456");
        User mockedUser = new User(email, password);
        when(userService.findByEmail(email)).thenReturn(Optional.of(mockedUser));

        // Générer un token JWT valide pour cet utilisateur
        String token = jwtTokenProvider.generateToken(email);

        mockMvc.perform(get("/api/protected")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

//	@Test
//	public void testEndpointWithExpiredToken() throws Exception {
//		UserDetails userDetails = userDetailsService.loadUserByUsername("toto@gmail.com");
//
//		String expiredToken = jwtTokenProvider.generateToken(userDetails.getUsername());
//
//		mockMvc.perform(get("/api/protected")
//				.header("Authorization", "Bearer "+expiredToken))
//				.andExpect(status().isUnauthorized());
//}


}
