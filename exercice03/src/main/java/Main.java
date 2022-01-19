import kong.unirest.*;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
// TODO: add a way to abort/go back
public class Main {
    public static void main(String[] args) {
        String username = "hansjoergschurr";
        String class_project = "Systemes-Distribues";

        /* Depends on whether one opens the individual exercise or the entire Git repository in InteliJ */
        String token_file = "exercice03/secret_token";

        Scanner input = new Scanner(System.in);
        // TODO: add a way to abort/go back
        try (BufferedReader br = new BufferedReader(new FileReader(token_file))) {
            String token = br.readLine();
            Status status = new Status(username, class_project, username, token);
            System.out.printf("Selected user %s.%n", status.getUsername());
            status.listAndUpdateRepos();
            status.pickRepo();
            System.out.printf("Selected repository %s.%n", status.getRepository());
            while (true) {
                System.out.println(" Pick a [R]epository.");
                System.out.println(" [L]ist issue.");
                System.out.println(" [I]inspect or edit an issue.");
                System.out.println(" [E]xit.");
                System.out.print("Choose command: ");
                String choice = input.nextLine();
                switch (choice.charAt(0)) {
                    case 'R':
                        status.listAndUpdateRepos();
                        status.pickRepo();
                        System.out.printf("Selected repository %s.%n", status.getRepository());
                        break;
                    case 'L':
                        status.listIssues();
                        break;
                    case 'I':
                        status.inspectIssue();
                        break;
                    case 'E':
                        return;
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}

