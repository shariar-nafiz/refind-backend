package com.shariarunix.refind.service;

import com.shariarunix.refind.dto.item.CreateItemRequest;
import com.shariarunix.refind.dto.item.ItemDetailResponse;
import com.shariarunix.refind.dto.item.UpdateItemRequest;
import com.shariarunix.refind.dto.item.UpdateItemStatusRequest;
import com.shariarunix.refind.entity.Category;
import com.shariarunix.refind.entity.Item;
import com.shariarunix.refind.entity.Location;
import com.shariarunix.refind.entity.User;
import com.shariarunix.refind.entity.enums.ItemStatus;
import com.shariarunix.refind.entity.enums.ItemType;
import com.shariarunix.refind.event.ItemCreatedEvent;
import com.shariarunix.refind.exception.BadRequestException;
import com.shariarunix.refind.exception.ResourceNotFoundException;
import com.shariarunix.refind.repository.CategoryRepository;
import com.shariarunix.refind.repository.ItemMediaRepository;
import com.shariarunix.refind.repository.ItemRepository;
import com.shariarunix.refind.repository.LocationRepository;
import com.shariarunix.refind.repository.MediaRepository;
import com.shariarunix.refind.repository.UserRepository;
import com.shariarunix.refind.service.impl.ItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private ItemMediaRepository itemMediaRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User testUser;
    private Category testCategory;
    private Location testLocation;
    private Item testItem;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("user@example.com")
                .fullName("John Doe")
                .build();
        testUser.setId(100L);

        testCategory = Category.builder()
                .name("Wallets")
                .slug("wallets")
                .isActive(true)
                .build();
        testCategory.setId(1L);

        testLocation = Location.builder()
                .division("Dhaka")
                .district("Dhaka")
                .thana("Dhanmondi")
                .isActive(true)
                .build();
        testLocation.setId(2L);

        testItem = Item.builder()
                .user(testUser)
                .category(testCategory)
                .location(testLocation)
                .type(ItemType.LOST)
                .title("Lost Black Leather Wallet")
                .description("Lost near Dhanmondi 27 around 4 PM containing cards.")
                .incidentDateTime(Instant.now().minusSeconds(3600))
                .status(ItemStatus.OPEN)
                .viewCount(5)
                .tags(new ArrayList<>(List.of("wallet", "leather", "black")))
                .itemMediaList(new ArrayList<>())
                .build();
        testItem.setId(500L);
    }

    @Test
    @DisplayName("createItem creates and persists item and fires domain event")
    void createItem_Success() {
        CreateItemRequest request = CreateItemRequest.builder()
                .type(ItemType.LOST)
                .categoryId(1L)
                .locationId(2L)
                .title("Lost Black Leather Wallet")
                .description("Lost near Dhanmondi 27 around 4 PM containing cards.")
                .incidentDateTime(Instant.now().minusSeconds(3600))
                .tags(List.of("wallet", "leather"))
                .build();

        when(userRepository.findById(100L)).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(locationRepository.findById(2L)).thenReturn(Optional.of(testLocation));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item toSave = invocation.getArgument(0);
            toSave.setId(500L);
            return toSave;
        });

        ItemDetailResponse response = itemService.createItem(100L, request);

        assertThat(response.getId()).isEqualTo(500L);
        assertThat(response.getTitle()).isEqualTo("Lost Black Leather Wallet");
        assertThat(response.getCategory().getName()).isEqualTo("Wallets");
        assertThat(response.getLocation().getThana()).isEqualTo("Dhanmondi");
        assertThat(response.getIsOwner()).isTrue();

        verify(itemRepository).save(any(Item.class));
        verify(eventPublisher).publishEvent(any(ItemCreatedEvent.class));
    }

    @Test
    @DisplayName("createItem throws BadRequestException when secret question has no answer on FOUND item")
    void createItem_FoundItemMissingSecretAnswer() {
        CreateItemRequest request = CreateItemRequest.builder()
                .type(ItemType.FOUND)
                .categoryId(1L)
                .locationId(2L)
                .title("Found Leather Wallet")
                .description("Found on sidewalk near Dhanmondi 27")
                .incidentDateTime(Instant.now().minusSeconds(1800))
                .secretIdentifierQuestion("What brand is printed inside?")
                .secretIdentifierAnswer("") // Empty answer
                .build();

        when(userRepository.findById(100L)).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(locationRepository.findById(2L)).thenReturn(Optional.of(testLocation));

        assertThatThrownBy(() -> itemService.createItem(100L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Secret identifier answer is required");
    }

    @Test
    @DisplayName("getItemById increments view count and masks secret answer")
    void getItemById_Success() {
        testItem.setSecretIdentifierQuestion("What color is the inside lining?");
        testItem.setSecretIdentifierAnswer("Crimson red");

        when(itemRepository.findById(500L)).thenReturn(Optional.of(testItem));

        ItemDetailResponse response = itemService.getItemById(500L, null);

        assertThat(response.getId()).isEqualTo(500L);
        assertThat(response.getSecretIdentifierQuestion()).isEqualTo("What color is the inside lining?");
        verify(itemRepository).incrementViewCount(500L);
    }

    @Test
    @DisplayName("updateItem throws BadRequestException when user is not owner")
    void updateItem_UnauthorizedUser() {
        when(itemRepository.findById(500L)).thenReturn(Optional.of(testItem));

        UpdateItemRequest request = UpdateItemRequest.builder()
                .title("New title")
                .build();

        assertThatThrownBy(() -> itemService.updateItem(500L, 999L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("You do not have permission");
    }

    @Test
    @DisplayName("updateItemStatus allows owner to resolve item")
    void updateItemStatus_Success() {
        when(itemRepository.findById(500L)).thenReturn(Optional.of(testItem));
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        UpdateItemStatusRequest request = UpdateItemStatusRequest.builder()
                .status(ItemStatus.RESOLVED)
                .build();

        ItemDetailResponse response = itemService.updateItemStatus(500L, 100L, request);

        assertThat(response.getStatus()).isEqualTo(ItemStatus.RESOLVED);
        verify(itemRepository).save(testItem);
    }

    @Test
    @DisplayName("deleteItem allows author to remove item report")
    void deleteItem_Success() {
        when(itemRepository.findById(500L)).thenReturn(Optional.of(testItem));

        itemService.deleteItem(500L, 100L);

        verify(itemRepository).delete(testItem);
    }
}
