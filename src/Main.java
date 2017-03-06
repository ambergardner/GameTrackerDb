import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    static HashMap<String, User> users = new HashMap<>();

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS game (String name, String genre, String platform, int releaseYear);");

        {
            System.out.println("Starting GameTracker...");
            //how to make a get route
            Spark.init();
            Spark.get("/",
                    (request, response) -> {
                        Session session = request.session();
                        String name = session.attribute("userName");
                        User user = users.get(name);

                        HashMap m = new HashMap();
                        if (user == null) {
                            return new ModelAndView(m, "login.html");
                        } else {
                            return new ModelAndView(user, "home.html");
                        }
                    },
                    new MustacheTemplateEngine()
            );
            Spark.post("/create-game", (request, response) -> {
                String gameName = request.queryParams("gameName");
                String gameGenre = request.queryParams("gameGenre");
                String gamePlatform = request.queryParams("gamePlatform");
                int gameYear = Integer.parseInt(request.queryParams("gameYear"));
                insertGame(conn, gameName, gameGenre, gamePlatform, gameYear);
                response.redirect("/");
                return "";

            });
            Spark.post("/login", (request, response) -> {
                String name = request.queryParams("loginName");
                users.putIfAbsent(name, new User(name));
                Session session = request.session();
                session.attribute("userName", name);
                response.redirect("/");
                return "";

            });
            Spark.post("/edit-game", (request, response) -> {
                String gameName = request.queryParams("gameName");
                String gameGenre = request.queryParams("gameGenre");
                String gamePlatform = request.queryParams("gamePlatform");
                int gameYear = Integer.parseInt(request.queryParams("gameYear"));
                updateGame(conn, gameName, gameGenre, gamePlatform, gameYear);
                response.redirect("/");
                return "";
            });

        }
        ;

        Spark.post("/delete-game", (request, response) -> {
            Session session = request.session();
            String name = session.attribute("userName");
            User user = users.get(name);
            String gameName = request.queryParams("gameName");
            deleteGame(conn, gameName);
            return null;
        });

        Spark.post("/logout", (request, response) -> {
            Session session = request.session();
            session.invalidate();
            response.redirect("/");
            return "";
        });

    }

    private static void insertGame(Connection conn, String name, String genre, String platform, int releaseYear) throws SQLException {
        // Write a static method insertGame and run it in the /create-game route. It should insert a new row with the user-supplied information.
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO players VALUES (NULL, ?, ?, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, genre);
        stmt.setString(3, platform);
        stmt.setInt(4, releaseYear);
        stmt.execute();
    }

    private static void deleteGame(Connection conn, String id) throws SQLException {
        // Write a static method deleteGame and run it in the /delete-game route. It should remove the correct row using id.
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM game WHERE ID = ?");
        stmt.setInt(1, Integer.parseInt(id));
        stmt.execute();

    }

    private static ArrayList<Game> selectGames(Connection conn) throws SQLException {
        // Write a static method selectGames that returns an ArrayList<Game> containing all the games in the database.
        ArrayList<Game> games = new ArrayList<>();
        Statement stmt = conn.createStatement();
        ResultSet results = stmt.executeQuery("SELECT * FROM game");
        while (results.next()) {
            int id = results.getInt("id");

        }
        return games;
    }

    private static void updateGame(Connection conn, String name, String genre, String platform, int releaseYear) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("");
        stmt.setString(1, name);
        stmt.setString(2, genre);
        stmt.setString(3, platform);
        stmt.setInt(4, releaseYear);
        stmt.execute();
    }
}
