package com.library.controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.constants.ApiMessages;
import com.library.model.User;
import com.library.requestDTO.AuthenticationRequestDTO;
import com.library.responseDTO.ApiResponse;
import com.library.responseDTO.AuthenticationResponseDTO;
import com.library.responseDTO.Status;
import com.library.security.JwtUtil;
import com.library.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UserDetailsService userDetailsService;

	private final AuthenticationManager authenticationManager;

	private final PasswordEncoder passwordEncoder;

	private final JwtUtil jwtUtil;

	@Autowired
	private UserService userService;
	
	


//	@PostMapping("/login")
//	public ResponseEntity<ApiResponse<AuthenticationResponseDTO>> login(@RequestBody AuthenticationRequestDTO user) {
//		try {
//			// Authenticate the user
//			authenticationManager
//					.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
//
//			// Load user details and generate JWT token
//			UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
//			String jwtToken = jwtUtil.generateToken(userDetails.getUsername());
//
//			// Create an instance of AuthenticationResponseDTO
//			AuthenticationResponseDTO responseData = new AuthenticationResponseDTO();
//			responseData.setJwtToken(jwtToken);
//			responseData.setUsername(user.getUsername());
//
//			// Build the ApiResponse
//
//			// Build the ApiResponse with the List
//			ApiResponse<AuthenticationResponseDTO> response = ApiResponse.<AuthenticationResponseDTO>builder()
//					.status(Status.SUCCESS).message(ApiMessages.LOGGED_IN_SUCCESSFULLY).data(responseData).build();
//
//			return ResponseEntity.ok(response);
//		} catch (Exception e) {
//
//			ApiResponse<AuthenticationResponseDTO> errorResponse = ApiResponse.<AuthenticationResponseDTO>builder()
//					.status(Status.ERROR).message(ApiMessages.INVALID_CREDENTIALS_MESSAGE).build();
//
//			return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
//
//		}
//
//	}
	
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthenticationResponseDTO>> login(@RequestHeader("Authorization") String authorizationHeader) {
	    try {
	        // Check if the Authorization header is present and starts with "Basic"
	        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
	            // Decode Base64 credentials
	            String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
	            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
	            String decodedCredentials = new String(decodedBytes, StandardCharsets.UTF_8);

	            // Credentials are in the format "username:password"
	            String[] credentials = decodedCredentials.split(":", 2);
	            String username = credentials[0];
	            String password = credentials[1];

	            // Authenticate the user
	            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

	            // Load user details and generate JWT token
	            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
	            String jwtToken = jwtUtil.generateToken(userDetails.getUsername());

	            // Create an instance of AuthenticationResponseDTO
	            AuthenticationResponseDTO responseData = new AuthenticationResponseDTO();
	            responseData.setJwtToken(jwtToken);
	            responseData.setUsername(username);

	            // Build the ApiResponse
	            ApiResponse<AuthenticationResponseDTO> response = ApiResponse.<AuthenticationResponseDTO>builder()
	                    .status(Status.SUCCESS).message(ApiMessages.LOGGED_IN_SUCCESSFULLY).data(responseData).build();

	            return ResponseEntity.ok(response);
	        } else {
	            // If Authorization header is missing or doesn't have Basic Auth
	            ApiResponse<AuthenticationResponseDTO> errorResponse = ApiResponse.<AuthenticationResponseDTO>builder()
	                    .status(Status.ERROR).message("Authorization header is missing or not using Basic authentication").build();

	            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	        }
	    } catch (Exception e) {
	        // Handle authentication failure
	        ApiResponse<AuthenticationResponseDTO> errorResponse = ApiResponse.<AuthenticationResponseDTO>builder()
	                .status(Status.ERROR).message(ApiMessages.INVALID_CREDENTIALS_MESSAGE).build();

	        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	    }
	}

	
	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<String>> logoutUser(@RequestHeader("Authorization") String authorizationHeader) {
		// Check if the header is present and well-formed
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			
			ApiResponse<String> response = ApiResponse.<String>builder().status(Status.ERROR).message("Invalid token").build();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		// Extract the JWT token (removing "Bearer " prefix)
		String jwtToken = authorizationHeader.substring(7);
		// Add the token to the blacklist
		jwtUtil.blacklistToken(jwtToken);
		// Respond with success message
		ApiResponse<String> response = ApiResponse.<String>builder().status(Status.SUCCESS).message("Logged out successfully").build();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
