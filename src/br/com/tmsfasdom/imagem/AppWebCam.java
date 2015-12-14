package br.com.tmsfasdom.imagem;

import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.Videoio;
import org.opencv.videoio.VideoCapture;

public class AppWebCam {
	
	

	private JFrame frame;
	private JLabel imageLabel;

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		AppWebCam app = new AppWebCam();
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
	VideoCapture capture = new VideoCapture();    
	capture.set(Videoio.CAP_PROP_FRAME_WIDTH,1920);    
	capture.set(Videoio.CAP_PROP_FRAME_HEIGHT,1080);
	capture.open(0);
	
	if(capture.isOpened())
	{
		while (true){
			capture.read(webcamMatImage);
			if( !webcamMatImage.empty() ){
				tempImage= new ImageProcessor().toBufferedImage(webcamMatImage);  
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
