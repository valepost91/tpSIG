/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SIG;

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
import java.util.Scanner;
import org.postgis.Geometry;
import org.postgis.PGgeometry;

/**
 *
 * @author huong
 */
public class BasicCases {
    /*Question 9 : Écrivez un petit programme de test prenant un argument en 
     * ligne de commande, et affichant tous les noms et coordonnées géographiques
     * des points dont le nom ressemble à (au sens du LIKE SQL) l'argument. 
     */

    public static void query(Connection connection) throws SQLException {

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
        PreparedStatement stmt = connection.prepareStatement("SELECT tags->'name', ST_X(geom), ST_Y(geom) FROM nodes WHERE tags->'name' LIKE '" + input + "'");
        ResultSet res = stmt.executeQuery();
        while (res.next()) {
            System.out.println("name = " + res.getString(1) + "; X = " + res.getDouble(2) + "; Y = " + res.getDouble(3));
            /* We could use Point from http://intranet.ensimag.fr/KIOSK/Matieres/5MMSICP/SIG/postgis-api/org/postgis/Point.html 
             * ((PGgeometry) res.getObject(2)).getGeometry()); */
        }
    }
    /*Question 10 (alternative a) : L'ensemble des routes autour de Grenoble 
     * (longitudes comprises entre 5.7 et 5.8, et latitudes comprises entre 
     * 45.1 et 45.2).*/

    public static void allRoads(Connection connection, MapPanel map) throws SQLException {
        String request = "SELECT linestring FROM ways WHERE exist(tags,'highway');";
        DrawLineByRequest(connection, request, map, Color.black, Boolean.TRUE);
    }
    /*Question 10 (alternative b) : L'ensemble des bâtiments autour de Grenoble
     * (longitudes comprises entre 5.7 et 5.8, et latitudes comprises entre 45.1 
     * et 45.2).*/

    public static void allBuildings(Connection connection, MapPanel map) throws SQLException {
        String request = "SELECT linestring FROM ways WHERE exist(tags,'building');";
        DrawLineByRequest(connection, request, map, Color.black, Boolean.TRUE);
    }
    /*Question 10 (alternative c) : Les limites administratives de niveaux 
     * inférieurs à 7 (correspondant aux départements et niveaux hiérarchiques
     * supérieurs).*/

    public static void showDepartments(Connection connection, MapPanel map) throws SQLException {
        String request = "SELECT linestring FROM ways WHERE exist(tags,'admin_level') AND (tags->'admin_level'<'7');";
        DrawLineByRequest(connection, request, map, Color.black, Boolean.FALSE);
    }
    /*Question 11 (suggestion a) : Même question que la question 7 (nombre de 
     * boulangeries par quartier de Grenoble), mais en représentant graphiquement 
     * sur la carte les valeurs (utilisez des attributs graphiques tels que la couleur, ou autre).*/

    public static void showSchools(Connection connection, MapPanel map) throws SQLException {
        allRoads(connection, map);
        int i = 30;
        Color c = new Color(i, i, i);
        PreparedStatement stmt = connection.prepareStatement("SELECT id FROM quartier;");
        ResultSet res = stmt.executeQuery();
        while (res.next()) {
            String request = "SELECT linestring FROM quartier Q , ways W WHERE"
                    + " ST_Intersects(W.bbox,ST_Transform(Q.the_geom,4326))"
                    + " AND Q.id='" + res.getObject(1)
                    + "' and tags->'amenity'='school';";
            DrawLineByRequest(connection, request, map, c, Boolean.TRUE);
            i += 33;
            c = new Color(i % 255, (i + 50) % 255, (i + 80) % 255);
        }
    }
    /*Question 11 (suggestion b) : Tracer une carte des nuisances sonores (zones
     * à proximité des voies ferrées, autoroutes et aéroports). Vous pouvez 
     * vous appuyer sur la fonction ST_Buffer de PostGIS.*/

