package WalleServer.utils;

import WalleServer.persistence.JSONArrayList;

import java.util.Optional;

public interface CRUD<T> {

    Optional<T> get(String curp);

    JSONArrayList<T> getAll();

    T create(T person);

    T update(T person);

    void delete(String curp);

}
