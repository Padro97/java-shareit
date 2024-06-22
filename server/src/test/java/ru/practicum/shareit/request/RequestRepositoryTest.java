package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class RequestRepositoryTest {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User requester;
    private User anotherUser;
    private ItemRequest itemRequest;
    private ItemRequest anotherItemRequest;

    @BeforeEach
    public void setUp() {
        requester = new User();
        requester.setName("Requester");
        requester.setEmail("requester@example.com");
        entityManager.persistAndFlush(requester);

        anotherUser = new User();
        anotherUser.setName("Another User");
        anotherUser.setEmail("anotheruser@example.com");
        entityManager.persistAndFlush(anotherUser);

        itemRequest = new ItemRequest();
        itemRequest.setDescription("Request 1");
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now().minusDays(1));
        entityManager.persistAndFlush(itemRequest);

        anotherItemRequest = new ItemRequest();
        anotherItemRequest.setDescription("Request 2");
        anotherItemRequest.setRequester(anotherUser);
        anotherItemRequest.setCreated(LocalDateTime.now());
        entityManager.persistAndFlush(anotherItemRequest);
    }

    @Test
    public void testFindAllByRequesterIdOrderByCreatedDesc() {
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(requester.getId());
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0)).isEqualTo(itemRequest);
    }

    @Test
    public void testGetAllRequests() {
        Page<ItemRequest> requestsPage = requestRepository.getAllRequests(requester.getId(), PageRequest.of(0, 10));
        List<ItemRequest> requests = requestsPage.getContent();
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0)).isEqualTo(anotherItemRequest);
    }
}
