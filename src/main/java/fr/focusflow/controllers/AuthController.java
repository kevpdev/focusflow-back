package fr.focusflow.controllers;

import fr.focusflow.Models.ERole;
import fr.focusflow.Models.Role;
import fr.focusflow.Models.User;
import fr.focusflow.dtos.UserRequestDTO;
import fr.focusflow.dtos.UserResponseDTO;
import fr.focusflow.exceptions.EmailAlreadyExistsException;
import fr.focusflow.exceptions.RoleNotFoundException;
import fr.focusflow.security.JwtTokenProvider;
import fr.focusflow.services.RoleService;
import fr.focusflow.services.UserService;
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
                .authenticate(new UsernamePasswordAuthenticationToken(userRequestDTO.getEmail(), userRequestDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Token
        String token = jwtTokenProvider.generateToken(userRequestDTO.getEmail());

        return ResponseEntity.ok(new UserResponseDTO(token));

    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserRequestDTO userRequestDTO) throws EmailAlreadyExistsException, RoleNotFoundException {

        if (userService.existByEmail(userRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exist !");
        }

        Role role = roleService.findByName(ERole.USER.name())
                .orElseThrow(() -> new RoleNotFoundException("Role not found !"));

        User newUser = new User();
        newUser.setEmail(userRequestDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        newUser.getRoles().add(role);

        userService.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);


    }

    @GetMapping("/protected")
    public ResponseEntity<String> testAccessProtectedRessources() {
        return ResponseEntity.ok("Succeful access !");
    }
}
