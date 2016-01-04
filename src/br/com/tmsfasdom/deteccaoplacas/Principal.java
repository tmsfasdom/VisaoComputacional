package br.com.tmsfasdom.deteccaoplacas;


import java.awt.Image;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;


import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.Videoio;
import org.opencv.videoio.VideoCapture;

public class Principal {

	

	private JFrame frame;
	private JLabel imageLabel;

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Principal app = new Principal();
		app.initGUI();
		app.runMainLoop(args);
	}

	private void initGUI() {

		frame = new JFrame("Camera Input Example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);		
		imageLabel = new JLabel();
		frame.add(imageLabel);		
		frame.setVisible(true);
	}

private void runMainLoop(String[] args) {    
	Mat webcamMatImage = new Mat();      
	Image tempImage; 
	//VideoCapture capture = new VideoCapture("src/main/resources/videos/20160102_134305_xvid.avi");

	VideoCapture capture = new VideoCapture();    
	capture.set(Videoio.CAP_PROP_FRAME_WIDTH,1920);    
	capture.set(Videoio.CAP_PROP_FRAME_HEIGHT,1080);
	capture.open(0);
	
	if(capture.isOpened())
	{
		while (true){
			capture.read(webcamMatImage);
			if( !webcamMatImage.empty() ){
				Mat originalCopy = webcamMatImage.clone();
				Mat preProcessada = Util.preProcessamento(webcamMatImage);
				List<PossivelPlaca> listPlacas = new Util().detectPlatesInScene(webcamMatImage, preProcessada);

	            for (PossivelPlaca pl:listPlacas)
	            {
	            	Imgproc.rectangle(originalCopy, pl.getRrLocationOfPlateInScene().boundingRect().br(),pl.getRrLocationOfPlateInScene().boundingRect().tl(), new Scalar(255,0,0),2);
	            	ReconhecerChars.ReconhecerPlaca(pl);
	            	if (pl.getPlateChars()!= null)
	            	{
	            	Imgproc.putText(originalCopy, pl.getPlateChars(), pl.getRrLocationOfPlateInScene().boundingRect().tl(), 1, 2,new Scalar(255,0,0),2 );
	            	}
	            }
				
				
				
				tempImage= Util.toBufferedImage(originalCopy);  
				ImageIcon imageIcon = new ImageIcon(tempImage, "Captured video");  
				imageLabel.setIcon(imageIcon);  

				frame.pack();  //this will resize the window to fit the image
				}      
			else{
				System.out.println(" -- Frame not captured -- Break!");

				break;
				}
			}
		}
	else{
		System.out.println("Couldn't open capture.");
		}
	}
}
