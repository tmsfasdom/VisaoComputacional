package br.com.tmsfasdom.deteccaoplacas;


import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

/**
 * Created by 40276655893 on 08/12/2015.
 */
public class PossivelChar {

    private MatOfPoint contour;
    private Rect boundingRect;

    private int intCenterX;
    private int intCenterY;

    private double dblDiagonalSize;
    private double dblAspectRatio;

    private int intRectArea;


    public PossivelChar(MatOfPoint mop){
        contour = mop;

        boundingRect = Imgproc.boundingRect(contour);

        intCenterX = (boundingRect.x + boundingRect.x + boundingRect.width) / 2;
        intCenterY = (boundingRect.y + boundingRect.y + boundingRect.height) / 2;

        dblDiagonalSize = Math.sqrt(Math.pow(boundingRect.width, 2) + Math.pow(boundingRect.height, 2));

        dblAspectRatio = (float)boundingRect.width / (float)boundingRect.height;

        intRectArea = boundingRect.width * boundingRect.height;


    }

    public MatOfPoint getContour() {
        return contour;
    }

    public Rect getBoundingRect() {
        return boundingRect;
    }


    public double getDblDiagonalSize() {
        return dblDiagonalSize;
    }

    public double getDblAspectRatio() {
        return dblAspectRatio;
    }

    public int getIntRectArea() {
        return intRectArea;
    }

    public int getIntCenterX() {
        return intCenterX;
    }

    public int getIntCenterY() {
        return intCenterY;
    }

    public void setIntRectArea(int intRectArea) {
        this.intRectArea = intRectArea;
    }

    public void setIntCenterY(int intCenterY) {
        this.intCenterY = intCenterY;
    }

    public void setIntCenterX(int intCenterX) {
        this.intCenterX = intCenterX;
    }
}
