package br.com.tmsfasdom.deteccaoplacas;

import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 40276655893 on 09/12/2015.
 */
public class PossivelPlaca {

	private Mat imgPlate;
	private Mat imgThresh;
	private RotatedRect rrLocationOfPlateInScene;
	private double aspectRatio;
	private List<Mat> charMats;
	private String plateChars;

	PossivelPlaca() {
		charMats = new ArrayList<Mat>();
	}

	public Mat getImgPlate() {
		return imgPlate;
	}
	
	public double aspectRatio() {
		return aspectRatio;
	}

	public void setImgPlate(Mat imgPlate) {
		this.imgPlate = imgPlate;
	}

	public Mat getImgThresh() {
		return imgThresh;
	}

	public void setImgThresh(Mat imgThresh) {
		this.imgThresh = imgThresh;
	}

	public RotatedRect getRrLocationOfPlateInScene() {
		return rrLocationOfPlateInScene;
	}

	public void setRrLocationOfPlateInScene(RotatedRect rrLocationOfPlateInScene) {
		this.rrLocationOfPlateInScene = rrLocationOfPlateInScene;
		this.aspectRatio = (double) (rrLocationOfPlateInScene.boundingRect().width
				/ rrLocationOfPlateInScene.boundingRect().height);
	}

	public List<Mat> getCharMats() {
		return charMats;
	}

	public void setCharMats(List<Mat> charMats) {
		this.charMats = charMats;
	}

	public String getPlateChars() {
		return plateChars;
	}

	public void setPlateChars(String plateChars) {
		this.plateChars = plateChars;
	}
}
