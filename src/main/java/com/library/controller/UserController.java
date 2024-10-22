package com.library.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.library.constants.StringConstants;
import com.library.model.User;
import com.library.requestDTO.UserUpdateDTO;
import com.library.responseDTO.ApiResponse;
import com.library.responseDTO.Status;
import com.library.responseDTO.UserResponseDTO;
import com.library.service.Interfaces.InterfaceUserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api")
public class UserController {

	private InterfaceUserService userService;

	@Autowired
	public UserController(InterfaceUserService userService) {
		this.userService = userService;

	}

	@PostMapping("/users")
	public ResponseEntity<Object> addUser(@Valid @RequestBody User user) {
		UserResponseDTO responseDTO = userService.addUser(user);

		if (responseDTO != null) {
			ApiResponse<UserResponseDTO> response = ApiResponse.<UserResponseDTO>builder().status(Status.SUCCESS)
					.message("User added successfully").data(responseDTO).build();
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		}

		ApiResponse<UserResponseDTO> response = ApiResponse.<UserResponseDTO>builder().status(Status.FAILURE)
				.message("Failed to add user").build();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 500 Internal Server Error
	}

	@GetMapping("/users")
	public ResponseEntity<ApiResponse<List<User>>> getAllUsers(@RequestParam(required = false) String role,
			@RequestParam(defaultValue = "10", required = false) @Min(0) int limit,
			@RequestParam(defaultValue = "1", required = false) @Min(1) int pageNumber) {

		List<User> users = new ArrayList<>();

		if (role == null) {
			users = userService.getAllUsers(pageNumber, limit);
		} else {
			users = userService.getAllUsers(role, pageNumber, limit);
		}

		ApiResponse<List<User>> response = ApiResponse.<List<User>>builder().status(Status.SUCCESS)
				.message("Users retrieved successfully").data(users).count(users.size()).limit(limit).build();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/users/{userName}")
	public ResponseEntity<ApiResponse<User>> getUserByUserName(@PathVariable String userName) {

		User user = userService.getUserByUserName(userName);

		ApiResponse<User> response = ApiResponse.<User>builder().status(Status.SUCCESS)
				.message("User retrieved successfully").data(user).build();
		return ResponseEntity.ok(response);
	}

	@PutMapping("/users/{userId}")
	public ResponseEntity<ApiResponse<String>> updateUser(@PathVariable String userId,
			@Valid @RequestBody UserUpdateDTO userUpdateDTO) {

		boolean updateStatus = userService.updateUser(userId, userUpdateDTO);

		Status status;
		String message;
		HttpStatus httpStatus;
		if (updateStatus) {
			status = Status.SUCCESS;
			message = "User updated successfully";
			httpStatus = HttpStatus.OK;
		} else {
			status = Status.ERROR;
			message = "Failed to update user";
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		ApiResponse<String> response = ApiResponse.<String>builder().status(status).message(message).build();
		return ResponseEntity.status(httpStatus).body(response);

	}

	@DeleteMapping("/users/{userId}")
	public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String userId) {

		boolean userDeletionStatus = userService.deleteUser(userId);

		if (userDeletionStatus) {
			ApiResponse<String> response = ApiResponse.<String>builder().status(Status.SUCCESS)
					.message("User deleted successfully").build();
			return ResponseEntity.ok(response);
		}

		ApiResponse<String> response = ApiResponse.<String>builder().status(Status.FAILURE).message("User not found")
				.build();
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	public void payFine(User user) {
		boolean finePaidStatus = userService.payFine(user);
		if (finePaidStatus) {
			System.out.println(StringConstants.FINE_PAID_SUCCESSFULLY);
		}
	}

}
