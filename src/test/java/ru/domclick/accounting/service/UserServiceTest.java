package ru.domclick.accounting.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.domclick.accounting.entity.UserEntity;
import ru.domclick.accounting.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by dmitry on 28.06.17
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    private UserService userService;
    private List<UserEntity> createdUsers;

    @Before
    public void setUp() {
        createdUsers = new ArrayList<>();
        createdUsers.add(userService.create("Frank"));
    }

    @Test
    public void testCreate() {
        String name = "John";
        UserEntity createdUser = userService.create(name);
        createdUsers.add(createdUser);
        assertEquals(name, createdUser.getName());
        assertNotNull(createdUser.getApiKey());
    }

    @Test
    public void testGet() {
        UserEntity fetchedUser = userService.getUserByApiKey(createdUsers.get(0).getApiKey());
        assertNotNull(fetchedUser.getName());
        assertNotNull(fetchedUser.getApiKey());
    }

    @After
    public void tearDown() {
        userRepository.delete(createdUsers);
    }
}
