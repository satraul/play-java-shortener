package controllers;

import models.Link;
import models.*;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import javax.inject.Inject;
import java.util.concurrent.ExecutionException;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {
    private final LinkRepository linkRepository;
    private final HttpExecutionContext ec;

    @Inject
    public HomeController(LinkRepository linkRepository, HttpExecutionContext ec) {
        this.linkRepository = linkRepository;
        this.ec = ec;
    }

    public Result addLink(Http.Request request) {
        try {
            String url = request.body().asJson().get("link").asText();
            Link link = linkRepository.add(new Link(url)).toCompletableFuture().get();
            Link linkWithSlug = linkRepository.update(link.updateSlug()).toCompletableFuture().get();

            String protocol = request.secure() ? "https://" : "http://";
            linkWithSlug.setSlug(protocol + request.host() + request.path() + '/' + linkWithSlug.getSlug());

            return ok(linkWithSlug.toJson());
        } catch (InterruptedException | ExecutionException e) {
            return internalServerError("addLink failed");
        }
    }

    public Result getLink(String slug) {
        try {
            String url = linkRepository.findBySlug(slug).toCompletableFuture().get().getLink();
            if (!"^\\w+://.*".matches(url.toLowerCase())) {
                url = "http://" + url;
            }

            return movedPermanently(url);
        } catch (InterruptedException | ExecutionException e) {
            return internalServerError("getLink failed");
        }
    }
}
