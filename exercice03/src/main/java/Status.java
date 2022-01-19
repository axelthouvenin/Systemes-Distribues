import kong.unirest.*;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.*;

public class Status {
    private final String auth_token;
    private final String default_username;
    private final String default_repo;
    private final String auth_user;
    private String username = null;

    private String repository;
    private String repos_url;

    private final Map<String, JSONObject> repos;

    private final Scanner input = new Scanner(System.in);

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

        // TODO : Any other errors?
        if (r.getStatus() == HttpStatus.NOT_FOUND) {
            System.out.printf("Error user %s not found.%n", chosen_username);
            return;
        }

        JsonNode node = r.getBody();
        String[] user_fields = {"name", "company", "blog", "location", "email", "hireable", "bio", "twitter_username"};

        for (String field : user_fields) {
            // TODO : Capitalize the first letter?
            if (!node.getObject().isNull(field))
                System.out.printf("\t%s: %s%n", field, node.getObject().getString(field));
        }
        this.username = chosen_username;
        if (this.username.equals(auth_user))
            this.repos_url = "https://api.github.com/user/repos?per_page=100";
        else
            this.repos_url = node.getObject().getString("repos_url");
    }

    // TODO: support default_username being different from auth user.
    public Status(String default_username, String default_repo, String auth_user, String auth_token) {
        this.default_username = default_username;
        this.default_repo = default_repo;
        this.auth_user = auth_user;
        this.auth_token = auth_token;
        while (this.username == null)
            this.pickUsername();
        this.repos = new HashMap<>();
    }

    public void listAndUpdateRepos() {
        HttpResponse<JsonNode> r = Unirest.get(this.repos_url)
                .basicAuth(auth_user, auth_token)
                .asJson();
        // TODO : handle errors here!
        JSONArray repos_array = r.getBody().getArray();
        System.out.println("\tRepositories:");
        for (Object repo_o : repos_array) {
            JSONObject repo = (JSONObject) repo_o;
            String repo_name = repo.getString("name");
            String locked;
            if (repo.getBoolean("private"))
                locked = "\uD83D\uDD12";
            else
                locked = "\uD83D\uDD13";
            System.out.printf("\t\t%s %s%n", locked, repo.getString("full_name"));
            this.repos.put(repo_name, repo);
        }
    }

    public void pickRepo() {
        boolean have_repo = false;
        while (!have_repo) {
            if (Objects.equals(this.username, this.default_username))
                System.out.printf("Repository [%s]: ", default_repo);
            else
                System.out.print("Repository: ");

            this.repository = input.nextLine();
            if (this.repository.equals("") && Objects.equals(username, default_username)) {
                this.repository = default_repo;
            }
            if (this.repos.containsKey(this.repository)) {
                this.showRepo(this.repository);
                have_repo = true;
            }
            else {
                System.out.printf("Error repository %s not found.%n", this.repository);
            }
        }
    }

    private void showRepo(String name) {
        // TODO: handle the error case where listAndUpdateRepos has not been called.
        JSONObject repo = this.repos.get(name);
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
        return repository;
    }

    public String getUsername() {
        return username;
    }

    public void listIssues() {
        HttpResponse<List<Issue>> r = Unirest.get("https://api.github.com/repos/{owner}/{repo}/issues")
                .routeParam("owner", this.username)
                .routeParam("repo", this.repository)
                .queryString("state", "all")
                .basicAuth(auth_user, auth_token)
                .accept("application/vnd.github.v3+json")
                .asObject(new GenericType<List<Issue>>(){});
        // TODO: handle error
        List<Issue> issues = r.getBody();
        Collections.reverse(issues);
        for (Issue issue : issues) {
            System.out.printf("  %s%n", issue);
        }
    }

    public void inspectIssue() {
        System.out.print("Issue number: ");
        String issue_str = input.nextLine();
        try {
            Integer issue_num = Integer.parseInt(issue_str);
            HttpResponse<Issue> r = Unirest.get("https://api.github.com/repos/{owner}/{repo}/issues/{issue}")
                    .routeParam("owner", this.username)
                    .routeParam("repo", this.repository)
                    .routeParam("issue", issue_num.toString())
                    .queryString("state", "all")
                    .basicAuth(auth_user, auth_token)
                    .accept("application/vnd.github.v3+json")
                    .asObject(Issue.class);
            if (r.getStatus() == HttpStatus.NOT_FOUND) {
                System.out.printf("Error issue %d does not exist.%n", issue_num);
                return;
            }
            System.out.println(r.getBody().detailedDescription());
        }
        catch (NumberFormatException ex) {
            System.out.printf("Error %s is not a number.%n", issue_str);
        }
    }
}