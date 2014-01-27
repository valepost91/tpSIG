/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SIG;

import database.Utils;
import java.util.Scanner;
import static database.Utils.getConnection;
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


        System.out.println("=================");
        System.out.println("Bienvenue sur SIG");
        System.out.println("=================");
        while (!choice.equals("q")) {
            System.out.println("Menu : ");
            System.out.println("v : Voir Coordonnée (question 9)");
            System.out.println("r : Voir Routes (question 10a)");
            System.out.println("b : Voir Batiments (question 10b)");
            System.out.println("D : Voir Départements (question 10c)");
            System.out.println("s : Voir Ecoles (question 11a)");
            System.out.println("n : Voir Nuisance sonore (question 11b)");
            System.out.println("d : Voir densité (question 11c)");

            choice = lectureClavier.next();
            switch (choice) {
                case "v":
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
            map.removeAll();
        }
        connection.close();
    }
}
