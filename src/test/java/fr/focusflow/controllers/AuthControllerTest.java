package fr.focusflow.controllers;

import fr.focusflow.entities.ERole;
import fr.focusflow.entities.Role;
import fr.focusflow.entities.User;
import fr.focusflow.security.CustomUserDetailsService;
import fr.focusflow.security.JwtTokenProvider;
import fr.focusflow.security.SecurityConfig;
import fr.focusflow.services.RoleService;
import fr.focusflow.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    private final static String EMAIL_ALREADY_EXISTS_ERROR_MESSAGE = "Email already exist !";
    private final static String ROLE_NOT_FOUND_ERROR_MESSAGE = "Role not found !";
    private final Logger logger = LoggerFactory.getLogger(AuthControllerTest.class);

    private String email;
    private String password;
    private String token;
    private String username;
    private String jsonLogin;
    private String wrongJsonLogin;
    private String jsonSignup;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @MockBean
    private UserService userService;
    @MockBean
    private RoleService roleService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        email = "toto@gmail.com";
        password = "123456";
        username = "toto";
        token = "fake-token";
        jsonLogin = "{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}";
        wrongJsonLogin = "{\"email\" : \"wrong@gmail.com\", \"password\" : \"wrongpassword\"}";
        jsonSignup = "{\"email\": \"" + email + "\", \"password\": \"" + password + "\",\"username\" : \"" + username + "\"}";

    }

    @Test
    public void shouldLoginAndReturnJwt() throws Exception {

        logger.info("Debut should_return_jwt_when_user_has_logged_test");

        when(jwtTokenProvider.generateToken(email)).thenReturn(token);

        mockLoginRequest(jsonLogin)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));

        logger.info("Fin should_return_jwt_when_user_has_logged_test");
    }

    @Test
    public void shouldFailLoginWithInvalidCredentials() throws Exception {

        logger.info("Debut shouldFailLoginWithInvalidCredentials");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        MvcResult mvcResult = mockLoginRequest(wrongJsonLogin)
                .andExpect(status().isUnauthorized()) // S'attendre à une erreur 401 Unauthorized
                .andReturn();

        String responsBody = mvcResult.getResponse().getContentAsString();
        logger.info("Login unauthorized, received response : " + responsBody);

        logger.info("Fin shouldFailLoginWithInvalidCredentials");
    }

    private void mockExistsEmail(boolean status) {
        // Simuler que l'email n'existe pas encore dans la base de données
        when(userService.existByEmail(email)).thenReturn(status);
    }

    private void mockDefaultRole() {
        Role role = Role.builder()
                .name(ERole.USER.name())
                .build();
        when(roleService.findByName(ERole.USER.name())).thenReturn(Optional.of(role));
    }

    private ResultActions mockSignupRequest(String jsonRequest) throws Exception {
        return mockMvc.perform(post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));
    }

    private ResultActions mockLoginRequest(String jsonRequest) throws Exception {
        return mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));
    }

    @Test
    public void shouldThrowErrorWhenEmailAlreadyExists() throws Exception {

        // Simuler qu'un utilisateur avec cet email existe déjà
        mockExistsEmail(true);

        mockSignupRequest(jsonSignup)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(EMAIL_ALREADY_EXISTS_ERROR_MESSAGE));
    }

    @Test
    public void shouldRegisterUserWithDefaultRole() throws Exception {

        // Verification email n'existe pas
        mockExistsEmail(false);

        // mock du role par defaut
        mockDefaultRole();

        // mock appel REST Signup
        mockSignupRequest(jsonSignup)
                .andExpect(status().isCreated());

    }

    @Test
    public void shouldRegisterUserThenReturnJWT() throws Exception {
        // Verification email n'existe pas

        mockExistsEmail(false);

        // mock du role par defaut
        mockDefaultRole();

        when(jwtTokenProvider.generateToken(email)).thenReturn(token);

        // mock appel REST Signup
        mockSignupRequest(jsonSignup)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test
    public void shouldRegisterUserWithHashedPassword() throws Exception {
        // Verification que l'email n'existe pas
        mockExistsEmail(false);

        // mock du role par defaut
        mockDefaultRole();

        when(jwtTokenProvider.generateToken(email)).thenReturn(token);

        // mock appel REST Signup
        mockSignupRequest(jsonSignup)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value(token));

        // Capture des arguments passés à userService.save
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(userCaptor.capture());

        // Récupérer l'utilisateur capturé
        User capturedUser = userCaptor.getValue();

        // Test password
        Assertions.assertEquals(passwordEncoder.encode(password), capturedUser.getPassword());
    }

    @Test
    public void shouldThrowErrorWhenInvalidEmail() throws Exception {

        String invalidRequestParam = "{\"email\": \"invalid-email\", \"password\": \"" + password + "\",\"username\" : \"" + username + "\"}";

        // mock appel REST Signup
        mockSignupRequest(invalidRequestParam)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.email").value("L'email doit être valide"));
    }

    @Test
    public void shouldThrowErrorWhenInvalidPassword() throws Exception {

        String invalidRequestParam = "{\"email\": \"" + email + "\", \"password\": \"\",\"username\" : \"" + username + "\"}";

        // mock appel REST Signup
        mockSignupRequest(invalidRequestParam)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.password").value("Le mot de passe ne peut pas être vide"));
    }

    @Test
    public void shouldThrowErrorWhenInvalidUsername() throws Exception {

        String invalidRequestParam = "{\"email\": \"" + email + "\", \"password\": \"" + password + "\",\"username\" : \"\"}";

        // mock appel REST Signup
        mockSignupRequest(invalidRequestParam)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.username").value("Le nom d'utilisateur ne peut pas être vide"));
    }

}