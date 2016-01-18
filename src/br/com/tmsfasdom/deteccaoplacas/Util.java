package br.com.tmsfasdom.deteccaoplacas;




import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.RETR_LIST;
import static org.opencv.imgproc.Imgproc.getStructuringElement;

/**
 * Created by 40276655893 on 08/12/2015.
 */
public class Util {

    public static final String TAG = "DeteccaoDePlaca";

    public static final int MIN_PIXEL_WIDTH = 2;//2
    public static final int MIN_PIXEL_HEIGHT = 8;//8;

    public static final double MIN_ASPECT_RATIO = 0.25;//0.25
    public static final double MAX_ASPECT_RATIO = 1.0;//1
    
    public static final int MIN_PIXEL_AREA = 20;//20

    public static final double MIN_DIAG_SIZE_MULTIPLE_AWAY = 0.3;//0.3
    public static final double MAX_DIAG_SIZE_MULTIPLE_AWAY = 5.0;//5.0

    public static final double MAX_CHANGE_IN_AREA = 0.5;//0.5

    public static final double MAX_CHANGE_IN_WIDTH = 0.8;//0.8
    public static final double MAX_CHANGE_IN_HEIGHT = 0.2;//0.2

    public static final double MAX_ANGLE_BETWEEN_CHARS = 12.0;//12

    public static final int MIN_NUMBER_OF_MATCHING_CHARS = 4;//4

    public static final int ADAPTIVE_THRESH_BLOCK_SIZE = 19;//19
    public static final int ADAPTIVE_THRESH_WEIGHT = 9;//9

    public static final int MIN_CONTOUR_AREA = 60;//100

    public static final double PLATE_WIDTH_PADDING_FACTOR = 1.3;//1.3
    public static final double PLATE_HEIGHT_PADDING_FACTOR = 1.65;//1.65


    public static boolean checkIfPossibleChar(PossivelChar possivelChar) {
        return (possivelChar.getBoundingRect().area() >= MIN_CONTOUR_AREA &&
                possivelChar.getBoundingRect().width > MIN_PIXEL_WIDTH && 
		possivelChar.getBoundingRect().height > MIN_PIXEL_HEIGHT &&
                MIN_ASPECT_RATIO < possivelChar.getDblAspectRatio() 
		&& possivelChar.getDblAspectRatio() < MAX_ASPECT_RATIO &&
                possivelChar.getIntRectArea() > MIN_PIXEL_AREA);
    }

    public static double distanceBetweenChars(PossivelChar firstChar, PossivelChar secondChar) {
        int intX = Math.abs(firstChar.getIntCenterX() - secondChar.getIntCenterX());
        int intY = Math.abs(firstChar.getIntCenterY() - secondChar.getIntCenterY());

        return (Math.sqrt(Math.pow(intX, 2) + Math.pow(intY, 2)));
    }

    public static double angleBetweenChars(PossivelChar firstChar, PossivelChar secondChar) {
        double dblAdj = Math.abs(firstChar.getIntCenterX() - secondChar.getIntCenterX());
        double dblOpp = Math.abs(firstChar.getIntCenterY() - secondChar.getIntCenterY());

        double dblAngleInRad = Math.atan(dblOpp / dblAdj);

        double dblAngleInDeg = dblAngleInRad * (180.0 / 3.14159);

        return (dblAngleInDeg);
    }

    public static Mat preProcessamento(Mat image) {
        //Mat resizedMat = new Mat();
        Mat resizedMat = image.clone();
    	//Imgproc.resize(image, resizedMat, new Size(image.width() / 2, image.height() / 2));
        Imgproc.cvtColor(resizedMat, resizedMat, Imgproc.COLOR_BGRA2GRAY);
        Mat imgMaxContrastGrayScale = maximizarContraste(resizedMat);
        Mat imgBlurred = new Mat();
        Imgproc.GaussianBlur(imgMaxContrastGrayScale, imgBlurred, new Size(5, 5), 0);

        Mat imgThreshold = new Mat();
        Imgproc.adaptiveThreshold(imgBlurred, imgThreshold, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, ADAPTIVE_THRESH_BLOCK_SIZE, ADAPTIVE_THRESH_WEIGHT);

        return imgThreshold;
    }

