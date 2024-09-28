package fr.focusflow;

import fr.focusflow.Models.ERole;
import fr.focusflow.Models.Role;
import fr.focusflow.Models.User;
import fr.focusflow.security.JwtTokenProvider;
import fr.focusflow.services.RoleService;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
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

    @MockBean
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    public void testUserCanLoginAndReceiveJWT() throws Exception {

        // mock récupération user en base
        String email = "toto@gmail.com";
        String password = passwordEncoder.encode("123456");
        User mockedUser = new User();
        mockedUser.setEmail(email);
        mockedUser.setPassword(password);
        mockedUser.getRoles().add(new Role(ERole.USER.name()));
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

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidLoginJson))
                .andExpect(status().isUnauthorized());  // S'attendre à une erreur 401 Unauthorized
    }

    @Test
    public void testUserCanRegisternAndReceiveJWT() throws Exception {
        // Requête JSON pour l'inscription
        String userRegisterJson = "{\"email\" : \"lulu@gmail.com\", \"password\" : \"lulu123\"}";

        // Simuler que l'email n'existe pas encore dans la base de données
        when(userService.existByEmail("lulu@gmail.com")).thenReturn(false);

        // Simuler le rôle utilisateur
        Role role = new Role();
        role.setName(ERole.USER.name());
        when(roleService.findByName(ERole.USER.name())).thenReturn(Optional.of(role));

        // Effectuer la requête POST
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRegisterJson))
                .andExpect(status().isCreated())  // S'assurer que le statut est 201 Created
                .andReturn();

        // Capture des arguments passés à userService.save
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(userCaptor.capture());

        // Récupérer l'utilisateur capturé
        User capturedUser = userCaptor.getValue();

        // Vérifier que l'email est correct
        Assertions.assertEquals("lulu@gmail.com", capturedUser.getEmail());

        // Vérifier que le mot de passe est haché
        Assertions.assertTrue(passwordEncoder.matches("lulu123", capturedUser.getPassword()));

        // Vérifier que le rôle est correctement attribué
        Assertions.assertTrue(capturedUser.getRoles().stream().anyMatch(r -> r.getName().equals(ERole.USER.name())));
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

        User mockedUser = new User();
        mockedUser.setEmail(email);
        mockedUser.setPassword(password);
        mockedUser.getRoles().add(new Role(ERole.USER.name()));

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
