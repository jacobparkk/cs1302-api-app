package cs1302.api;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.control.Button;
import com.google.gson.Gson;
import javafx.application.Application;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;

/**
 * ApiApp prompts user to select a top 5 league in football and to input a year
 * and the app returns the team that won that league that year, then provides the user
 * with the wikipedia page of the team that won.
 */
public class ApiApp extends Application {
    private static final String API_FOOTBALL_URL_FORMAT =
        "https://api-football-standings.azharimm.dev/leagues/%s/standings?season=%s&sort=asc";
    private static final String WIKIPEDIA_API_URL =
        "https://en.wikipedia.org/w/rest.php/v1/search/page";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private static final Gson GSON = new Gson();

    private final String[] leagueNames =
    {"Premier League", "Ligue 1", "Serie A", "Bundesliga", "La Liga"};
    private final String[] leagueCodes = {"eng.1", "fra.1", "ita.1", "ger.1", "esp.1"};

    Stage stage;
    Scene scene;
    VBox root;

    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */

    public ApiApp() {
        root = new VBox();
    } // ApiApp

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {

        stage.setTitle("Top Team Wiki Finder");

        Label leagueLabel = new Label("Select a Top 5 league");
        ComboBox<String> dropDown = new ComboBox<>();
        for (String league : leagueNames) {
            dropDown.getItems().add(league);
        }
        // provides layout
        Label yearLabel = new Label("Enter Year");
        TextField yearField = new TextField();
        Button searchButton = new Button("Search");
        Label resultLabel = new Label();
        Label resultLabel2 = new Label();
        Label resultLabel3 = new Label();
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        searchButton.setOnAction(e -> {
            // all if statements determine if there is content stored in the variables
            String year = URLEncoder.encode(yearField.getText(), StandardCharsets.UTF_8);
            String league = dropDown.getValue();
            if (!year.isEmpty() && league != null) {
                String leagueCode = getLeagueCode(league);
                if (leagueCode != null) {
                    String topTeam = getTopTeam(year, leagueCode);
                    if (topTeam != null) {
                        resultLabel.setText(topTeam + " won the " +
                            league + " in the season that started in " + year + ".");

                        String teamWiki = searchTopTeam(topTeam);
                        if (teamWiki != null) {
                            resultLabel2.setText("Wiki URL for " + topTeam + ": " + teamWiki);
                            System.out.println(teamWiki);
                            webView.getEngine().load(teamWiki);
                        } else {
                            resultLabel2.setText("Failed to load wiki for the top team.");
                        }
                    } else {
                        resultLabel.setText("No top team found for the given year and league.");
                    }
                } else {
                    resultLabel.setText("Please enter a year and league.");
                }
            }
        });
        VBox layout = new VBox(10);
        layout.getChildren().addAll
            (leagueLabel, dropDown, yearLabel, yearField,
                searchButton, resultLabel, resultLabel2, webView);
        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Retrieves the league code corresponding to the league name.
     *
     * @return returns the league code that corresponds to the league name.
     * @param leagueName  name of the league
     */
    private String getLeagueCode(String leagueName) {
        for (int i = 0; i < leagueNames.length; i++) {
            if (leagueNames[i].equals(leagueName)) {
                return leagueCodes[i];
            }
        }
        return null;
    }

    /**
     * Retrieves the team that won the league that year.
     *
     * @return returns the top team of the league that year.
     * @param leagueCode  the league the user wants to know the winner of.
     * @param year  the year the user wants to know the winner of in the league.
     */
    private String getTopTeam(String year, String leagueCode) {
        try {
            // building url
            String url = String.format(API_FOOTBALL_URL_FORMAT, leagueCode, year);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

            // ensures status is well
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                System.out.println(url);
                ApiResponse apiResponse = GSON.fromJson(response.body(), ApiResponse.class);
                if (apiResponse.data != null && apiResponse.data.standings != null
                    && apiResponse.data.standings.length > 0) {
                    return apiResponse.data.standings[0].team.name;
                } else {
                    System.out.println("Standings array is empty.");
                }
            } else {
                System.out.println("Standings array is empty for the specified league and season.");
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Constructs wiki url for the top team.
     *
     * @return returns  wiki url for top team.
     * @param topTeamName  name of the top team
     */
    public String searchTopTeam(String topTeamName) {
        try {
            // correctly formats and builds url
            String formattedTeamName = topTeamName.toLowerCase().replace(" ", "+");
            String url = String.format
                ("%s?q=%s&limit=1", WIKIPEDIA_API_URL, URLEncoder.encode
                (topTeamName, StandardCharsets.UTF_8));
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
            HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // ensures status is well
            // if statements describe what is going wrong in command prompt
            if (response.statusCode() == 200) {
                WikipediaSearchResponse searchResponse =
                    GSON.fromJson(response.body(), WikipediaSearchResponse.class);
                if (searchResponse.pages != null && searchResponse.pages.length > 0) {
                    return "https://en.wikipedia.org/wiki/" +
                        URLEncoder.encode(searchResponse.pages[0].key, StandardCharsets.UTF_8);
                } else {
                    System.out.println("No Wikipedia page found for the specified team.");
                }
            } else {
                System.out.println("Failed to fetch data from Wikipedia API. Status code: " +
                    response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    // all these private static classes allow to parse through json using gson

    /**
     * Represents team's name.
     */
    private static class Team {
        public String name;
    }

    /**
     * Represents team's standing.
     */
    private static class Standing {
        public Team team;
    }

    /**
     * Represents team's standings data.
     */
    private static class Data {
        public Standing[] standings;
    }

    /**
     * Represents api response data.
     */
    private static class ApiResponse {
        public Data data;
    }

    /**
     * Represents wikipedia search response.
     */
    private static class WikipediaSearchResponse {
        private WikipediaPage[] pages;
    }

    /**
     * Represents the key of the wikipedia page
     * according to the format and top team.
     */
    private static class WikipediaPage {
        private String key;
    }
} // ApiApp
