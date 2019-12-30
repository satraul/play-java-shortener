package models;

import com.google.inject.ImplementedBy;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * This interface provides a non-blocking API for possibly blocking operations.
 */
@ImplementedBy(JPALinkRepository.class)
public interface LinkRepository {

    CompletionStage<Link> add(Link person);

    CompletionStage<Stream<Link>> list();

    CompletionStage<Link> findBySlug(String slug);

    CompletionStage<Link> update(Link link);
}
