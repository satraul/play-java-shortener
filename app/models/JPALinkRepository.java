package models;

import play.db.jpa.JPAApi;
import utils.Convert;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Provide JPA operations running inside of a thread pool sized to the connection pool
 */
public class JPALinkRepository implements LinkRepository {

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public JPALinkRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Link> add(Link link) {
        return supplyAsync(() -> wrap(em -> insert(em, link)), executionContext);
    }

    @Override
    public CompletionStage<Stream<Link>> list() {
        return supplyAsync(() -> wrap(em -> list(em)), executionContext);
    }

    @Override
    public CompletionStage<Link> findBySlug(String slug) {
        return supplyAsync(() -> wrap(em -> findBySlug(em, slug)), executionContext);
    }

    @Override
    public CompletionStage<Link> update(Link link) {
        return supplyAsync(() -> wrap(em -> update(em, link)), executionContext);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Link insert(EntityManager em, Link person) {
        em.persist(person);
        return person;
    }

    private Stream<Link> list(EntityManager em) {
        List<Link> persons = em.createQuery("select p from Link p", Link.class).getResultList();
        return persons.stream();
    }

    private Link findBySlug(EntityManager em, String slug) {
        Link person = em.find(Link.class, Convert.decode(slug));
        return person;
    }

    private Link update(EntityManager em, Link person) {
        em.merge(person);
        return person;
    }
}