    public static Mat maximizarContraste(Mat image) {
        Mat imgTopHat = new Mat();
        Mat imgBlackHat = new Mat();
        Mat imgGrayscalePlusTopHat = new Mat();
        Mat imgGrayscalePlusTopHatMinusBlackHat = new Mat();

        Mat structuringElement = getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(3, 3));

        Imgproc.morphologyEx(image, imgTopHat, Imgproc.MORPH_TOPHAT, structuringElement);
        Imgproc.morphologyEx(image, imgBlackHat, Imgproc.MORPH_BLACKHAT, structuringElement);

        Core.add(image, imgTopHat, imgGrayscalePlusTopHat);
        Core.subtract(imgGrayscalePlusTopHat, imgBlackHat, imgGrayscalePlusTopHatMinusBlackHat);

        return (imgGrayscalePlusTopHatMinusBlackHat);
    }
    

    public List<PossivelPlaca> detectPlatesInScene(Mat matOriginal, Mat imgMat) {
        Mat matOriginalCopy = matOriginal.clone();
        Mat matPreProcessada = imgMat;

        List<PossivelPlaca> listOfPossivelPlaca = new ArrayList<PossivelPlaca>();

        List<PossivelChar> possivelCharList = Util.findPossibleCharsInScene(matPreProcessada);
        List<List<PossivelChar>> listOfListOfMatchingCharsInScene = Util.findListOfListOfMatchingChars(possivelCharList);

        for (List<PossivelChar> listOfMatchingCharsInScene : listOfListOfMatchingCharsInScene) {
            PossivelPlaca possivelPlaca = Util.extractPlate(matOriginalCopy, listOfMatchingCharsInScene);
            if (possivelPlaca != null) {
                listOfPossivelPlaca.add(possivelPlaca);
                detectCharsInPlate(possivelPlaca);
            }
        }

        return listOfPossivelPlaca;
    }

    private void detectCharsInPlate(PossivelPlaca plate) {
        plate.setImgThresh(Util.plateProcessing(plate));
        List<PossivelChar> listOfPossivelChar = Util.findPossibleCharsInPlate(plate);
        List<List<PossivelChar>> listOfListOfMatchingChars = Util.findListOfListOfMatchingChars(listOfPossivelChar);

        //Showing results
        Mat imgContours = new Mat(plate.getImgPlate().size(), CvType.CV_8UC1, new Scalar(0.0));
        plate.setImgThresh(Util.plateProcessing(plate));
        for (List<PossivelChar> listOfMatchingChars : listOfListOfMatchingChars) {
            Util.removeInnerOverlappingChars(listOfMatchingChars);
            Collections.sort(listOfMatchingChars, new PossivelCharComparator());
            for (PossivelChar possivelChar : listOfMatchingChars) {
                Mat charMat = Util.extractCharMat(possivelChar, plate.getImgThresh());
               // Mat charResize = new Mat();
               // Imgproc.resize(charMat, charResize, new Size(charMat.width() * 4, charMat.height() * 4));
                plate.getCharMats().add(charMat);
            }
        }

        for (List<PossivelChar> listOfMatchingChars : listOfListOfMatchingChars) {
            Random random = new Random();

            int intRandomBlue = random.nextInt(256);
            int intRandomGreen = random.nextInt(256);
            int intRandomRed = random.nextInt(256);

            List<MatOfPoint> listOfContours = new ArrayList<MatOfPoint>();
            for (PossivelChar matchingChar : listOfMatchingChars) {
                listOfContours.add(matchingChar.getContour());

            }
            Imgproc.drawContours(imgContours, listOfContours, -1, new Scalar((double) intRandomBlue, (double) intRandomGreen, (double) intRandomRed));

        }
        //End showing results

   
    }


    public static List<PossivelChar> findPossibleCharsInScene(Mat imgThreshold) {
        List<PossivelChar> listOfPossivelChar = new ArrayList<PossivelChar>();

        Mat imgContours = new Mat(imgThreshold.size(), CvType.CV_8UC1, new Scalar(0.0));


        Mat imgThresholdCopy = imgThreshold.clone();

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(imgThresholdCopy, contours, imgThreshold, RETR_LIST, CHAIN_APPROX_SIMPLE);

        List<MatOfPoint> possibleCharContourList = new ArrayList<MatOfPoint>();

        for (int i = 0; i < contours.size(); i++) {
            Imgproc.drawContours(imgContours, contours, i, new Scalar(255.0));

            MatOfPoint2f contour = new MatOfPoint2f();
            MatOfPoint2f mop2f = new MatOfPoint2f();
            contours.get(i).convertTo(mop2f, CvType.CV_32FC2);
            Imgproc.approxPolyDP(mop2f, contour, Imgproc.arcLength(mop2f, true) * 0.0001, true);
            MatOfPoint mopContour = new MatOfPoint();
            contour.convertTo(mopContour, CvType.CV_32S);

            PossivelChar possivelChar = new PossivelChar(mopContour);

            if (Util.checkIfPossibleChar(possivelChar)) {
                //intCountOfValidPossibleChars++;
                listOfPossivelChar.add(possivelChar);
                possibleCharContourList.add(possivelChar.getContour());
            }
        }
        //Showing result

        Mat matPossibleChars = new Mat(imgContours.size(), CvType.CV_8UC1, new Scalar(0.0));
        Imgproc.drawContours(matPossibleChars, possibleCharContourList, -1, new Scalar(255.0));


        return listOfPossivelChar;
    }

    public static List<List<PossivelChar>> findListOfListOfMatchingChars(List<PossivelChar> possivelCharList) {
        List<List<PossivelChar>> listOfListOfMatchingChars = new ArrayList<List<PossivelChar>>();

        for (PossivelChar possivelChar : possivelCharList) {
            List<PossivelChar> listOfMatchingChars = findListOfMatchingChars(possivelChar, possivelCharList);

            listOfMatchingChars.add(possivelChar);

            if (listOfMatchingChars.size() < Util.MIN_NUMBER_OF_MATCHING_CHARS) {
                continue;
            }

            listOfListOfMatchingChars.add(listOfMatchingChars);

            List<PossivelChar> listOfPossivelCharsWithCurrentMatchesRemoved = new ArrayList<PossivelChar>();

            for (PossivelChar possChar : possivelCharList) {
                boolean isCharMatched = false;
                for (PossivelChar charMatched : listOfMatchingChars) {
                    if (charMatched == possChar) {
                        isCharMatched = true;
                    }
                }
                if (!isCharMatched) {
                    listOfPossivelCharsWithCurrentMatchesRemoved.add(possChar);
                }
            }

            List<List<PossivelChar>> recursiveListOfListOfMatchingChars;

            recursiveListOfListOfMatchingChars = findListOfListOfMatchingChars(listOfPossivelCharsWithCurrentMatchesRemoved);

            for (List<PossivelChar> recursiveListOfMatchingChars : recursiveListOfListOfMatchingChars) {
                listOfListOfMatchingChars.add(recursiveListOfMatchingChars);
            }
            break;

        }

        return listOfListOfMatchingChars;
    }


    private static List<PossivelChar> findListOfMatchingChars(PossivelChar possivelChar, List<PossivelChar> possivelCharList) {
        List<PossivelChar> listOfMatchingChars = new ArrayList<PossivelChar>();

        for (PossivelChar possibleMatchingChar : possivelCharList) {
            if (possibleMatchingChar.equals(possivelChar)) {
                continue;
            }

            double dblDistanceBetweenChars = Util.distanceBetweenChars(possivelChar, possibleMatchingChar);
            double dblAngleBetweenChars = Util.angleBetweenChars(possivelChar, possibleMatchingChar);
            double dblChangeInArea = Math.abs(possibleMatchingChar.getIntRectArea() - possivelChar.getIntRectArea()) / possivelChar.getIntRectArea();
            double dblChangeInWidth = Math.abs(possibleMatchingChar.getBoundingRect().width - possivelChar.getBoundingRect().width) / possivelChar.getBoundingRect().width;
            double dblChangeInHeight = Math.abs(possibleMatchingChar.getBoundingRect().height - possivelChar.getBoundingRect().height) / possivelChar.getBoundingRect().height;

            if (dblDistanceBetweenChars < (possivelChar.getDblDiagonalSize() * Util.MAX_DIAG_SIZE_MULTIPLE_AWAY) &&
                    dblAngleBetweenChars < Util.MAX_ANGLE_BETWEEN_CHARS &&
                    dblChangeInArea < Util.MAX_CHANGE_IN_AREA &&
                    dblChangeInWidth < Util.MAX_CHANGE_IN_WIDTH &&
                    dblChangeInHeight < Util.MAX_CHANGE_IN_HEIGHT) {
                listOfMatchingChars.add(possibleMatchingChar);
            }
        }
        return listOfMatchingChars;
    }

    public static PossivelPlaca extractPlate(Mat imgMat, List<PossivelChar> listOfMatchingChars) {
        PossivelPlaca possivelPlaca = new PossivelPlaca();


        Comparator<PossivelChar> possivelCharComparator = new PossivelCharComparator();

        Collections.sort(listOfMatchingChars, possivelCharComparator);

        PossivelChar firstChar = listOfMatchingChars.get(0);
        PossivelChar lastChar = listOfMatchingChars.get(listOfMatchingChars.size() - 1);

        firstChar.setIntCenterX(firstChar.getIntCenterX() * 2);
        firstChar.setIntCenterY(firstChar.getIntCenterY() * 2);

        lastChar.setIntCenterY(lastChar.getIntCenterY() * 2);
        lastChar.setIntCenterX(lastChar.getIntCenterX() * 2);

        double dblPlateCenterX = (double) (firstChar.getIntCenterX() + lastChar.getIntCenterX()) / 2.0;
        double dblPlateCenterY = (double) (firstChar.getIntCenterY() + lastChar.getIntCenterY()) / 2.0;

        Point pointPlateCenter = new Point(dblPlateCenterX, dblPlateCenterY);

        double totalOfCharWidths = 0;
        for (PossivelChar matchingChar : listOfMatchingChars) {
            totalOfCharWidths = totalOfCharWidths + matchingChar.getBoundingRect().width;
        }
        double averageCharWidth = (double) totalOfCharWidths / listOfMatchingChars.size();
        int plateWidth = (int) (averageCharWidth * 9 * Util.PLATE_WIDTH_PADDING_FACTOR);

        //int plateWidth = (int)((((((lastChar.getBoundingRect().x + lastChar.getBoundingRect().width - firstChar.getBoundingRect().x)))*7)/listOfMatchingChars.size()) * Util.PLATE_WIDTH_PADDING_FACTOR);

        double totalOfCharHeights = 0;

        for (PossivelChar matchingChar : listOfMatchingChars) {
            totalOfCharHeights = totalOfCharHeights + matchingChar.getBoundingRect().height;
        }

        double averageCharHeight = (double) totalOfCharHeights / listOfMatchingChars.size();

        int plateHeight = (int) (averageCharHeight * Util.PLATE_HEIGHT_PADDING_FACTOR);

        double dblOpposite = lastChar.getIntCenterY() - firstChar.getIntCenterY();
        double dblHypotenuse = Util.distanceBetweenChars(firstChar, lastChar);
        double dblCorrectionAngleInRad = Math.asin(dblOpposite / dblHypotenuse);
        double dblCorrectionAngleInDeg = dblCorrectionAngleInRad * (180.0 / Math.PI);


        possivelPlaca.setRrLocationOfPlateInScene(new RotatedRect(pointPlateCenter, new Size(plateWidth * 2, plateHeight * 2), dblCorrectionAngleInDeg));
        try {
            possivelPlaca.setImgPlate(new Mat(imgMat, possivelPlaca.getRrLocationOfPlateInScene().boundingRect()));
        } catch (Exception e) {
            System.out.println("Invalid size or position: " + e.getMessage());
            return null;
        }

        return possivelPlaca;
    }


    public static Mat plateProcessing(PossivelPlaca plate) {
        Mat imgGrayscale = new Mat();
        Imgproc.cvtColor(plate.getImgPlate(), imgGrayscale, Imgproc.COLOR_BGRA2GRAY);
        Mat imgMaxContrastGrayScale = maximizarContraste(imgGrayscale);
        Mat imgBlurred = new Mat();
        Imgproc.GaussianBlur(imgMaxContrastGrayScale, imgBlurred, new Size(5, 5), 0);

        Mat imgThreshold = new Mat();
        Imgproc.adaptiveThreshold(imgBlurred, imgThreshold, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, ADAPTIVE_THRESH_BLOCK_SIZE, ADAPTIVE_THRESH_WEIGHT);

        return imgThreshold;
    }


    public static List<PossivelChar> findPossibleCharsInPlate(PossivelPlaca plate) {
        List<PossivelChar> listOfPossivelChar = new ArrayList<PossivelChar>();
        List<MatOfPoint> listOfContours = new ArrayList<MatOfPoint>();
        Mat plateThreasholdCopy = plate.getImgThresh().clone();

        Imgproc.findContours(plateThreasholdCopy, listOfContours, plate.getImgThresh(), RETR_LIST, CHAIN_APPROX_SIMPLE);

        for (MatOfPoint contour : listOfContours) {
            PossivelChar possivelChar = new PossivelChar(contour);
            if (checkIfPossibleChar(possivelChar)) {
                listOfPossivelChar.add(possivelChar);
            }
        }
        return listOfPossivelChar;
    }

    public static List<PossivelChar> removeInnerOverlappingChars(List<PossivelChar> listOfMatchingChars) {
        List<PossivelChar> listOfInnerOverlapingCharsToRemove = new ArrayList<PossivelChar>();

        for (Iterator<PossivelChar> it = listOfMatchingChars.iterator(); it.hasNext(); ) {
            PossivelChar currentChar = it.next();
            for (Iterator<PossivelChar> it2 = listOfMatchingChars.iterator(); it2.hasNext(); ) {
                PossivelChar otherChar = it2.next();
                if (currentChar != otherChar) {
                    if (distanceBetweenChars(currentChar, otherChar) < (currentChar.getDblDiagonalSize() * MIN_DIAG_SIZE_MULTIPLE_AWAY)) {//
                        if (currentChar.getBoundingRect().area() < otherChar.getBoundingRect().area()) {
                            if (listOfInnerOverlapingCharsToRemove.indexOf(currentChar) < 0) {
                                listOfInnerOverlapingCharsToRemove.add(currentChar);
                            }
                        } else if (listOfInnerOverlapingCharsToRemove.indexOf(otherChar) < 0) {
                            listOfInnerOverlapingCharsToRemove.add(otherChar);
                        }
                    }
                }
            }
        }
        listOfMatchingChars.removeAll(listOfInnerOverlapingCharsToRemove);
        return listOfMatchingChars;
    }

    public static Mat extractCharMat(PossivelChar possivelChar, Mat plateThreshold) {
        return new Mat(plateThreshold, possivelChar.getBoundingRect());
    }


    public void plateProcessingList(List<PossivelPlaca> listOfPossiblePlates) {
        for (PossivelPlaca placa : listOfPossiblePlates) {
            placa.setImgThresh(plateProcessing(placa));
        }
    }
    
    public static BufferedImage toBufferedImage(Mat matrix) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (matrix.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
		byte[] buffer = new byte[bufferSize];
		matrix.get(0, 0, buffer); // get all the pixels
		BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
		return image;
	}
    
    
    public static void displayImage(Mat img)
    {   
    	BufferedImage img2 = toBufferedImage(img);
        ImageIcon icon=new ImageIcon(img2);
        JScrollPane scroll = new JScrollPane();
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setSize(640, 480);
        scroll.setBounds(50, 50, 600, 400);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        //scroll.setViewportBorder(BorderFactory.createLoweredBevelBorder());
        //scroll.setAutoscrolls(true);
        scroll.setVisible(true);
        ///scroll.setViewportView(panel); 
        
        JFrame frame=new JFrame();
        frame.setLayout(new FlowLayout());        
        frame.setSize(640, 480); 
        
        JLabel lbl=new JLabel();
        lbl.setIcon(icon);
        //panel.add(lbl);
        scroll.setViewportView(lbl);
        frame.add(scroll);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
  
}