import kong.unirest.*;
import kong.unirest.jackson.JacksonObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static List<String> requestsIssued = new ArrayList<>();

    public static MetricContext startMetric(HttpRequestSummary httpRequest) {
        String description = String.format("%s: %s", httpRequest.getHttpMethod(), httpRequest.getUrl());
        requestsIssued.add(description);
        return Main::stopMetric;
    }

    public static void stopMetric(HttpResponseSummary httpResponse, Exception ex) {
    }

    public static void main(String[] args) {
        Unirest.config().setObjectMapper(new JacksonObjectMapper());
        Unirest.config().instrumentWith(Main::startMetric);

        String username = "hansjoergschurr";
        String class_repo = "Systemes-Distribues";

        /* Depends on whether one opens the individual exercise or the entire Git repository in InteliJ */
        String token_file = "exercice03/secret_token";

        Scanner input = new Scanner(System.in);
        try (BufferedReader br = new BufferedReader(new FileReader(token_file))) {
            String token = br.readLine();

            Status status = new Status(username, class_repo, username, token);
            System.out.printf("Selected user %s.%n", status.getUsername());
            status.listAndPickRepos();
            while (true) {
                System.out.println(status);
                System.out.println(" Pick a [R]epository.");
                System.out.println(" [L]ist issue.");
                System.out.println(" [P]ick an issue.");
                System.out.println(" [I]inspect active issue.");
                System.out.println(" [W]rite comment.");
                System.out.println(" [D]elete comment.");
                System.out.println(" Print repor[T].");
                System.out.println(" [E]xit.");
                System.out.print("Choose command: ");
                String choice = input.nextLine();
                switch (choice.charAt(0)) {
                    case 'R':
                        status.listAndPickRepos();
                        System.out.printf("Selected repository %s/%s.%n",
                                status.getUsername(),
                                status.getRepository());
                        break;
                    case 'L':
                        status.listIssues();
                        break;
                    case 'P':
                        status.pickIssue();
                        break;
                    case 'I':
                        status.inspectActiveIssue();
                        break;
                    case 'W':
                        status.writeComment();
                        break;
                    case 'D':
                        status.deleteComment();
                        break;
                    case 'T':
                        System.out.printf("In total %d requests issued.%n", requestsIssued.size());
                        for (String s : requestsIssued)
                            System.out.printf(" %s%n", s);
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

