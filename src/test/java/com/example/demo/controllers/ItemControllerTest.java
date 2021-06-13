package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;
    private final ItemRepository itemRepository = mock(ItemRepository.class);

    List<Item> items;
    @Before
    public void setUp(){
        itemController = new ItemController();
        // set the mocked item repository to item controller
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);

        items = getItems();
        when(itemRepository.findAll()).thenReturn(items);
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(items.get(0)));
        List<Item> testItems = new ArrayList<>();
        testItems.add(items.get(1));
        when(itemRepository.findByName(items.get(1).getName())).thenReturn(testItems);
    }

    @Test
    public void getItemsTest(){
        final ResponseEntity<List<Item>> getItemsResponse = itemController.getItems();
        assertNotNull(getItemsResponse);
        assertEquals(TestUtils.OK_RESPONSE, getItemsResponse.getStatusCodeValue());

        List<Item> actualItems = getItemsResponse.getBody();
        assertNotNull(actualItems);
        assertArrayEquals(items.toArray(), actualItems.toArray());
    }

    @Test
    public void getItemByIdTest(){
        final ResponseEntity<Item> getItemByIdResponse = itemController.getItemById(1L);
        assertNotNull(getItemByIdResponse);
        assertEquals(TestUtils.OK_RESPONSE, getItemByIdResponse.getStatusCodeValue());

        Item actualItem = getItemByIdResponse.getBody();
        assertNotNull(actualItem);
        assertEquals(items.get(0).getId(), actualItem.getId());
        assertEquals(items.get(0).getName(), actualItem.getName());
        assertEquals(items.get(0).getPrice(), actualItem.getPrice());
        assertEquals(items.get(0).getDescription(), actualItem.getDescription());
    }

    @Test
    public void getItemByName(){
        final ResponseEntity<List<Item>> getItembyNameResponse = itemController.getItemsByName(items.get(1).getName());

        assertNotNull(getItembyNameResponse);
        assertEquals(TestUtils.OK_RESPONSE, getItembyNameResponse.getStatusCodeValue());

        List<Item> actualItems = getItembyNameResponse.getBody();
        assertNotNull(actualItems);
        assertEquals(1, actualItems.size());
        assertEquals(items.get(1).getId(), actualItems.get(0).getId());
        assertEquals(items.get(1).getName(), actualItems.get(0).getName());
        assertEquals(items.get(1).getPrice(), actualItems.get(0).getPrice());
        assertEquals(items.get(1).getDescription(), actualItems.get(0).getDescription());
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
        item1.setId(2L);
        item1.setName("Square Widget");
        item1.setPrice(BigDecimal.valueOf(1.99));
        item1.setDescription("A widget that is square");
        items.add(item2);

        return items;
    }
}
