package self.ed.repository.impl;

import self.ed.entity.User;
import self.ed.repository.CustomUserRepository;

public class CustomUserRepositoryImpl implements CustomUserRepository {
    @Override
    public User customMethod(User user) {
        return user;
    }
}
