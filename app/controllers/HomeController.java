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

    public Result index(String name) {
        return ok(views.html.index.render(name));
    }

    public Result addLink(Http.Request request) {
        String url = request.body().asJson().get("link").asText();
        Link link;
        try {
            link = linkRepository.add(new Link(url)).toCompletableFuture().get();
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError("Insert new link failed");
        }
        link.updateSlug();
        Link result;
        try {
            result = linkRepository.update(link).toCompletableFuture().get();
            result.setSlug(absolutePath(request,result.getSlug()));
            return ok(result.toJson());
        } catch (InterruptedException | ExecutionException e) {
            return internalServerError("addLink failed");
        }
    }

    public String absolutePath(Http.Request request, String slug) {
        String http = request.secure()?"https://":"http://";
        return http+request.host()+request.path()+'/'+slug;
    }

    public Result getLink(String slug) {
        String url;
        try {
            url = linkRepository.findBySlug(slug).toCompletableFuture().get().getLink();
            if (!"^\\w+://.*".matches(url.toLowerCase())) {
                url = "http://" + url;
            }
            return movedPermanently(url);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return internalServerError("getLink failed");
    }
}
