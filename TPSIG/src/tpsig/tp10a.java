/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tpsig;

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
import org.postgis.Geometry;
import org.postgis.PGgeometry;

/**
 *
 * @author postiglv
 */
public class tp10a {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException, InterruptedException {
        MapPanel map = new MapPanel(5.75, 45.15, 1.3);
        GeoMainFrame mainFrame = new GeoMainFrame("test",map);

        // TODO code application logic here
        Connection connection;

        connection = getConnection();
        
        // TODO : try to set the geometry as a parameter
        PreparedStatement stmt = connection.prepareStatement("SELECT linestring FROM ways WHERE ST_Intersects(bbox,ST_SetSRID(ST_MakeBox2D(ST_Point(5.7,45.1),ST_Point(5.8,45.2)),4326));");
        ResultSet res = stmt.executeQuery();
        while (res.next()) {
            Geometry geom = ((PGgeometry) res.getObject(1)).getGeometry();
            int nb = geom.numPoints();
            //Point[] points = new Point[nb];
            for(int i = 0;i<nb;i++) {
                //points[i] = geom.getPoint(i);
            }
        }
    }
}