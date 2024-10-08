package fr.focusflow.controllers;

import fr.focusflow.dtos.UserRequestDTO;
import fr.focusflow.dtos.UserResponseDTO;
import fr.focusflow.exceptions.EmailAlreadyExistsException;
import fr.focusflow.exceptions.RoleNotFoundException;
import fr.focusflow.models.ERole;
import fr.focusflow.models.Role;
import fr.focusflow.models.User;
import fr.focusflow.security.JwtTokenProvider;
import fr.focusflow.services.RoleService;
import fr.focusflow.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final static String EMAIL_ALREADY_EXISTS_ERROR_MESSAGE = "Email already exist !";
    private final static String ROLE_NOT_FOUND_ERROR_MESSAGE = "Role not found !";
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


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequestDTO userRequestDTO) {
        // Test connexion
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userRequestDTO.email(), userRequestDTO.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Token
        String token = jwtTokenProvider.generateToken(userRequestDTO.email());

        return ResponseEntity.ok(new UserResponseDTO(token));

    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserRequestDTO userRequestDTO) throws EmailAlreadyExistsException, RoleNotFoundException {

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

    @GetMapping("/protected")
    public ResponseEntity<String> testAccessProtectedRessources() {
        return ResponseEntity.ok("Succeful access !");
    }
}
