package models;

import org.hibernate.validator.constraints.URL;
import play.data.validation.Constraints.*;
import utils.Convert;

import javax.persistence.*;
import play.libs.Json;


@Entity
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    @Required
    public String link;
    public String slug;

    public Link () {
    }

    public Link (String link) {
        this.link = link;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void updateSlug() {
        if (this.id == null){
            throw new NullPointerException(String.format("id of %s is null", this.link));
        }
        this.slug = Convert.encode(this.id);
    }

    public String toJson() {
        return Json.prettyPrint(Json.toJson(this));
    }
}