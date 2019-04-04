package app;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class ASCIIGenerator {

    private Chracter[] charvalues;
    private BufferedImage imag;

    private ArrayList<ArrayList<String>> imagarr;

    int blocksize;

    public ASCIIGenerator(String[] sourcechars, String url, int blocksize) {
        this.blocksize = blocksize;
        imag = convertToGrayscale(loadImage(url));
        new JFrame(){
            private static final long serialVersionUID = 1L;

            int init = init();

            public int init(){
                setUndecorated(true);
                int wid = imag.getWidth(null);
                int hig = imag.getHeight(null);
                setSize(wid, hig);
                setLocation(0, (int) (2000-1.35*hig));
                setVisible(true);
                return 0;
            }

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.drawImage(imag, init, init, null);
            }
        };
        imagarr = new ArrayList<ArrayList<String>>(imag.getHeight()/blocksize);
        for (int i = 0; i < imag.getHeight()/blocksize; i++) 
            imagarr.add(new ArrayList<String>(imag.getWidth()/blocksize));
        charvalues = prepareValues(sourcechars);
    }

    public void convert() {
        createintarray();
        outputImage();
    }

    public void createintarray() {
        Font font = new Font("times", Font.PLAIN, 60);
        FontRenderContext frc = new FontRenderContext(null, true, true);
        int width = imag.getWidth();
        int height = imag.getHeight();
        for (int y = 0; y < height/blocksize; y++){
            String row = "";
            for (double x = 0; x < width/blocksize;) {
                double[][] britness = new double[blocksize][blocksize];
                for (int x1 = 0; x1 < britness.length; x1++) {
                    for (int y1 = 0; y1 < britness[x1].length; y1++) {
                        britness[x1][y1] = new Color(imag.getRGB((int) x*blocksize+x1,blocksize* y+y1)).getRed()/255.0;
                    }
                }
                Chracter charIndex = getClosestChar(britness);
                imagarr.get(y).add(charIndex.chars);
                row += charIndex.chars;
                x = font.getStringBounds(row, frc).getWidth()/charIndex.height;
            }
        }
    }

    public void outputImage() {
        PrintStream out = null;
        try {
            out = new PrintStream(new File("src/app/result.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (ArrayList<String> row : imagarr) {
            for (String chr : row)
                out.print(chr);
            out.print("\n");
        }
    }

    public Chracter getClosestChar(double[][] darkness) {
        double distance = dist(charvalues[0], darkness);
        int idx = 0;
        for (int c = 1; c < charvalues.length; c++) {
            double cdistance = dist(charvalues[c], darkness);
            if (cdistance < distance) {
                idx = c;
                distance = cdistance;
            }
        }
        return charvalues[idx];
    }

    public double dist(Chracter c, double[][] darkness){
        double dist = 0;
        for (int x = 0;x < darkness.length; x++)
            for (int y = 0;y < darkness[x].length; y++)
                dist += Math.abs(darkness[x][y]-c.brightness[x][y]);
        return dist;
    }

    public Chracter[] prepareValues(String[] characters) {
        Chracter[] values = new Chracter[characters.length];
        for (int i = 0; i < values.length; i++)
            values[i] = new Chracter(i, characters[i], blocksize);

        return values;
    }

    public BufferedImage loadImage(String url) {
        BufferedImage img = null;
        File f = new File(url);
        try {
            img = ImageIO.read(f);
        } catch (IOException e) {
            System.out.println(e);
        }
        return img;
    }

    public BufferedImage convertToGrayscale(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();

        BufferedImage img2 = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        // convert to grayscale
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                Color gccontrast = contrast(img, x, y, 5).color();
                img2.setRGB(x, y, gccontrast.getRGB());
            }
        DColor avgC = new DColor(0, 0, 0);
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                if((x*y)%25 == 0) avgC = avg(img, x, y, 30);
                Color c = new Color(img.getRGB(x, y));
                Color c2 = new Color(img2.getRGB(x, y));
                double r = (c.getRed()+4*c2.getRed()-avgC.getRed())*1.25+avgC.getRed();
                double g = (c.getGreen()+4*c2.getGreen()-avgC.getBlue())*1.25+avgC.getBlue();
                double b = (c.getBlue()+4*c2.getBlue()-avgC.getGreen())*1.25+avgC.getGreen();

                // calculate average
                int avg = (int) Math.max(0, Math.min(255, (r + g + b) / 3));

                // replace RGB value with avg
                Color gc = new Color(avg,avg,avg);
                img.setRGB(x, y, gc.getRGB());
            }
        return img;
    }

    public DColor contrast(BufferedImage img, int x, int y, int range){
        DColor avg = avg(img, x, y, range-1);
        double rcontrast = 0;
        double gcontrast = 0;
        double bcontrast = 0;
        int xdiff = Math.min(img.getWidth(), x+range)-Math.max(0, x-range)+1;
        int ydiff = Math.min(img.getHeight(), y+range)-Math.max(0, y-range)+1;
        for (int x1 = Math.max(0, x-range); x1 <= Math.min(img.getWidth()-1, x+range); x1++)
            for (int y1 = Math.max(0, y-range); y1 <= Math.min(img.getHeight()-1, y+range); y1++){
                DColor diff = diff(avg, new DColor(img.getRGB(x1, y1)));
                rcontrast +=  diff.getRed();
                gcontrast +=  diff.getGreen();
                bcontrast +=  diff.getBlue();
            }
        rcontrast = rcontrast / xdiff / ydiff;
        gcontrast = gcontrast / xdiff / ydiff;
        bcontrast = bcontrast / xdiff / ydiff;
        return new DColor(rcontrast, gcontrast, bcontrast) ;
    } 

    public DColor avg(BufferedImage img, int x, int y, int range){
        double ravg = 0;
        double gavg = 0;
        double bavg = 0;
        int xdiff = Math.min(img.getWidth(), x+range)-Math.max(0, x-range)+1; 
        int ydiff = Math.min(img.getHeight(), y+range)-Math.max(0, y-range)+1;
        for (int x1 = Math.max(0, x-range); x1 <= Math.min(img.getWidth()-1, x+range); x1++)
            for (int y1 = Math.max(0, y-range); y1 <= Math.min(img.getHeight()-1, y+range); y1++){
                ravg +=  new Color(img.getRGB(x1, y1)).getRed();
                gavg +=  new Color(img.getRGB(x1, y1)).getGreen();
                bavg +=  new Color(img.getRGB(x1, y1)).getBlue();
            }
        ravg = ravg / xdiff / ydiff;
        gavg = gavg / xdiff / ydiff;
        bavg = bavg / xdiff / ydiff;
        return new DColor(ravg, bavg, gavg);
    } 

    public DColor diff(DColor a, DColor b){
        return new DColor(Math.abs(a.getRed()-b.getRed()), Math.abs(a.getGreen()-b.getGreen()), Math.abs(a.getBlue()-b.getBlue()));
    }
}