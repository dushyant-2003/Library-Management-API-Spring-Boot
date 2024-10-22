package com.library.controller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.model.Role;
import com.library.model.User;
import com.library.responseDTO.Status;
import com.library.responseDTO.UserResponseDTO;
import com.library.service.Interfaces.InterfaceUserService;

@SpringBootTest
public class UserControllerTest {

    
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Mock
    private InterfaceUserService userService;

    @InjectMocks
    private UserController userController;
    
    private User user;
    private List<User> userList;

    @BeforeEach
    void setUp() {
    	MockitoAnnotations.openMocks(this);
    	mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    	user = new User("1", "John Doe", "johndoe", Role.ISSUER, null, null,
                "IT", "Manager", "1234567890", "john@example.com", 
                "123 Street", 3, null, "Password@123");
        userList = Arrays.asList(user);
    }
 
    @Test
    void testAddUser() throws Exception {
    	UserResponseDTO userResponse = new UserResponseDTO(user.getUserId(),user.getUserName(),user.getName(),user.getEmail());
        when(userService.addUser(any(User.class))).thenReturn(userResponse); // You can also return a DTO here

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(Status.SUCCESS.toString()))
                .andExpect(jsonPath("$.message").value("User added successfully"));

        verify(userService, times(1)).addUser(any(User.class));
    }
//
    @Test
    void testGetAllUsers() throws Exception {
        when(userService.getAllUsers(anyInt(), anyInt())).thenReturn(userList);

        mockMvc.perform(get("/api/users")
                .param("limit", "10")
                .param("pageNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.SUCCESS.toString()));

        verify(userService, times(1)).getAllUsers(anyInt(), anyInt());
    }
    

    @Test
    void testGetUserByUserName() throws Exception {
        when(userService.getUserByUserName("johndoe")).thenReturn(user);

        mockMvc.perform(get("/api/users/johndoe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.SUCCESS.toString()))
                .andExpect(jsonPath("$.data.name").value(user.getName()));

        verify(userService, times(1)).getUserByUserName("johndoe");
    }

    @Test
    void testDeleteUser() throws Exception {
        when(userService.deleteUser("1")).thenReturn(true);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.SUCCESS.toString()))
                .andExpect(jsonPath("$.message").value("User deleted successfully"));

        verify(userService, times(1)).deleteUser("1");
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        when(userService.deleteUser("2")).thenReturn(false);

        mockMvc.perform(delete("/api/users/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(Status.FAILURE.toString()))
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(userService, times(1)).deleteUser("2");
    }
}

