package ru.practicum.shareit.item.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ItemApiPathConstants {
    public static final String ITEMS_PATH = "/items";
    public static final String ITEM_ID_PATH = "/{itemId}";
    public static final String SEARCH_ITEMS_PATH = "/search";
    public static final String COMMENTS = "/{itemId}/comment";
}
