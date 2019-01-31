package shah.adit;

import magick.*;

public class Convert_test {

    public static void main(String[] args) throws Exception {

        String inputfileName = "test.jpg"; //Input JPG file
        ImageInfo info = new ImageInfo(inputfileName); //Get JPG file into ImageInfo object
        MagickImage magick_converter = new MagickImage(info); //Create MagickImage object

        String outputfile = "jmagick_converted_image.pgm"; //Output File name
        magick_converter.setFileName(outputfile); //set output file format
        magick_converter.writeImage(info); //do the conversion

    }
}
