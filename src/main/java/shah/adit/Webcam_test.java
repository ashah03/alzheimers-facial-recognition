package shah.adit;

import com.github.sarxos.webcam.Webcam;
import magick.*;

import javax.imageio.ImageIO;
import javax.media.jai.operator.PNMDescriptor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("unused")
public class Webcam_test {


    public static void main(String[] args) throws IOException, MagickException {

        //get default webcam and open it
        System.out.println(System.getProperty("java.library.path"));
        Webcam webcam = Webcam.getDefault();
        webcam.open();

        // get image
        BufferedImage image = webcam.getImage();

        // save image to PNG file
        ImageIO.write(image, "JPG", new File("test.jpg"));
        PNMDescriptor x = new PNMDescriptor();


        ImageInfo info = new ImageInfo("test.jpg");
        MagickImage magick_converter = new MagickImage(info); //Create MagickImage object
        String outputfile = "test.pgm"; //Output File name
        magick_converter.setFileName(outputfile); //set output file format
        magick_converter.writeImage(info); //do the conversion


    }
}
