package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.h2.command.ddl.CreateUser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    private CreateUserRequest userRequest;
    private User user;

    @Before
    public void setUp(){
        userController = new UserController();
        // inject mocked userRepository to userController
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);

        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        userRequest = setUserRequest();
        user = setUser();
        when(userRepository.findById(0L)).thenReturn(java.util.Optional.ofNullable(user));
        when(userRepository.findByUsername("test")).thenReturn(user);
    }

    @Test
    public void create_user_happy_path() throws Exception{
        final ResponseEntity<User> response = userController.createUser(userRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void create_user_bad_password() throws Exception{
        userRequest.setConfirmPassword("12345678");

        final ResponseEntity<User> response = userController.createUser(userRequest);
        assertNotNull(response);
        assertEquals(TestUtils.BAD_REQUEST, response.getStatusCodeValue());

        User actualUser = response.getBody();
        assertNull(actualUser);
    }

    @Test
    public void findByIdTest() throws Exception{
        final ResponseEntity<User> response = userController.createUser(userRequest);
        assertNotNull(response);
        assertEquals(TestUtils.OK_RESPONSE, response.getStatusCodeValue());

        ResponseEntity<User> findByIdResponse = userController.findById(0L);
        assertNotNull(findByIdResponse);

        User actualUser = findByIdResponse.getBody();
        assertNotNull(actualUser);
        assertEquals("test", actualUser.getUsername());
        assertEquals("thisIsHashed", actualUser.getPassword());
    }

    @Test
    public void findByUserNameTest(){
        final ResponseEntity<User> response = userController.findByUserName("test");
        assertNotNull(response);
        assertEquals(TestUtils.OK_RESPONSE, response.getStatusCodeValue());

        User actualUser = response.getBody();
        assertNotNull(actualUser);
        assertEquals(0L, actualUser.getId());
        assertEquals("thisIsHashed", actualUser.getPassword());
    }

    private CreateUserRequest setUserRequest(){
        CreateUserRequest r = new CreateUserRequest();

        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        return r;
    }

    private User setUser(){
        User user = new User();

        user.setId(0L);
        user.setUsername("test");
        user.setPassword("thisIsHashed");
        user.setCart(new Cart());

        return user;
    }
}
