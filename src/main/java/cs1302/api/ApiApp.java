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

/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class ApiApp extends Application {
    private static final String SCOREBAT_API_TOKEN =
        "MTU2MTg5XzE3MTQ3Njc3MzVfNTE4OTY5ZDRlNjljODY2ZDJjMzI2MzkyNjc5ZTVkZDdjZTBhM2JmNQ==";
    private static final String SCOREBAT_API_URL_FORMAT =
        "https://www.scorebat.com/video-api/v3/team/%s/?token=%s";
    private static final String API_FOOTBALL_URL_FORMAT =
        "https://api-football-standings.azharimm.site/leagues/";

    private final Gson gson = new Gson();

    private final HttpClient httpClient = HttpClient.newHttpClient();

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

        stage.setTitle("Top Team Highlight Finder");

        Label leagueLabel = new Label("Select a Top 5 league");
        ComboBox<String> dropDown = new ComboBox<>();
        dropDown.setValue(" ");
        dropDown.getItems().addAll(
            "Premier League (England)", "Ligue 1 (France)",
            "Serie A (Italy)", "Bundesliga (Germany)", "La Liga (Spain)");
        Label yearLabel = new Label("Enter Year");
        TextField yearField = new TextField();
        Button searchButton = new Button("Search");
        Label resultLabel = new Label();

        searchButton.setOnAction(e -> {
            String year = URLEncoder.encode(yearField.getText(), StandardCharsets.UTF_8);
            String league = dropDown.getValue();
            if (league != " ") {
                String leagueCode = getLeagueCode(league);
                if (!year.isEmpty() && leagueCode != null) {
                    String topTeam = getTopTeam(year, league);
                    if (topTeam != null) {
                        String highlightUrl = getHighlightUrl(topTeam);
                        resultLabel.setText("Top Team Highlight: " + highlightUrl);
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
            (leagueLabel, dropDown, yearLabel, yearField, searchButton, resultLabel);
        Scene scene = new Scene(layout, 500, 500);
        stage.setScene(scene);
        stage.show();
    }

    private String getLeagueCode(String league) {
        if (league  == "Premier League (English)") {
            return "eng.1";
        }
        if (league  == "Ligue 1 (France)") {
            return "fra.1";
        }
        if (league == "Serie A (Italy)") {
            return "ita.1";
        }
        if (league == "Bundesliga (Germany)") {
            return "ger.1";
        }
        if (league == "La Liga (Spain)") {
            return "spa.1";
        }
        return null;
    }
    /**
     * Retrieves the team that won the league that year.
     *
     * @return returns the top team of the league that year.
     * @param league  the league the user wants to know the winner of.
     * @param year  the year the user wants to know the winner of in the league.
     */
    private String getTopTeam(String league, String year) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format
                ("league/standings?season=year&sort=asc", league, Integer.parseInt(year))))
                .build();

            HttpResponse<String> response = httpClient.send
                (request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                FootballStandings standings = new Gson().fromJson
                    (response.body(), FootballStandings.class);
                if (standings != null && standings.getStandings() != null
                && !standings.getStandings().isEmpty()) {
                    return standings.getStandings().get(0).getTeamName();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getHighlightUrl(String teamName) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format(SCOREBAT_API_URL_FORMAT, teamName, SCOREBAT_API_TOKEN)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ScorebatTeam scorebatTeam = new Gson().fromJson(response.body(), ScorebatTeam.class);
                if (scorebatTeam != null && scorebatTeam.getVideos() != null && !scorebatTeam.getVideos().isEmpty()) {
                    return scorebatTeam.getVideos().get(0).getTitle();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static class FootballStandings {
        private java.util.List<TeamStanding> standings;

        public java.util.List<TeamStanding> getStandings() {
            return standings;
        }

        public void setStandings(java.util.List<TeamStanding> standings) {
            this.standings = standings;
        }
    }

    static class TeamStanding {
        private String teamName;

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }
    }

    static class ScorebatTeam {
        private java.util.List<Video> videos;

        public java.util.List<Video> getVideos() {
            return videos;
        }

        public void setVideos(java.util.List<Video> videos) {
            this.videos = videos;
        }
    }

    static class Video {
        private String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
} // ApiApp
