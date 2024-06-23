package pointDraw;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class HeatMap extends JPanel {
    private static HeatMap panel;
    private static int w = 800;
    private static int h = 600;
    private static int camX = 0;
    private static JTextField textX;
    private static int camY = 0;
    private static JTextField textY;
    private Point[] points;
    private static Cone[] allCones;
    private static ColorId[] colorIds;

    private static class Point {
        int x, y;
        float z;
        ColorId id;

        Point(int x, int y, float z, ColorId id) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.id = id;
        }
    }

    public HeatMap(){
        readCones();
        generatePoints();
    }

    private static void readCones(){
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir") + "\\Files");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
        fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
            Scanner myReader = new Scanner(selectedFile);
            int i = 0;
            int b = 0;
            while (myReader.hasNextLine()) {
                if (myReader.nextLine().equals("/")){
                    try (Scanner myReader2 = new Scanner(selectedFile)) {
                        colorIds = new ColorId[i];
                        for (b = 0 ; b < i; b++){
                            colorIds[b] = colorIdFromString(myReader2.nextLine());
                        }
                    }
                }
                i++;
            }
            myReader.close();
            myReader = new Scanner(selectedFile);
            for (; b >= 0; b--){myReader.nextLine(); i--;}
            allCones = new Cone[i];
            for (; i > 0; i--){
                
                allCones[i-1] = coneFromString(myReader.nextLine()); 
            }
            myReader.close();
            } catch (FileNotFoundException e) {
                
                System.out.println("An error occurred.");
            }
            
        }
    }

    private static ColorId getColorId(int id){
        if (id == 0) {return new ColorId(0, 0);}
        for (ColorId c : colorIds) {
            if (c.id == id){return c;}
        }
        return new ColorId(-1, 0);
    }

    private static ColorId colorIdFromString(String s){
        String[] sr = s.split("/");//id/h/comment
        return new ColorId(Integer.parseInt(sr[0]), Float.parseFloat(sr[1]));
    }

    private static Cone coneFromString(String s){
        String[] sr = s.split("/");//x/y/bot/tip/z/id/commnet
        ColorId ci = getColorId(Integer.parseInt(sr[5]));
        return (new Cone(Integer.parseInt(sr[0]), Integer.parseInt(sr[1]), Integer.parseInt(sr[2]), Integer.parseInt(sr[3]), Float.parseFloat(sr[4]), ci));
    }

    private void generateCone(Cone c){
        int nx = c.x - camX + (w/2);
        int ny = -c.y + camY + (h/2);
        for (int i = -c.baseR; i <= c.baseR ;i++){
            if (nx + i < 0 || nx + i >= w){continue;}
            int ly = 0;
            while (true) {
                if (ly*ly + i*i > c.baseR * c.baseR){break;}
                if (ny + ly < 0 || ny + ly >= h){ly++;continue;}
                float newZ;
                if (ly*ly + i*i < c.tipR * c.tipR){
                        newZ = c.z;
                    }else{
                        newZ =  c.z*(1-((float)(Math.sqrt(ly*ly + i*i)-c.tipR))/(c.baseR - c.tipR));
                    }
                if (points[(ny + ly)*w + nx + i].z < newZ){
                    points[(ny + ly)*w + nx + i] = new Point(nx + i, (ny + ly), newZ, c.id);
                }
                ly++;
            }
            for (ly = -ly; ly <  0; ly++){
                if (ny + ly < 0 || ny + ly >= h){continue;}
                float newZ;
                if (ly*ly + i*i < c.tipR * c.tipR){
                        newZ = c.z;
                    }else{
                        newZ =  c.z*(1-((float)(Math.sqrt(ly*ly + i*i)-c.tipR))/(c.baseR - c.tipR));
                    }
                if (points[(ny + ly)*w + nx + i].z < newZ){
                    points[(ny + ly)*w + nx + i] = new Point(nx + i, (ny + ly), newZ, c.id);
                }
            }
        }
   }

   private void generatePoints() {
        points = new Point[w * h];
        for (int i = 0; i < h; i++){
            for (int j = 0; j < w; j++){
                points[i*w + j] = new Point(j, i, 0f, new ColorId(-1, 0));
            }
        }

        for (Cone c : GetVisibleCones()) {
            generateCone(c);
        }
    }

    private Cone[] GetVisibleCones(){
        ArrayList<Cone> visibleCones = new ArrayList<Cone>();
        for (Cone c : allCones) {
            int nx = c.x - camX + (w/2);
            int ny = -c.y + camY + (h/2);
            double a = Math.atan2(ny, nx);
            int fx = nx + (int)Math.round(Math.cos(a) * c.baseR);
            int fy = ny + (int)Math.round(Math.sin(a) * c.baseR);
            if (fy > 0 && fy < h && fx > 0 && fx < w){visibleCones.add(c);}
        }
        return visibleCones.toArray(Cone[]::new);
    }

    private Color getCotrastToPoint(Point p){
        if (p.id.h >= 0.5f){return Color.getHSBColor(p.id.h - 0.5f, 1f, p.z);}
        //else
        return Color.getHSBColor(p.id.h + 0.5f, 1f, 1f);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        panel.generatePoints();

        for (Point p : points) {
            switch (p.id.id) {
                case -1 -> g.setColor(Color.getHSBColor(0f, 0f, 0f));
                case 0 -> g.setColor(Color.getHSBColor(0f, 0f, p.z));
                default -> g.setColor(Color.getHSBColor(p.id.h, 1f, p.z));
            }
            g.drawLine(p.x, p.y, p.x, p.y);
        }

        g.setColor(getCotrastToPoint(points[(h/2 *w) + w/2]));
        g.drawLine(w/2+2, h/2, w/2-2, h/2);
        g.drawLine(w/2, h/2+2, w/2, h/2-2);
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("HeatMap");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(w, h);

        panel = new HeatMap();
        panel.setLayout(null);  
        
        frame.add(panel);

        textX = new MyTextField("X position");
        textX.setBounds(90, 5, 80, 30);
        panel.add(textX);

        textY = new MyTextField("Y position");
        textY.setBounds(175, 5, 80, 30);
        panel.add(textY);  

        JButton importButton = new JButton("import");
        importButton.setFocusable(false);
        importButton.setBounds(5, 5, 80, 30);    
        panel.add(importButton);

        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                camX = 0;
                camY = 0;
                readCones();
                panel.repaint();
            }
        });

        JButton moveButton = new JButton("move");
        moveButton.setBounds(260, 5, 80, 30);    
        panel.add(moveButton);

        moveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                camX = Integer.parseInt(textX.getText());
                camY = Integer.parseInt(textY.getText());
                panel.repaint();
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                //Component c = (Component)evt.getSource();
                h = frame.getHeight();
                w = frame.getWidth();
            }
        });
    }
}