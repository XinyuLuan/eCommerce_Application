package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class CartControllerTest {

    private CartController cartController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final ItemRepository itemRepository = mock(ItemRepository.class);

    ModifyCartRequest cartRequest;
    User user;
    Item item;
    Cart cart;
    @Before
    public void setUp(){
        cartController = new CartController();

        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);

        user = getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        item = getItems().get(0);
        when(itemRepository.findById(item.getId())).thenReturn(java.util.Optional.ofNullable(item));
        cart = getCart();

        cartRequest = getCartRequest();
    }

    @Test
    public void addToCartHappyPathTest(){
        ResponseEntity<Cart> response = cartController.addTocart(cartRequest);
        assertNotNull(response);
        assertEquals(TestUtils.OK_RESPONSE, response.getStatusCodeValue());

        Cart actualCart = response.getBody();
        assertNotNull(actualCart);
        assertEquals(cart.getTotal(), actualCart.getTotal());
        assertEquals(cartRequest.getQuantity(), actualCart.getItems().size());
        assertEquals(Collections.nCopies(cartRequest.getQuantity(),getItems().get(0)).toString(),
                actualCart.getItems().toString());
    }

    @Test
    public void removeToCartHappyPathTest(){
        cartController.addTocart(cartRequest);
        ResponseEntity<Cart> removeFromCartResponse = cartController.removeFromcart(cartRequest);
        assertNotNull(removeFromCartResponse);
        assertEquals(TestUtils.OK_RESPONSE, removeFromCartResponse.getStatusCodeValue());

        Cart actualCart = removeFromCartResponse.getBody();
        assertNotNull(actualCart);
        assertEquals(new BigDecimal("0.00"), actualCart.getTotal());
        log.info(actualCart.getUser().getUsername());
        assertEquals(cart.getUser().getUsername(), actualCart.getUser().getUsername());
        assertEquals(0, actualCart.getItems().size());
    }


    private ModifyCartRequest getCartRequest(){
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername(getUser().getUsername());
        cartRequest.setItemId(getItems().get(0).getId());
        cartRequest.setQuantity(5);
        return cartRequest;
    }

    private Cart getCart(){
        Cart cart = new Cart();

        cart.setId(1L);
        cart.setUser(getUser());
        cart.setTotal(BigDecimal.valueOf(14.95));
        cart.setItems(Collections.nCopies(5, getItems().get(0)));
        return cart;
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

    private User getUser(){
        User user = new User();

        user.setId(0L);
        user.setUsername("test");
        user.setPassword("thisIsHashed");
        user.setCart(new Cart());

        return user;
    }
}
