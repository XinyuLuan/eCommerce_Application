package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;

    private final UserRepository userRepository = mock(UserRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);

    User user;
    UserOrder order;
    @Before
    public void setUp(){
        orderController = new OrderController();

        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);

        user = getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        order = getOrder();
    }

    @Test
    public void submitHappyPathTest(){
        ResponseEntity<UserOrder> submitResponse = orderController.submit(user.getUsername());
        assertNotNull(submitResponse);
        assertEquals(TestUtils.OK_RESPONSE, submitResponse.getStatusCodeValue());

        UserOrder actualOrder = submitResponse.getBody();
        assertNotNull(actualOrder);
        assertEquals(order.getUser().getUsername(), actualOrder.getUser().getUsername());
        assertEquals(order.getItems().size(), actualOrder.getItems().size());
        assertArrayEquals(order.getItems().toArray(), actualOrder.getItems().toArray());
    }

    private User getUser(){
        User user = new User();

        user.setId(0L);
        user.setUsername("test");
        user.setPassword("thisIsHashed");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(Collections.nCopies(5, getItems().get(0)));
        user.setCart(cart);

        return user;
    }

    private List<Item> getItems(){
        List<Item> items = new ArrayList<>();

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Round Widget");
        item1.setPrice(BigDecimal.valueOf(2.99));
        item1.setDescription("A widget that is round");
        items.add(item1);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Square Widget");
        item2.setPrice(BigDecimal.valueOf(1.99));
        item2.setDescription("A widget that is square");
        items.add(item2);

        return items;
    }

    private UserOrder getOrder(){
        UserOrder order = new UserOrder();

        order.setId(1L);
        order.setUser(getUser());
        order.setItems(Collections.nCopies(5, getItems().get(0)));

        return order;
    }
}
