package org.softuni.main.database.repositories;

public interface Repository {

    Object doAction(String action, Object... arguments);

    void dismiss();
}
