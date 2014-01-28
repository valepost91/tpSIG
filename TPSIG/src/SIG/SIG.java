/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SIG;

import database.Utils;
import java.util.Scanner;
import static database.Utils.getConnection;
import geoexplorer.gui.GeoMainFrame;
import geoexplorer.gui.MapPanel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.postgis.PGgeometry;

/**
 *
 * @author huong
 */
public class SIG {

	public static void main(String[] args) throws SQLException {
		String choice = "init";
		Scanner lectureClavier = new Scanner(System.in);
		Connection connection;
		connection = getConnection();

		MapPanel map = new MapPanel(5.75, 45.15, 0.1);
		GeoMainFrame mainFrame = new GeoMainFrame("test", map);



		System.out.println("=================");
		System.out.println("Bienvenue sur SIG");
		System.out.println("=================");
		while (!choice.equals("q")) {
			System.out.println("Menu : ");
			System.out.println("c : Show Coordonates (question 9)");
			System.out.println("r : Show Roads (question 10a)");
			System.out.println("b : Show Buildings (question 10b)");
			System.out.println("D : Show Departments (question 10c)");
			System.out.println("s : Show Scools (question 11a)");
			System.out.println("n : Show Noises (question 11b)");
			System.out.println("d : Show density (question 11c)");

			choice = lectureClavier.next();
			map.reset();
			switch (choice) {
				case "c":
					BasicCases.query(connection);
					break;
				case "r":
					BasicCases.allRoads(connection, map);
					break;
				case "b":
					BasicCases.allBuildings(connection, map);
					break;
				case "D":
					BasicCases.showDepartments(connection, map);
					break;
				case "s":
					BasicCases.showSchools(connection, map);
					break;
				case "n":
					BasicCases.showNoise(connection, map);
					break;
				case "d":
					BasicCases.showDensity(connection, map);
					break;
				default:
					break;
			}
			//Clear the map
		}
		connection.close();
	}
}
