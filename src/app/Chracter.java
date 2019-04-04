package app;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;


     
/**
 * Chracter
 */
public class Chracter {
    int index;
    String chars;
    double[][] brightness;
    double width, height;
    
    public Chracter(int index, String chars, int blocksize){
        this.index = index;
        this.chars = chars;
        this.brightness = getBrightness(blocksize);
    }
    
    public double[][] getBrightness(int blocksize) {
        Font font = new Font("times", Font.PLAIN, 60);
        // create the FontRenderContext object which helps us to measure the text
        FontRenderContext frc = new FontRenderContext(null, true, true);

        // get the height and width of the text
        Rectangle2D bounds = font.getStringBounds(chars, frc);
        int w = (int) bounds.getWidth() + 1;
        int h = (int) bounds.getHeight() + 1;

        // create a BufferedImage object
        final BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        // calling createGraphics() to get the Graphics2D
        Graphics2D g = image.createGraphics();

        // set color and other parameters
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, w, h);
        g.setColor(Color.BLACK);
        g.setFont(font);

        g.drawString(chars, (float) bounds.getX(), (float) -bounds.getY());
        width = g.getFontMetrics().stringWidth(chars);
        height = g.getFontMetrics().getHeight();

        // releasing resources
        g.dispose();

        double[][] lighttally = new double[blocksize][blocksize];
        final int width = image.getWidth();
        final int height = image.getHeight();

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                lighttally[x*lighttally.length/width][y*lighttally[0].length/height] += new Color(image.getRGB(x, y)).getRed()/255.0;

        for (int x = 0; x < lighttally.length; x++)
            for (int y = 0; y < lighttally[0].length; y++)
                lighttally[x][y] =  lighttally[x][y]*blocksize*blocksize / (height * width);

        new JFrame(chars){
            private static final long serialVersionUID = 1L;

            int init = init();

            public int init(){
                setUndecorated(true);
                int wid = 75;
                int hig = 75;
                setSize(wid, hig);
                setLocation((wid * index)%3000, index/(3000/wid)*hig);
                // setVisible(true);
                return 0;
            }

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.drawImage(image, init, init, null);
                g.setColor(Color.MAGENTA);
                g.drawString(""+width, init, 75);
            }
        };
        return lighttally;
    }
}