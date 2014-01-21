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

        // TODO : prevent SQL Injection
        PreparedStatement stmt = connection.prepareStatement("SELECT tags->'name', ST_X(geom), ST_Y(geom) FROM nodes WHERE tags->'name' LIKE '"+input+"'");
        ResultSet res = stmt.executeQuery();
        while (res.next()) {
            System.out.println("name = " + res.getString(1) + "; X = "+res.getDouble(2)+"; Y = "+res.getDouble(3));
                    /* We could use Point from http://intranet.ensimag.fr/KIOSK/Matieres/5MMSICP/SIG/postgis-api/org/postgis/Point.html 
                     * ((PGgeometry) res.getObject(2)).getGeometry()); */
        }

    }
}
