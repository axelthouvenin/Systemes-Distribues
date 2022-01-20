import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Issue {
    private Long id;
    private Integer number;
    private String state;
    private String title;
    private String body;

    @JsonProperty("comments")
    private Integer numberOfComments;

    private List<Label> labels;

    @JsonIgnore
    private List<Comment> comments;

    public Issue() {
    }

    public Issue(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public Long getId() {
        return id;
    }

    public Integer getNumber() {
        return number;
    }

    public String getState() {
        return state;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Integer getNumberOfComments() {
        return numberOfComments;
    }

    public List<Label> getLabels() {
        return labels;
    }

    @Override
    public String toString() {
        String state_icon;
        switch (state) {
            case "closed":
                state_icon = "✔️";
                break;
            case "open":
                state_icon = "⚙️";
                break;
            default:
                state_icon = "?";
        }
        return String.format("[%d] %s '%s' %s (%d comments)", number, state_icon, title, labels, numberOfComments);
    }

    public String detailedDescription() {
        if (this.body != null)
            return String.format("%s%n-----%n%s-----%n", this, body);
        else
            return this.toString();
    }

    @JsonIgnore
    public List<Comment> getComments() {
        return comments;
    }

    @JsonIgnore
    public void setComments(List<Comment> comment_bodies) {
        this.comments = comment_bodies;
    }
}
