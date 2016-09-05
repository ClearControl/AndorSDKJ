package andorsdkj.bindings.util;
import java.util.Random;
//import java.awt.image.BufferedImage;
import java.awt.image.*;
import javax.imageio.*;
import java.lang.*;
import java.io.*;


public class SavePNG {
    
    public static void savePNG(int[][] buff, String path, String name) {
        /* --- funtion to build and save a .png from an int array --- */
        
        
        // exception handling        
        if (buff == null) {
            throw new java.lang.NullPointerException("Array is null.");
        }
        
        int height = buff.length;
        
        if (height == 0) {
            throw new java.lang.IllegalArgumentException("Array is empty.");
        }
             
        int width = buff[0].length;
        
        if (width == 0) {
            throw new java.lang.IllegalArgumentException("first string of the input array is empty.");
        }
        
        // check
        System.out.println("SavePNG: height is: " + height + " width is : " + width);
        
        // creating a buffered image
        BufferedImage im = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
        WritableRaster raster = im.getRaster();
        
        
        //function per se
        
        // create a 1d array for flattened data
       // int[] data = new int[height*width];
        
        // fill it
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                //data[i + j*height] = buff[i][j];
            	raster.setSample(j, i, 0, buff[i][j]);
            }
        }
        
        
       // im.setRGB(0, 0, width, height, data, 0, width);

        // creating the output file
        
        File out = new File(path+name);
        try {
            ImageIO.write(im, "png", out);
        } catch (IOException e) {
            System.out.println("IO exception during the writing process.");
        }
    }
    
    public static void main(String[] args) throws IOException {
        System.out.println("This example will save a byte/int array as a .png image:");
        
        Random randGen = new Random();
        //test randGen
        for (int i = 0; i < 10; i++) {
            int r = randGen.nextInt(100);
            System.out.println("Generated: " + r);
        }
        
        //generate a random array
        
        int width = 1280;
        int height = 500;
        int[][] arr = new int[height][width];
             
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
            	if (i > height/2) {
            		arr[i][j] = 70000;
            	}
            	else {
            		arr[i][j] = 0;
            	}
            }
        }
        
        String stdPath = "C:\\Users\\myersadmin\\Documents\\images\\";
        String stdName = "test.png";
        savePNG(arr, stdPath, stdName);
     
        
    }
}
