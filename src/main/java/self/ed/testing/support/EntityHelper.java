package self.ed.testing.support;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static self.ed.testing.support.RandomUtils.random;

/**
 * @author Anatolii
 */
@Repository
@Transactional
public class EntityHelper {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private SessionFactory sessionFactory;

    public <T> T create(Class<T> clazz) {
        return create(clazz, e -> {
            // no-op
        });
    }

    public <T> T create(Class<T> clazz, Consumer<T> callback) {
        T entity = random(clazz, getIdFieldName(clazz));
        callback.accept(entity);
        persist(entity);
        return entity;
    }

    public <T> T find(Class<T> clazz, Object id, String... lazyFieldsToInitialize) {
        T entity = entityManager.find(clazz, id);
        initializeLazyFields(entity, lazyFieldsToInitialize);
        return entity;
    }

    @SuppressWarnings("unchecked")
    public <T> T find(T entity) {
        return find((Class<T>) entity.getClass(), getId(entity));
    }

    public <T> List<T> findAll(Class<T> clazz) {
        return findAll(clazz, emptyMap());
    }

    public <T> List<T> findAll(Class<T> clazz, Map<String, Object> whereParams, String... lazyFieldsToInitialize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> rootEntity = query.from(clazz);
        Predicate[] restrictions = whereParams.entrySet().stream()
                .map(entry -> equalOrIsNull(cb, rootEntity.get(entry.getKey()), entry.getValue()))
                .toArray(Predicate[]::new);
        query.select(rootEntity).where(cb.and(restrictions));
        List<T> resultList = entityManager.createQuery(query).getResultList();
        resultList.forEach(entity -> initializeLazyFields(entity, lazyFieldsToInitialize));
        return resultList;
    }

    public void remove(Object entity) {
        entityManager.remove(entity);
    }

    public void removeAll(Class<?> clazz) {
        entityManager.createQuery("delete from " + clazz.getName()).executeUpdate();
    }

    public void merge(Object entity) {
        entityManager.merge(entity);
    }

    public void refresh(Object entity, String... lazyFieldsToInitialize) {
        entityManager.refresh(entity);
        initializeLazyFields(entity, lazyFieldsToInitialize);
    }

    private void persist(Object entity) {
        entityManager.persist(entity);
    }

    private Object getId(Object entity) {
        return entityManagerFactory.getPersistenceUnitUtil().getIdentifier(entity);
    }

    private String getIdFieldName(Class<?> clazz) {
        return entityManagerFactory.getMetamodel()
                .entity(clazz)
                .getSingularAttributes()
                .stream()
                .filter(SingularAttribute::isId)
                .findFirst()
                .map(SingularAttribute::getName)
                .orElseThrow(() -> new IllegalArgumentException("Cannot get id field name for " + clazz));
    }

    private void initializeLazyFields(Object entity, String... lazyFieldsToInitialize) {
        if (entity != null) {
            asList(lazyFieldsToInitialize).forEach(field -> initialize(getField(entity, field)));
        }
    }

    private static void initialize(Object lazyField) {
        // TODO: implement
    }

    private Object getField(Object entity, String field) {
        try {
            return readField(entity, field, true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Predicate equalOrIsNull(CriteriaBuilder cb, Expression<?> expression, Object value) {
        return value == null ? cb.isNull(expression) : cb.equal(expression, value);
    }
}