    public static void showNoise(Connection connection, MapPanel map) throws SQLException {
        double raduisSound = 0.001;
        //Noise next to railway..tram ..
        String request = "SELECT w.linestring FROM ways w WHERE exist(w.tags,'railway');";
        DrawLineByRequest(connection, request, map, Color.BLUE, Boolean.TRUE);
        request = "SELECT ST_Buffer(geometry(w.linestring)," + raduisSound + ") FROM ways w WHERE exist(w.tags,'railway') AND w.tags->'railway'='tram';";
        DrawLineByRequest(connection, request, map, Color.RED, Boolean.TRUE);
        request = "SELECT ST_Buffer(geometry(w.linestring)," + raduisSound + ") FROM ways w WHERE exist(w.tags,'railway') AND w.tags->'railway'='rail';";
        DrawLineByRequest(connection, request, map, Color.RED, Boolean.TRUE);

        allBuildings(connection, map);
        allRoads(connection, map);
    }
    /*Question 11 (suggestion c) : Affichez une carte matricielle de densité
     * sur le territoire Rhône-Alpes. Vous pouvez parcourir l'ensemble du 
     * territoire par carreau de x km de côté, et compter pour chaque carreau 
     * le nombre de bâtiments à l'intérieur du carreau.*/

    public static void showDensity(Connection connection, MapPanel map) throws SQLException {
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
        for (int x = 0; x < square_nb; x++) {
            for (int y = 0; y < square_nb; y++) {
                LineString l = new LineString();
                double x1_x = GLon_a + ((GLon_b - GLon_a) * x / square_nb);
                double x1_y = GLat_a + ((GLat_b - GLat_a) * y / square_nb);
                Point x1 = new Point(x1_x, x1_y);
                Point x2 = new Point(GLon_a + ((GLon_b - GLon_a) * (x + 1)
                        / square_nb), GLat_a + ((GLat_b - GLat_a) * y / square_nb));
                double x3_x = GLon_a + ((GLon_b - GLon_a) * (x + 1)
                        / square_nb);
                double x3_y = GLat_a + ((GLat_b - GLat_a) * (y + 1) / square_nb);
                Point x3 = new Point(x3_x, x3_y);
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
                //Count nb building by square
                //select count(*) from ways where exist(tags,'building'); + intersect
                String request = "select linestring from ways w where"
                        + " exist(tags,'building') AND ST_Intersects(w.bbox,"
                        + "ST_SetSRID(ST_MakeBox2D(ST_Point(" + Double.toString(x1_x)
                        + "," + Double.toString(x1_y) + "),ST_Point(" + Double.toString(x3_x)
                        + "," + Double.toString(x3_y) + ")),4326))";
                String request2 = "select count(*) from ways w where"
                        + " exist(tags,'building') AND ST_Intersects(w.bbox,"
                        + "ST_SetSRID(ST_MakeBox2D(ST_Point(" + Double.toString(x1_x)
                        + "," + Double.toString(x1_y) + "),ST_Point(" + Double.toString(x3_x)
                        + "," + Double.toString(x3_y) + ")),4326))";
                PreparedStatement stmt = connection.prepareStatement(request2);
                ResultSet res = stmt.executeQuery();

                while (res.next()) {
                    if (0 < res.getInt(1) && res.getInt(1) <= 50) {
                        DrawLineByRequest(connection, request, map, Color.blue, Boolean.FALSE);
                    } else if (50 < res.getInt(1) && res.getInt(1) <= 100) {
                        DrawLineByRequest(connection, request, map, Color.green, Boolean.FALSE);
                    } else if (100 < res.getInt(1) && res.getInt(1) <= 300) {
                        DrawLineByRequest(connection, request, map, Color.yellow, Boolean.FALSE);
                    } else if (300 < res.getInt(1) && res.getInt(1) <= 500) {
                        DrawLineByRequest(connection, request, map, Color.orange, Boolean.FALSE);
                    } else {//>500
                        DrawLineByRequest(connection, request, map, Color.red, Boolean.FALSE);
                    }
                }
            }

        }
    }

    private static void DrawLineByRequest(Connection connection, String request, MapPanel map, Color color, Boolean intersectGrenoble) throws SQLException {
        if (request.endsWith(";")) {
            request = request.substring(0, request.length() - 1);
        }
        String r = request;

        if (intersectGrenoble) {
            r += " AND ST_Intersects(w.bbox,ST_SetSRID(ST_MakeBox2D(ST_Point(5.7,45.1),ST_Point(5.8,45.2)),4326));";
        }else{
            r += ";";
        }
        PreparedStatement stmt = connection.prepareStatement(r);
        ResultSet res = stmt.executeQuery();

        while (res.next()) {
            Geometry geom = ((PGgeometry) res.getObject(1)).getGeometry();
            int nb = geom.numPoints();

            LineString l = new LineString(color);
            org.postgis.Point p;
            for (int i = 0; i < nb; i++) {
                p = geom.getPoint(i);
                l.addPoint(new Point(p.x, p.y));
            }
            map.addPrimitive(l);
        }

    }
}
