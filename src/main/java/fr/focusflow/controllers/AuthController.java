package fr.focusflow.controllers;

import fr.focusflow.dtos.UserRequestDTO;
import fr.focusflow.dtos.UserResponseDTO;
import fr.focusflow.entities.ERole;
import fr.focusflow.entities.Role;
import fr.focusflow.entities.User;
import fr.focusflow.exceptions.EmailAlreadyExistsException;
import fr.focusflow.exceptions.RoleNotFoundException;
import fr.focusflow.security.JwtTokenProvider;
import fr.focusflow.services.RoleService;
import fr.focusflow.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private static final String EMAIL_ALREADY_EXISTS_ERROR_MESSAGE = "Email already exists !";
    private static final String ROLE_NOT_FOUND_ERROR_MESSAGE = "Role not found !";
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public AuthController(JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager,
                          UserService userService, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Operation(summary = "Log in to get a JWT token", description = "Authenticates user credentials and returns a JWT token if successful.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully, JWT token returned"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials provided")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequestDTO userRequestDTO) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userRequestDTO.email(), userRequestDTO.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(userRequestDTO.email());

        return ResponseEntity.ok(new UserResponseDTO(token));
    }

    @Operation(summary = "Sign up a new user", description = "Creates a new user with the provided credentials and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully, JWT token returned"),
            @ApiResponse(responseCode = "400", description = "Email already exists"),
            @ApiResponse(responseCode = "404", description = "User role not found")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserRequestDTO userRequestDTO)
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

        String token = jwtTokenProvider.generateToken(newUser.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDTO(token));
    }
}
