package ru.practicum.shareit.user;

public class UserMapper {
    public static UserBookingDto toUserBookingDto(User user) {
        return UserBookingDto.builder()
                .id(user.getId())
                .build();
    }
}
