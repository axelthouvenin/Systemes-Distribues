import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        HttpResponse<String> response = Unirest.get("https://api.github.com/users/{user}")
                .routeParam("user", "hansjoergschurr")
                .asString();

        System.out.println(response.getBody());

        HttpResponse<String> response = Unirest.get("https://api.github.com/users/{user}")
                .routeParam("user", username)
                .asString();

        System.out.println(response.getBody());


        try (BufferedReader br = new BufferedReader(new FileReader("secret_token"))) {
            String token = br.readLine();

            HttpResponse<JsonNode> r = Unirest.get("https://api.github.com/users/{user}")
                    .routeParam("user", username)
                    .basicAuth(username, token)
                    .asJson();
            System.out.println(r.getBody());
            JsonNode node = r.getBody();
            int privateRepos = node.getObject().getInt("total_private_repos");
            System.out.print(privateRepos);

        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}

