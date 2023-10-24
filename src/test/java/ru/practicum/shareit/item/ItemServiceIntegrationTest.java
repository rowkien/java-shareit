package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

@SpringBootTest
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldCreateItem() {
        User owner = User
                .builder()
                .name("owner")
                .email("email@yandex.ru")
                .build();
        userRepository.save(owner);
        Item item = Item
                .builder()
                .name("item")
                .description("description")
                .owner(owner)
                .available(true)
                .build();
        Assertions.assertEquals(item.getName(), itemService.createItem(owner.getId(), item).getName());
    }

}
