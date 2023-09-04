package com.skilldistillery.filmquery.app;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.skilldistillery.filmquery.database.DatabaseAccessor;
import com.skilldistillery.filmquery.database.DatabaseAccessorObject;
import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class FilmQueryApp {

	DatabaseAccessor db = new DatabaseAccessorObject();

	public static void main(String[] args) throws SQLException {
		FilmQueryApp app = new FilmQueryApp();

//		app.test();
		app.launch();
	}

//	private void test() throws SQLException {
//
//		List<Film> films = db.findFilmsByActorId(1);
//		Actor actor = db.findActorById(5);
//		System.out.println(films);
//		System.out.println(actor);
//	}

	private void launch() throws SQLException {
		Scanner kb = new Scanner(System.in);

		menuUserSelection(kb);
		kb.close();
	}

	public void menuUserSelection(Scanner input) throws SQLException {
		boolean keepGoing = true;
		while (keepGoing) {
			printMenu(input);
			System.out.println("Please make a selection");
			int numChoice = input.nextInt();

			switch (numChoice) {
			case 1:
				System.out.println("Please enter film ID");
				int filmId = input.nextInt();
				if (db.findFilmById(filmId) != null) {
					System.out.println(db.findFilmById(filmId));
				} else {
					System.out.println("You have entered an unused ID, please check yourself!");
				}
				break;
			case 2:
				input.nextLine();
				System.out.println("Please enter keyword");
				String keyword = input.nextLine();
				if (db.findFilmsByKeyword(keyword).size() > 0) {
					System.out.println(db.findFilmsByKeyword(keyword));
				} else {
					System.out.println("There are no films containing that keyword, stop wasting my time");
				}
				break;
			case 3:
				keepGoing = false;
				System.out.println("Have a great day!");
				break;
			default:
				System.out.println("Invalid selection");
			}
		}

	}

	public void printMenu(Scanner input) {
		System.out.println();
		System.out.println("What would you like to do?");
		System.out.println();
		System.out.println("================= Menu =================");
		System.out.println("|                                      |");
		System.out.println("|1. Look up a film by its id           |");
		System.out.println("|2. Look up a film by a search keyword |");
		System.out.println("|3. Exit the application               |");
		System.out.println("|                                      |");
		System.out.println("========================================");
	}

}
