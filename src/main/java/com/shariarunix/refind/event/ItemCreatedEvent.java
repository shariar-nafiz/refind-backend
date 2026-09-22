package com.shariarunix.refind.event;

import com.shariarunix.refind.entity.enums.ItemType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ItemCreatedEvent extends ApplicationEvent {

    private final Long itemId;
    private final ItemType itemType;

    public ItemCreatedEvent(Object source, Long itemId, ItemType itemType) {
        super(source);
        this.itemId = itemId;
        this.itemType = itemType;
    }
}
