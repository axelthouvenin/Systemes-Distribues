import kong.unirest.*;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.*;

public class Status {
    private final String default_username;
    private final String default_repo;

    private final String auth_user;
    private final String auth_token;

    private String chosen_username = null;
    private String chosen_repo;
    private Integer chosen_issue = null;

    private String repos_url = null;

    private final Scanner input = new Scanner(System.in);

    public Status(String default_username, String default_repo, String auth_user, String auth_token) {
        this.default_username = default_username;
        this.default_repo = default_repo;
        this.auth_user = auth_user;
        this.auth_token = auth_token;
        while (this.chosen_username == null)
            this.pickUsername();
    }

    private void pickUsername() {
        System.out.printf("Username [%s]: ", default_username);
        String chosen_username = input.nextLine();
        if (chosen_username.equals("")) {
            chosen_username = default_username;
        }

        HttpResponse<JsonNode> r = Unirest.get("https://api.github.com/users/{user}")
                .routeParam("user", chosen_username)
                .basicAuth(auth_user, auth_token)
                .asJson();

        if (r.getStatus() == HttpStatus.NOT_FOUND) {
            System.out.printf("Error user %s not found.%n", chosen_username);
            return;
        }

        this.chosen_username = chosen_username;

        JsonNode node = r.getBody();
        String[][] user_fields = {{"name", "Name"}, {"company", "Company"}, {"blog", "Blog"}, {"location", "Location"},
                {"email", "EMail"}, {"hireable", "Hireable"}, {"bio", "Bio"}, {"twitter_username", "Twitter"}};

        for (String[] field : user_fields) {
            if (!node.getObject().isNull(field[0]))
                System.out.printf("\t%s: %s%n", field[1], node.getObject().getString(field[0]));
        }

        // List repos for this user
        if (this.chosen_username.equals(auth_user))
            this.repos_url = "https://api.github.com/user/repos";
        else
            this.repos_url = node.getObject().getString("repos_url");
    }

    public void listAndPickRepos() {
        if (this.repos_url == null) {
            System.out.println("Error: Pick a username first.");
            return;
        }

        int page = 1;
        boolean have_repo = false;
        while (!have_repo) {
            HttpResponse<JsonNode> r = Unirest.get(this.repos_url)
                    .queryString("per_page", "10")
                    .queryString("page", Integer.toString(page))
                    .basicAuth(auth_user, auth_token)
                    .asJson();

            switch (r.getStatus()) {
                case HttpStatus.NOT_FOUND:
                    System.out.println("Error: User not found.");
                    return;
                case HttpStatus.UNAUTHORIZED:
                case HttpStatus.FORBIDDEN:
                    System.out.println("Error: Authentication failed.");
                    return;
            }

            JSONArray repos_array = r.getBody().getArray();
            System.out.printf("\tRepositories (Page %d):%n", page);
            for (Object repo_o : repos_array) {
                JSONObject repo = (JSONObject) repo_o;
                String repo_name = repo.getString("name");
                String locked;
                if (repo.getBoolean("private"))
                    locked = "\uD83D\uDD12";
                else
                    locked = "\uD83D\uDD13";
                System.out.printf("\t\t%s %s%n", locked, repo.getString("full_name"));
            }
            String repo_str = "Repository (+/- to switch page)";
            if (Objects.equals(this.chosen_username, this.default_username))
                System.out.printf("%s [%s]: ", repo_str, default_repo);
            else
                System.out.printf("%s: ", repo_str);

            this.chosen_repo = input.nextLine();
            if (Objects.equals(this.chosen_repo, "+")){
                page++;
                continue;
            }
            if (Objects.equals(this.chosen_repo, "-")){
                if (page > 1)
                    page--;
                continue;
            }

            if (this.chosen_repo.equals("") && Objects.equals(chosen_username, default_username)) {
                this.chosen_repo = default_repo;
            }

            if (this.chosen_repo.contains("/")) {
                String[] split = this.chosen_repo.split("/");
                HttpResponse testRequest = Unirest.get("https://api.github.com/users/{user}")
                        .basicAuth(auth_user, auth_token)
                        .routeParam("user", split[0])
                        .asEmpty();
                if (testRequest.getStatus() == HttpStatus.NOT_FOUND) {
                    System.out.printf("Error: User %s does not exist.%n", split[0]);
                    continue;
                }
                chosen_username = split[0];
                chosen_repo = split[1];
            }

            HttpResponse testRequest = Unirest.get("https://api.github.com/repos/{user}/{repo}")
                    .basicAuth(auth_user, auth_token)
                    .routeParam("user", chosen_username)
                    .routeParam("repo", chosen_repo)
                    .asEmpty();
            if (testRequest.getStatus() != HttpStatus.OK)
                System.out.printf("Error repository %s not found.%n", this.chosen_repo);
            else {
                System.out.printf("Picked repository %s.%n", this.chosen_repo);
                have_repo = true;
            }
        }
    }

    private void showRepo(String name) {
        HttpResponse<JsonNode> r = Unirest.get("https://api.github.com/repos/{user}/{repo}")
                .routeParam("user", chosen_username)
                .routeParam("repo", chosen_repo)
                .asJson();
        JSONObject repo = r.getBody().getObject();
        System.out.printf("\t%s: %s%n", "Name", repo.getString("full_name"));
        if (!repo.isNull("description")) {
            System.out.printf("\t%s: %s%n", "Description", repo.getString("description"));
        }
        if (!repo.isNull("language"))
            System.out.printf("\t%s: %s%n", "Language", repo.getString("language"));
        // TODO: what happens if the user deactivates issues?
        System.out.printf("\t%s: %d%n", "Open Issues", repo.getInt("open_issues_count"));
    }

