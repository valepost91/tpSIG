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
import java.awt.Color;
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
public class tp11a {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException, InterruptedException {
        MapPanel map = new MapPanel(5.75, 45.15, 0.1);
        GeoMainFrame mainFrame = new GeoMainFrame("test",map);

        // TODO code application logic here
        Connection connection;

        connection = getConnection();
        
        // TODO : try to set the geometry as a parameter
        String request = "SELECT linestring FROM quartier Q , ways W WHERE ST_Intersects(W.bbox,ST_Transform(Q.the_geom,4326)) ";
        DrawLineByRequest(request, map, Color.BLACK);
        Integer i=30;
        Color c=new Color(i,i,i);
        System.out.println("Ciao");
        PreparedStatement stmt = connection.prepareStatement("select id from quartier;");
        ResultSet res = stmt.executeQuery();
        
        while (res.next()) {
            request = "SELECT linestring FROM quartier Q , ways W WHERE ST_Intersects(W.bbox,ST_Transform(Q.the_geom,4326)) and Q.id='"+res.getObject(1)+"' and tags->'amenity'='school';";
        DrawLineByRequest(request, map, c);
        i+=30;
        c=new Color(i,(i+50)%255,(i+80)%255);
        }
        
        
        
        
    }
    
    private static void DrawLineByRequest(String request, MapPanel map, Color color) throws SQLException, InterruptedException {
		Connection connection;

        connection = getConnection();
		//restriction sur grenoble
		if(request.endsWith(";")) {
			request = request.substring(0, request.length() - 1);
		}
                if(request.contains("ways")) {
                    request = request + " AND ST_Intersects(bbox,ST_SetSRID(ST_MakeBox2D(ST_Point(5.7,45.1),ST_Point(5.8,45.2)),4326));";
                }
		System.out.println(request);
		PreparedStatement stmt = connection.prepareStatement(request);
        ResultSet res = stmt.executeQuery();
		
		while (res.next()) {
            Geometry geom = ((PGgeometry) res.getObject(1)).getGeometry();
            int nb = geom.numPoints();
			
			LineString l = new LineString();
			l.drawColor = color;
            org.postgis.Point p;
            for(int i = 0;i<nb;i++) {
                p = geom.getPoint(i);
                l.addPoint(new Point(p.x,p.y));
            }
			map.addPrimitive(l);
        }
	}
}
