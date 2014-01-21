/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tpsig;

import static database.Utils.getConnection;
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
 * @author postiglv
 */
public class TPSIG {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        // TODO code application logic here
        Connection connection;

        connection = getConnection();

        System.out.print("Enter request: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input = null;
        try {
            input = br.readLine();
        } catch (IOException e) {
            System.out.println("IO error trying to read your input");
            System.exit(1);
        }

        PreparedStatement stmt = connection.prepareStatement("Select * from nodes where name like '%" + input + "'");
        ResultSet res = stmt.executeQuery();
        while (res.next()) {
            System.out.println("colonne 1 = " + res.getInt(1) + "; colonne 2 = " + ((PGgeometry) res.getObject(2)).getGeometry());
        }

    }
}
