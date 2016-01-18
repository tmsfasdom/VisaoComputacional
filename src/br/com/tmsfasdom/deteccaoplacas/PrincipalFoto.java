package br.com.tmsfasdom.deteccaoplacas;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class PrincipalFoto {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat img = Imgcodecs.imread("src/main/resources/images/exemplo.jpg");
		Mat imgprocessada = Util.preProcessamento(img);
	    Imshow im = new Imshow("Title", 1024, 768);
		im.showImage(imgprocessada);
	}

}
