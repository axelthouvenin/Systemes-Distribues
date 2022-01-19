import java.util.List;

public class Issue {
    private Long id;
    private Integer number;
    private String state;
    private String title;
    private String body;
    private Integer comments;
    private List<Label> labels;

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

    public Integer getComments() {
        return comments;
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
        return String.format("[%d] %s '%s' %s (%d comments)", number, state_icon, title, labels, comments);
    }

    public String detailedDescription() {
        if (this.body != null)
            return String.format("%s%n-----%n%s-----%n", this, body);
        else
            return this.toString();
    }

}
