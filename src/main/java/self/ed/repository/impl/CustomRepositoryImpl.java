package self.ed.repository.impl;

import self.ed.repository.CustomRepository;

public class CustomRepositoryImpl<T> implements CustomRepository<T> {
    @Override
    public T customMethod(T user) {
        return user;
    }
}
