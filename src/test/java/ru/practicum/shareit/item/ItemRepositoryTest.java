package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private ItemRequest itemRequest;
    private Item item;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        entityManager.persistAndFlush(user);

        itemRequest = new ItemRequest();
        itemRequest.setDescription("Test Request");
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        entityManager.persistAndFlush(itemRequest);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequest(itemRequest);
        entityManager.persistAndFlush(item);
    }

    @Test
    public void testFindAllByRequestId() {
        List<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
        assertThat(items).hasSize(1);
        assertThat(items.get(0)).isEqualTo(item);
    }

    @Test
    public void testFindAllByOwnerIdOrderByIdAsc() {
        Page<Item> itemsPage = itemRepository.findAllByOwnerIdOrderByIdAsc(user.getId(), PageRequest.of(0, 10));
        List<Item> items = itemsPage.getContent();
        assertThat(items).hasSize(1);
        assertThat(items.get(0)).isEqualTo(item);
    }

    @Test
    public void testSearch() {
        Page<Item> itemsPage = itemRepository.search("Test", PageRequest.of(0, 10));
        List<Item> items = itemsPage.getContent();
        assertThat(items).hasSize(1);
        assertThat(items.get(0)).isEqualTo(item);
    }
}