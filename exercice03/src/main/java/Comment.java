import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Comment {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long id;
    private String body;
    private User user;
    private String url;

    @JsonIgnore
    private Integer menuId = -1;

    @JsonIgnore
    private Boolean is_by_us = false;

    public Comment() {
    }

    public Comment(String body) {
        this.body = body;
    }

    public long getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public User getUser() {
        return user;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        if (this.is_by_us)
            return String.format("[%d] by: %s%n%s", menuId, user.getLogin(), body);
        else
            return String.format("by: %s%n%s", user.getLogin(),  body);
    }

    @JsonIgnore
    public Boolean getIsByUs() {
        return is_by_us;
    }

    @JsonIgnore
    public void setIsByUs(Boolean is_by_us) {
        this.is_by_us = is_by_us;
    }

    @JsonIgnore
    public Integer getMenuId() {
        return menuId;
    }

    @JsonIgnore
    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }
}
