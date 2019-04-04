package app;

import java.awt.Color;

/**
 * DColor
 */
public class DColor {
    double r, g, b;
    public DColor(double r, double g, double b){
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public DColor(int rgb){
        Color c = new Color(rgb);
        r = c.getRed();
        g = c.getGreen();
        b = c.getBlue();
    }

    public DColor invert(){
        return new DColor(255-r, 255-g, 255-b);
    }

    public Color color(){
        return new Color((int) r, (int) g, (int) b);
    }

    /**
     * @return the r
     */
    public double getRed() {
        return r;
    }

    /**
     * @return the g
     */
    public double getGreen() {
        return g;
    }

    /**
     * @return the b
     */
    public double getBlue() {
        return b;
    }
}