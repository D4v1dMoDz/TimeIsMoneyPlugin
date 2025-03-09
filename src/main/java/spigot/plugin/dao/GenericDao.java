package spigot.plugin.dao;

import java.util.List;
import java.util.Optional;

public interface GenericDao<T, ID> {
    Optional<T> getById(ID id);
    List<T> getAll();
    T save(T entity);
    void update(T entity);
    void delete(ID id);
}
