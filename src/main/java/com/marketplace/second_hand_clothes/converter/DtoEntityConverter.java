package com.marketplace.second_hand_clothes.converter;

public interface DtoEntityConverter<T, E> {
    T toDto(E e);
    E toEntity(T t);
}