    public String getRepository() {
        return chosen_repo;
    }

    public String getUsername() {
        return chosen_username;
    }

    public void listIssues() {
        HttpResponse<List<Issue>> r =
                Unirest.get("https://api.github.com/repos/{owner}/{repo}/issues")
                .routeParam("owner", this.chosen_username)
                .routeParam("repo", this.chosen_repo)
                .queryString("state", "all")
                .basicAuth(auth_user, auth_token)
                .accept("application/vnd.github.v3+json")
                .asObject(new GenericType<List<Issue>>(){});
        if (r.getStatus() == HttpStatus.NOT_FOUND) {
            System.out.println("Error: Could not retrieve the issues.");
            return;
        }
        List<Issue> issues = r.getBody();
        Collections.reverse(issues);
        for (Issue issue : issues) {
            System.out.printf("  %s%n", issue);
        }
    }

    public void pickIssue() {
        System.out.print("Issue number: ");
        String issue_str = input.nextLine();

        try {
            Integer issue_num = Integer.parseInt(issue_str);
            inspectIssue(issue_num);
        }
        catch(NumberFormatException ex){
            System.out.printf("Error %s is not a number.%n", issue_str);
        }
    }

    public Issue inspectActiveIssue() {
        if (this.chosen_issue == null) {
            System.out.printf("Error: please pick an issue first.%n");
            return null;
        }
        return inspectIssue(this.chosen_issue);
    }

    private Issue inspectIssue(Integer issue_num) {
        HttpResponse<Issue> r = Unirest.get("https://api.github.com/repos/{owner}/{repo}/issues/{issue}")
                .routeParam("owner", this.chosen_username)
                .routeParam("repo", this.chosen_repo)
                .routeParam("issue", issue_num.toString())
                .queryString("state", "all")
                .basicAuth(auth_user, auth_token)
                .accept("application/vnd.github.v3+json")
                .asObject(Issue.class);
        if (r.getStatus() == HttpStatus.NOT_FOUND) {
            System.out.printf("Error: Issue %d does not exist.%n", issue_num);
        }
        Issue issue = r.getBody();
        this.updateComments(issue);
        System.out.println(issue.getId());
        System.out.println(issue.detailedDescription());
        for (Comment c : issue.getComments()) {
            System.out.println(c);
        }
        this.chosen_issue = issue_num;
        return issue;
    }

    private void updateComments(Issue issue) {
        HttpResponse<List<Comment>> r = Unirest.get("https://api.github.com/repos/{owner}/{repo}/issues/{issue}/comments")
                .routeParam("owner", this.chosen_username)
                .routeParam("repo", this.chosen_repo)
                .routeParam("issue", issue.getNumber().toString())
                .queryString("state", "all")
                .basicAuth(auth_user, auth_token)
                .accept("application/vnd.github.v3+json")
                .asObject(new GenericType<List<Comment>>(){});
        // TODO: error handling
        issue.setComments(r.getBody());
        int menuId = 0;
        for (Comment c: issue.getComments()) {
            if (Objects.equals(c.getUser().getLogin(), this.auth_user))
                c.setIsByUs(true);
                c.setMenuId(menuId);
                menuId++;
        }
    }

    public void deleteComment() {
        Issue issue = this.inspectActiveIssue();
        if (issue == null)
            return;
        System.out.print("Comment to delete: ");
        String comment_str = input.nextLine();
        try {
            int comment_id = Integer.parseInt(comment_str);
            for (Comment c : issue.getComments()) {
                if (c.getMenuId() == comment_id) {
                    Unirest.delete(c.getUrl())
                            .basicAuth(auth_user, auth_token)
                            .accept("application/vnd.github.v3+json")
                            .asEmpty();
                    return;
                }
            }
            System.out.printf("Error: no comment %d.%n", comment_id);
        } catch (NumberFormatException ex) {
            System.out.printf("Error: %s is not a number.%n", comment_str);
        }
    }

    @Override
    public String toString() {
        if (chosen_issue != null)
            return String.format("→ Active repository: %s/%s. Active issue: %d.",
                    chosen_username, chosen_repo, chosen_issue);
        return String.format("→ Active repository: %s/%s.",
                chosen_username, chosen_repo);
    }

    public void writeComment() {
        if (this.chosen_issue == null) {
            System.out.println("Error: please pick an issue first.");
            return;
        }
        System.out.print("Your comment: ");
        String comment_str = input.nextLine();
        Comment c = new Comment(comment_str);

        HttpResponse<Comment> r = Unirest.post(
                "https://api.github.com/repos/{owner}/{repo}/issues/{issue}/comments")
                .routeParam("owner", chosen_username)
                .routeParam("repo", chosen_repo)
                .routeParam("issue", chosen_issue.toString())
                .basicAuth(auth_user, auth_token)
                .accept("application/vnd.github.v3+json")
                .body(c).asObject(Comment.class);

        if (r.getStatus() == HttpStatus.FORBIDDEN) {
            System.out.println("Error: Unauthorized.");
            return;
        }
        if (r.getStatus() == HttpStatus.GONE) {
            System.out.println("Error: Gone.");
            return;
        }
        if (r.getStatus() == HttpStatus.NOT_FOUND) {
            System.out.println("Error: Either the user, the repo, or the issue was not found.");
            return;
        }
        r.getBody().setIsByUs(true);
        System.out.println(r.getBody());
    }

}