package org.softuni.main.database.repositories;

import org.softuni.main.database.models.User;

import java.util.List;

public class UserRepository extends BaseRepository implements Repository {

    private boolean create(String username, String password) {
        this.entityManager.persist(new User(username, password));
        return true;
    }

    @SuppressWarnings(value = "unchecked")
    private User[] findAll() {
        List<User> resultList = this.entityManager
                .createNativeQuery("SELECT * FROM users", User.class)
                .getResultList();

        return resultList.toArray(new User[resultList.size()]);
    }
}
