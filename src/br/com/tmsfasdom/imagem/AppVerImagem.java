package br.com.tmsfasdom.imagem;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;



public class AppVerImagem {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {
		String filePath = "D:/Projetos/Java/OpenCVTest/src/main/resources/images/catedral.jpg";
		Mat newImage = Imgcodecs.imread(filePath);
		if (newImage.dataAddr() == 0) {
			System.out.println("Couldn't open file " + filePath);
		} else {
			ImageViewer imageViewer = new ImageViewer();
			imageViewer.show(newImage, "Loaded image");
		}
	}
}