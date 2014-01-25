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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.postgis.Geometry;
import org.postgis.PGgeometry;

/**
 *
 * @author alepeel
 */
public class tp11b {
	

	public static void main(String[] args) throws SQLException, InterruptedException {
        MapPanel map = new MapPanel(5.75, 45.15, 1.3);
        GeoMainFrame mainFrame = new GeoMainFrame("test",map);

        // TODO code application logic here
        Connection connection;

        connection = getConnection();
		
		double raduisSound = 0.001;
        
        // TODO : try to set the geometry as a parameter
		String request = "SELECT w.linestring FROM ways w WHERE exist(w.tags,'railway');";
		DrawLineByRequest(request, map, Color.BLUE);
		request = "SELECT ST_Buffer(geometry(w.linestring)," + raduisSound + ") FROM ways w WHERE exist(w.tags,'railway') AND w.tags->'railway'='tram';";
		DrawLineByRequest(request, map, Color.RED);
		request = "SELECT ST_Buffer(geometry(w.linestring)," + raduisSound + ") FROM ways w WHERE exist(w.tags,'railway') AND w.tags->'railway'='rail';";
		DrawLineByRequest(request, map, Color.RED);
		
		//request = "SELECT ST_Intersects(ST_Buffer(geometry(w.linestring)," + raduisSound + "),geometry(b.linestring)) FROM ways w, ways b WHERE exist(w.tags,'railway') AND w.tags->'railway'='rail' AND exist(b.tags,'building');";
		//DrawLineByRequest(request, map, Color.RED);
		
		request = "SELECT w.linestring FROM ways w WHERE exist(w.tags,'highway');";
		DrawLineByRequest(request, map, Color.BLACK);
		request = "SELECT w.linestring FROM ways w WHERE exist(w.tags,'building');";
		DrawLineByRequest(request, map, Color.BLACK);
		
		
	}
	
	private static void DrawLineByRequest(String request, MapPanel map, Color color) throws SQLException, InterruptedException {
		Connection connection;

        connection = getConnection();
		//restriction sur grenoble
		if(request.endsWith(";")) {
			request = request.substring(0, request.length() - 1);
		}
		String r = request + " AND ST_Intersects(w.bbox,ST_SetSRID(ST_MakeBox2D(ST_Point(5.7,45.1),ST_Point(5.8,45.2)),4326));";
		System.out.println(r);
		PreparedStatement stmt = connection.prepareStatement(r);
        ResultSet res = stmt.executeQuery();
		
		while (res.next()) {
            Geometry geom = ((PGgeometry) res.getObject(1)).getGeometry();
            int nb = geom.numPoints();
			
			LineString l = new LineString(color);
            org.postgis.Point p;
            for(int i = 0;i<nb;i++) {
                p = geom.getPoint(i);
                l.addPoint(new Point(p.x,p.y));
            }
			map.addPrimitive(l);
        }
	}
}
	
