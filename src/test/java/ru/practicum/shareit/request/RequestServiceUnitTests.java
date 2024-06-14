package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForDto;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class RequestServiceUnitTests {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private RequestServiceImpl requestService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAdd() {
        Long userId = 1L;
        UserDto userDto = UserDto.builder()
                .id(userId)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Request description")
                .build();
        when(userService.getById(userId)).thenReturn(userDto);
        ItemRequest request = new ItemRequest();
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDto result = requestService.add(requestDto, userId);

        assertThat(result).isNotNull();
    }

    @Test
    public void testGetByUser() {
        Long userId = 1L;
        List<ItemRequest> requests = Collections.singletonList(new ItemRequest());
        when(userService.getById(userId)).thenReturn(UserDto.builder().id(userId).name("John Doe").email("john.doe@example.com").build());
        when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId)).thenReturn(requests);
        when(itemService.getAllByRequestId(anyLong())).thenReturn(Collections.emptyList());

        List<ItemRequestForDto> result = requestService.getByUser(userId);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getItems()).isEmpty();
    }

    @Test
    public void testGetAll() {
        Long userId = 1L;
        Long from = 0L;
        Long size = 10L;
        int page = 0;
        Pageable pageable = PageRequest.of(page, size.intValue());
        Page<ItemRequest> pageRequests = new PageImpl<>(Collections.singletonList(new ItemRequest()));
        when(requestRepository.getAllRequests(userId, pageable)).thenReturn(pageRequests);
        when(itemService.getAllByRequestId(anyLong())).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = requestService.getAll(from, size, userId);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getItems()).isEmpty();
    }

    @Test
    public void testGetAllWithNegativePaginationParameters() {
        Long userId = 1L;

        assertThatThrownBy(() -> requestService.getAll(-1L, 10L, userId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Параметры пагинации не могут быть отрицательными.");
    }

    @Test
    public void testGetById() {
        Long requestId = 1L;
        Long userId = 1L;
        UserDto userDto = UserDto.builder().id(userId).name("John Doe").email("john.doe@example.com").build();
        ItemRequest request = new ItemRequest();
        request.setId(requestId);
        when(userService.getById(userId)).thenReturn(userDto);
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemService.getAllByRequestId(anyLong())).thenReturn(Collections.emptyList());

        ItemRequestForDto result = requestService.getById(requestId, userId);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    public void testGetByIdThrowsExceptionWhenRequestNotFound() {
        Long requestId = 1L;
        Long userId = 1L;
        when(userService.getById(userId)).thenReturn(UserDto.builder().id(userId).name("John Doe").email("john.doe@example.com").build());
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.getById(requestId, userId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Запроса с таким ID не существует");
    }
}
