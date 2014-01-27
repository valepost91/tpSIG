/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tpsig;

import static database.Utils.getConnection;
import geoexplorer.gui.GeoMainFrame;
import geoexplorer.gui.LineString;
import geoexplorer.gui.MapPanel;
import geoexplorer.gui.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import org.postgis.Geometry;
import org.postgis.PGgeometry;

/**
 *
 * @author postiglv
 */
public class tp11c {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException, InterruptedException {
        MapPanel map = new MapPanel(5.75, 45.15, 0.1);
        GeoMainFrame mainFrame = new GeoMainFrame("test", map);

        // TODO code application logic here
        Connection connection;

        connection = getConnection();
        double GLon_a = 5.7;
        double GLon_b = 5.8;
        double GLat_a = 45.1;
        double GLat_b = 45.2;

        int square_nb = 0;

        while (square_nb < 1 || square_nb > 10) {
            //TODO : treat exception if user want to enter String
            Scanner lectureClavier = new Scanner(System.in);
            System.out.println("Veuillez choisir le nombre de zones a considérer (entre 1 et 10)");
            square_nb = lectureClavier.nextInt();/* demande à l'utilisateur la saisie de son nom*/
            System.out.println("Vous avez saisi " + square_nb);
        }
        //    int nb_x = GrenobleLongitude / square_size;
        //    int nb_y = GrenobleLattitude / square_size;
        for (int x = 0; x < square_nb; x++) {
            for (int y = 0; y < square_nb; y++) {
                LineString l = new LineString();
                Point x1 = new Point(GLon_a + ((GLon_b - GLon_a) * x / square_nb),
                        GLat_a + ((GLat_b - GLat_a) * y / square_nb));
                Point x2 = new Point(GLon_a + ((GLon_b - GLon_a) * (x + 1)
                        / square_nb), GLat_a + ((GLat_b - GLat_a) * y / square_nb));
                Point x3 = new Point(GLon_a + ((GLon_b - GLon_a) * (x + 1)
                        / square_nb), GLat_a + ((GLat_b - GLat_a) * (y + 1) / square_nb));
                Point x4 = new Point(GLon_a + ((GLon_b - GLon_a) * x / square_nb),
                        GLat_a + ((GLat_b - GLat_a) * (y + 1) / square_nb));
                //line starts at x1
                l.addPoint(x1);
                //x1 to x2
                l.addPoint(x2);
                //x2 to x3
                l.addPoint(x3);
                //x3 to x4
                l.addPoint(x4);
                //x4 to x1
                l.addPoint(x1);
                map.addPrimitive(l);

            }
        }

        /*
        // TODO : try to set the geometry as a parameter
        PreparedStatement stmt = connection.prepareStatement("SELECT linestring FROM ways WHERE exist(tags,'highway') AND ST_Intersects(bbox,ST_SetSRID(ST_MakeBox2D(ST_Point(5.7,45.1),ST_Point(5.8,45.2)),4326));");
        ResultSet res = stmt.executeQuery();
        while (res.next()) {
        Geometry geom = ((PGgeometry) res.getObject(1)).getGeometry();
        int nb = geom.numPoints();
        LineString l = new LineString();
        org.postgis.Point p;
        for(int i = 0;i<nb;i++) {
        p = geom.getPoint(i);
        l.addPoint(new Point(p.x,p.y));
        }
        map.addPrimitive(l);
        }
         */
    }
}