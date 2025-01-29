package fr.focusflow.controllers;

import fr.focusflow.dtos.UserRequestDTO;
import fr.focusflow.dtos.UserResponseDTO;
import fr.focusflow.entities.ERole;
import fr.focusflow.entities.Role;
import fr.focusflow.entities.User;
import fr.focusflow.exceptions.EmailAlreadyExistsException;
import fr.focusflow.exceptions.RoleNotFoundException;
import fr.focusflow.security.JwtTokenProvider;
import fr.focusflow.services.AuthenticatedUserService;
import fr.focusflow.services.RoleService;
import fr.focusflow.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final String EMAIL_ALREADY_EXISTS_ERROR_MESSAGE = "Email already exists !";
    private static final String ROLE_NOT_FOUND_ERROR_MESSAGE = "Role not found !";
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final AuthenticatedUserService authenticatedUserService;
    private final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    @Value("${jwt.token.expiration}")
    private String jwtTokenExpiration;
    @Value("${jwt.refresh.token.expiration}")
    private String jwtRefreshTokenExpiration;
    @Value("${server.servlet.session.cookie.secure:true}")
    private boolean isSecure;
    @Value("${server.servlet.session.cookie.same-site:None}")
    private String sameSite;

    public AuthController(JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager,
                          UserService userService, PasswordEncoder passwordEncoder, RoleService roleService,
                          AuthenticatedUserService authenticatedUserService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Operation(summary = "Log in to get a JWT token", description = "Authenticates user credentials and returns a JWT token if successful.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully, JWT token returned"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials provided")
    })
    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody UserRequestDTO userRequestDTO, HttpServletRequest request) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userRequestDTO.email(), userRequestDTO.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.generateToken(userRequestDTO.email());
        String refreshToken = jwtTokenProvider.generateRefreshToken(userRequestDTO.email());

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        ResponseCookie csrfCookie = ResponseCookie.from("XSRF-TOKEN", csrfToken.getToken())
                .path("/")
                .httpOnly(false)
                .secure(false)
                .sameSite(sameSite)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, getAccessToken(accessToken).toString());
        headers.add(HttpHeaders.SET_COOKIE, getRefreshToken(refreshToken).toString());
        headers.add(HttpHeaders.SET_COOKIE, csrfCookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(new UserResponseDTO("Login successful !", userRequestDTO.email(), authenticatedUserService.getAuthenticatedUserRoles()));
    }

    @Operation(summary = "Generate a new access token", description = "Generate a new access token if refresh token is still valid.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Refresh token validation successfully, new JWT token returned"),
            @ApiResponse(responseCode = "401", description = "Refresh token hax expirated")
    })
    @PostMapping("/refresh")
    public ResponseEntity<UserResponseDTO> refreshToken(HttpServletRequest request) {

        String refreshToken = jwtTokenProvider.extractRefreshTokenFromCookie(request);

        if (jwtTokenProvider.validateToken(refreshToken)) {
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);
            String newAccessToken = jwtTokenProvider.generateToken(email);

            ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
                    .httpOnly(true)
                    .secure(isSecure)
                    .path("/")
                    .maxAge(Long.parseLong(jwtTokenExpiration))
                    .sameSite(sameSite)
                    .build();

            List<String> roles = authenticatedUserService.getAuthenticatedUserRoles();

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, accessCookie.toString()).body(new UserResponseDTO("refresh token successful !", email, roles));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Verify if the user is authenticated", description = "Verify if the user is authenticated and if the access token is valid.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is authenticated and access token is valid")})
    @GetMapping("/isAuthenticated")
    public ResponseEntity<Boolean> isAuthenticated() {
        return ResponseEntity.ok().body(true);
    }


    @Operation(summary = "Sign up a new user", description = "Creates a new user with the provided credentials and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully, JWT token returned"),
            @ApiResponse(responseCode = "400", description = "Email already exists"),
            @ApiResponse(responseCode = "404", description = "User role not found")
    })
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> signup(@Valid @RequestBody UserRequestDTO userRequestDTO, HttpServletRequest request)
            throws EmailAlreadyExistsException, RoleNotFoundException {

        if (userService.existByEmail(userRequestDTO.email())) {
            throw new EmailAlreadyExistsException(EMAIL_ALREADY_EXISTS_ERROR_MESSAGE);
        }

        Role role = roleService.findByName(ERole.USER.name())
                .orElseThrow(() -> new RoleNotFoundException(ROLE_NOT_FOUND_ERROR_MESSAGE));

        User newUser = new User();
        newUser.setEmail(userRequestDTO.email());
        newUser.setPassword(passwordEncoder.encode(userRequestDTO.password()));
        newUser.setUsername(userRequestDTO.username());
        newUser.getRoles().add(role);

        userService.save(newUser);

        String accesToken = jwtTokenProvider.generateToken(userRequestDTO.email());
        String refreshToken = jwtTokenProvider.generateRefreshToken(userRequestDTO.email());

        ResponseCookie accessCookie = getAccessToken(accesToken);
        ResponseCookie refreshCookie = getRefreshToken(refreshToken);
        ResponseCookie csrfCookie = getCsrfCookie(request);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, csrfCookie.toString());

        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(headers)
                .body(new UserResponseDTO("Sign up successful !", userRequestDTO.email(), authenticatedUserService.getAuthenticatedUserRoles()));
    }

    private ResponseCookie getCsrfCookie(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return ResponseCookie.from("XSRF-TOKEN", csrfToken.getToken())
                .path("/")
                .httpOnly(false)
                .secure(isSecure)
                .sameSite(sameSite)
                .build();
    }


    /**
     * Get an JWT acces cookie
     *
     * @param accesToken
     * @return a ResponseCookie object
     */
    private ResponseCookie getAccessToken(String accesToken) {
        return ResponseCookie.from("accessToken", accesToken).
                httpOnly(true)
                .secure(isSecure) // to force https connection
                .path("/")
                .maxAge(Long.parseLong(jwtTokenExpiration))
                .sameSite(sameSite)
                .build();
    }


    /**
     * Get an JWT refresh  cookie
     *
     * @param refreshToken
     * @return a ResponseCookie object
     */
    private ResponseCookie getRefreshToken(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken).
                httpOnly(true)
                .secure(isSecure) // to force https connection
                .path("/")
                .maxAge(Long.parseLong(jwtRefreshTokenExpiration))
                .sameSite(sameSite)
                .build();
    }

}
