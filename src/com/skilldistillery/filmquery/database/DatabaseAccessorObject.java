package com.skilldistillery.filmquery.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class DatabaseAccessorObject implements DatabaseAccessor {
	private static final String URL = "jdbc:mysql://localhost:3306/sdvid?useSSL=false&useLegacyDatetimeCode=false&serverTimezone=US/Mountain";
	private static final String user = "student";
	private static final String pass = "student";

	String sqltxt = "SELECT * FROM staff WHERE store_id = ?  AND last_name = ?";

	public List<Film> findFilmsByKeyword(String keyword) throws SQLException {
		List<Film> films = new ArrayList<>();
		Film film = null;
		Connection conn = DriverManager.getConnection(URL, user, pass);
		String sql = "SELECT * FROM film WHERE description LIKE ? or title LIKE ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, "%" + keyword + "%");
		stmt.setString(2, "%" + keyword + "%");
		ResultSet filmResult = stmt.executeQuery();

		while (filmResult.next()) {
			film = new Film();
			film.setId(filmResult.getInt("id"));
			film.setTitle(filmResult.getString("title"));
			film.setLanguage(getLanguageByLanguageId(filmResult.getInt("language_id")));
			film.setDescription(filmResult.getString("description"));
			film.setReleaseYear(filmResult.getInt("release_year"));
			film.setRentalDuration(filmResult.getInt("rental_duration"));
			film.setRentalRate(filmResult.getInt("rental_rate"));
			film.setLength(filmResult.getInt("length"));
			film.setReplacementCost(filmResult.getDouble("replacement_cost"));
			film.setRating(filmResult.getString("rating"));
			film.setSpecialFeatures(filmResult.getString("special_features"));
			List<Actor> actors = findActorsByFilmId(filmResult.getInt("id"));
			film.setActors(actors);
			films.add(film);
		}
		filmResult.close();
		stmt.close();
		conn.close();

		return films;
	}

	@Override
	public Film findFilmById(int filmId) throws SQLException {
		Film film = null;
		Connection conn = DriverManager.getConnection(URL, user, pass);
		String sql = "SELECT * FROM film WHERE id = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, filmId);
		ResultSet filmResult = stmt.executeQuery();

		if (filmResult.next()) {
			film = new Film();
			film.setId(filmResult.getInt("id"));
			film.setTitle(filmResult.getString("title"));
			film.setLanguage(getLanguageByLanguageId(filmResult.getInt("language_id")));
			film.setDescription(filmResult.getString("description"));
			film.setReleaseYear(filmResult.getInt("release_year"));
			film.setRentalDuration(filmResult.getInt("rental_duration"));
			film.setRentalRate(filmResult.getInt("rental_rate"));
			film.setLength(filmResult.getInt("length"));
			film.setReplacementCost(filmResult.getDouble("replacement_cost"));
			film.setRating(filmResult.getString("rating"));
			film.setSpecialFeatures(filmResult.getString("special_features"));
			List<Actor> actors = findActorsByFilmId(filmId);
			film.setActors(actors);
		}

		filmResult.close();
		stmt.close();
		conn.close();
		return film;
	}

	{
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Actor findActorById(int actorId) throws SQLException {
		Actor actor = null;
		Connection conn = DriverManager.getConnection(URL, user, pass);
		String sql = "SELECT * FROM actor WHERE id = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, actorId);
//		System.out.println(stmt);    
		ResultSet actorResult = stmt.executeQuery();

		if (actorResult == null) {

		} else if (actorResult.next()) {
			actor = new Actor(); // Create the object
			// Here is our mapping of query columns to our object fields:
			actor.setId(actorResult.getInt("id"));
			actor.setFirstName(actorResult.getString("first_name"));
			actor.setLastName(actorResult.getString("last_name"));
		}
		actor.setFilms(findFilmsByActorId(actorId)); // An Actor has Films
		actorResult.close();
		stmt.close();
		conn.close();

		return actor;

//		
	}

	public List<Actor> findActorsByFilmId(int filmId) {
		List<Actor> actors = new ArrayList<>();

		try {
			Connection conn = DriverManager.getConnection(URL, user, pass);
			String sql = "SELECT * FROM actor a JOIN film_actor ON a.id = film_actor.actor_id "
					+ " WHERE film_actor.film_id = ?";

			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, filmId);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int actorId = rs.getInt("id");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				Actor actor = new Actor(actorId, firstName, lastName);
				actors.add(actor);
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return actors;
	}

	public List<Film> findFilmsByActorId(int actorId) {
		List<Film> films = new ArrayList<>();

		try {
			Connection conn = DriverManager.getConnection(URL, user, pass);
			String sql = "SELECT * FROM film JOIN film_actor ON film.id = film_actor.film_id " + " WHERE actor_id = ?";

			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, actorId);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int filmId = rs.getInt("id");
				String title = rs.getString("title");
				String desc = rs.getString("description");
				int releaseYear = rs.getInt("release_year");
				int langId = rs.getInt("language_id");
				int rentDur = rs.getInt("rental_duration");
				double rate = rs.getDouble("rental_rate");
				int length = rs.getInt("length");
				double repCost = rs.getDouble("replacement_cost");
				String rating = rs.getString("rating");
				String features = rs.getString("special_features");
				List<Actor> actors = findActorsByFilmId(filmId);
				String language = getLanguageByLanguageId(langId);

				Film film = new Film(filmId, title, language, desc, releaseYear, langId, rentDur, rate, length, repCost,
						rating, features, actors);
				films.add(film);
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return films;
	}

	public String getLanguageByLanguageId(int languageId) {
		String language = null;
		try {
			Connection conn = DriverManager.getConnection(URL, user, pass);
			String sql = "SELECT name  From language l JOIN film f ON l.id = f.language_id Where f.language_id = ?";

			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, languageId);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				language = rs.getString("name");
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return language;

	}
}
