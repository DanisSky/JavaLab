package ru.itis.javalab.repositories;


public interface BlacklistRepository {
    void save(String token);

    boolean exists(String token);
}
