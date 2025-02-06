package fr.focusflow.controllers;

import fr.focusflow.TestDataFactory;
import fr.focusflow.entities.User;
import fr.focusflow.security.CustomUserDetailsService;
import fr.focusflow.security.JwtTokenProvider;
import fr.focusflow.security.SecurityConfig;
import fr.focusflow.services.AuthenticatedUserService;
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
import org.springframework.mock.web.MockCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@WebMvcTest(AuthController.class)
@TestPropertySource(properties = {
        "jwt.token.expiration=900",
        "jwt.refresh.token.expiration=604800"
})
class AuthControllerTest {

    private static final String EMAIL_ALREADY_EXISTS_ERROR_MESSAGE = "Email already exists !";
    private final Logger logger = LoggerFactory.getLogger(AuthControllerTest.class);

    private String email;
    private String password;
    private String token;
    private String refreshToken;
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
    private AuthenticatedUserService authenticatedUserService;
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
        refreshToken = "fake-reresh-token";
        jsonLogin = "{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}";
        wrongJsonLogin = "{\"email\" : \"wrong@gmail.com\", \"password\" : \"wrongpassword\"}";
        jsonSignup = "{\"email\": \"" + email + "\", \"password\": \"" + password + "\",\"username\" : \"" + username + "\"}";

    }

    @Test
    void shouldLoginAndReturnJwtCookie() throws Exception {

        logger.info("Debut shouldLoginAndReturnJwtCookie");

        when(jwtTokenProvider.generateToken(email)).thenReturn(token);
        when(jwtTokenProvider.generateRefreshToken(email)).thenReturn(refreshToken);

        mockLoginRequest(jsonLogin)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().exists("refreshToken"));


        verify(jwtTokenProvider).generateToken(email);
        verify(jwtTokenProvider).generateRefreshToken(email);


        logger.info("Fin shouldLoginAndReturnJwtCookie");
    }

    @Test
    void shouldFailLoginWithInvalidCredentials() throws Exception {

        logger.info("Debut shouldFailLoginWithInvalidCredentials");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        MvcResult mvcResult = mockLoginRequest(wrongJsonLogin)
                .andExpect(status().isUnauthorized()) // S'attendre à une erreur 401 Unauthorized
                .andReturn();

        String responsBody = mvcResult.getResponse().getContentAsString();
        logger.info("Login unauthorized, received response : {}", responsBody);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        logger.info("Fin shouldFailLoginWithInvalidCredentials");
    }

    private void mockExistsEmail(boolean status) {
        // Simuler que l'email n'existe pas encore dans la base de données
        when(userService.existByEmail(email)).thenReturn(status);
    }


    private ResultActions mockSignupRequest(String jsonRequest) throws Exception {
        return mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));
    }

    private ResultActions mockLoginRequest(String jsonRequest) throws Exception {
        return mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));
    }

    @Test
    void shouldThrowErrorWhenEmailAlreadyExists() throws Exception {

        // Simuler qu'un utilisateur avec cet email existe déjà
        mockExistsEmail(true);

        mockSignupRequest(jsonSignup)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(EMAIL_ALREADY_EXISTS_ERROR_MESSAGE));

        verify(userService).existByEmail(email);
    }

    @Test
    void shouldRegisterUserWithDefaultRole() throws Exception {

        // Verification email n'existe pas
        mockExistsEmail(false);

        // mock appel REST Signup
        mockSignupRequest(jsonSignup)
                .andExpect(status().isCreated());
    }

    @Test
    void shouldRegisterUserThenReturnJWT() throws Exception {
        // Verification email n'existe pas

        mockExistsEmail(false);


        when(jwtTokenProvider.generateToken(email)).thenReturn(token);
        when(jwtTokenProvider.generateRefreshToken(email)).thenReturn(refreshToken);

        // mock appel REST Signup
        mockSignupRequest(jsonSignup)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().exists("refreshToken"));

        verify(userService).existByEmail(email);
        verify(jwtTokenProvider).generateToken(email);
        verify(jwtTokenProvider).generateRefreshToken(email);
    }

    @Test
    void shouldRegisterUserWithHashedPassword() throws Exception {
        // Verification que l'email n'existe pas
        mockExistsEmail(false);

        when(jwtTokenProvider.generateToken(email)).thenReturn(token);
        when(jwtTokenProvider.generateRefreshToken(email)).thenReturn(refreshToken);

        // mock appel REST Signup
        mockSignupRequest(jsonSignup)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().exists("refreshToken"));


        // Capture des arguments passés à userService.save
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(userCaptor.capture());

        // Récupérer l'utilisateur capturé
        User capturedUser = userCaptor.getValue();

        // Test password
        Assertions.assertEquals(passwordEncoder.encode(password), capturedUser.getPassword());

        verify(userService).existByEmail(email);
        verify(jwtTokenProvider).generateToken(email);
        verify(jwtTokenProvider).generateRefreshToken(email);

    }

    @Test
    void shouldThrowErrorWhenInvalidEmail() throws Exception {

        String invalidRequestParam = "{\"email\": \"invalid-email\", \"password\": \"" + password + "\",\"username\" : \"" + username + "\"}";

        // mock appel REST Signup
        mockSignupRequest(invalidRequestParam)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.email").value("L'email doit être valide"));
    }

    @Test
    void shouldThrowErrorWhenInvalidPassword() throws Exception {

        String invalidRequestParam = "{\"email\": \"" + email + "\", \"password\": \"\",\"username\" : \"" + username + "\"}";

        // mock appel REST Signup
        mockSignupRequest(invalidRequestParam)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.password").value("Le mot de passe ne peut pas être vide"));
    }

    @Test
    void shouldThrowErrorWhenInvalidUsername() throws Exception {

        String invalidRequestParam = "{\"email\": \"" + email + "\", \"password\": \"" + password + "\",\"username\" : \"\"}";

        // mock appel REST Signup
        mockSignupRequest(invalidRequestParam)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.username").value("Le nom d'utilisateur ne peut pas être vide"));
    }

    @Test
    void testIsAuthenticatedWithCookie() throws Exception {

        // initialisatio context spring pour le bean authentication
        TestDataFactory.setUpSecurityContext();

        MockCookie accessTokenCookie = new MockCookie("accessToken", "fake_secret_jwt_GFHjF7GkJrZeR7bLxXBtZZtS");

        mockMvc.perform(get("/api/v1/auth/isAuthenticated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenCookie))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testIsAuthenticatedWithBearer() throws Exception {

        // initialisatio context spring pour le bean authentication
        TestDataFactory.setUpSecurityContext();

        mockMvc.perform(get("/api/v1/auth/isAuthenticated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer fake_secret_jwt_GFHjF7GkJrZeR7bLxXBtZZtS"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

}