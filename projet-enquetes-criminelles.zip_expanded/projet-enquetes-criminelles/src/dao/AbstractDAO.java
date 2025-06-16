// src/dao/AbstractDAO.java
package dao;

import java.util.List;
import java.util.Optional;

public abstract class AbstractDAO<T> {

    protected abstract String file();
    protected abstract Class<T> type();

    public List<T> findAll() { return CSVUtil.read(file(), type()); }

    public Optional<T> find(java.util.function.Predicate<T> p) {
        return findAll().stream().filter(p).findFirst();
    }

    public void save(T bean) { CSVUtil.write(file(), List.of(bean), type(), true); }

    public void overwriteAll(List<T> all) { CSVUtil.write(file(), all, type(), false); }
}
