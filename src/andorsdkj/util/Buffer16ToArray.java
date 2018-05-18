package andorsdkj.util;
import org.bridj.Pointer;

import andorsdkj.ImageBuffer;



public class Buffer16ToArray
{
	public static int[][] toArray(ImageBuffer buffer, int width, int height){
		
//		System.out.println("in toArray");

		
		Pointer<Byte> auxP = buffer.getPointer();
////		
//		System.out.println("allocating auxP");
//		int size = buffer.getImageSizeInBytes();
//		System.out.println("buffer pointer is: " + buffer.getPointer());
//		Pointer<Byte> auxP = Pointer.allocateBytes(size);
////		
////		
//		System.out.println("copying");
//		//buffer.getPointer().copyTo(auxP, buffer.getImageSizeInBytes());
//		System.out.println("copied");
//		System.out.println("buffer pointer is: " + buffer.getPointer());
//		//		System.out.println("allocating the byteholder");
////		byte[] byteHolder = new byte[buffer.getImageSizeInBytes()];
////		System.out.println("getting the pointer from the imagebuffer");
////		Pointer<Byte> auxP = buffer.getPointer();
////		System.out.println("checking for the valid bytes" );
////		int validBytes = (int) auxP.getValidBytes();
////		System.out.println("# of VB:" + validBytes);
////		System.out.println("getting the bytes");
////		byteHolder = auxP.getBytes(buffer.getImageSizeInBytes());
////		System.out.println("comparing numbers");
////		if (validBytes != buffer.getImageSizeInBytes()) {
////			throw new IllegalArgumentException("byte arrays don't match!");
////		}
//		//auxP.release();
//		
//		//TODO make height and width variables and write proper checks for the correspondence of the byte array size to the height and width sepcified 
//		
		int[][] result = new int[height][width];
////		
//		
		int pos = 0;
		for (int k = 0; k < height; k++)
		{
			for (int j = 0; j < width; j++)
			{
		//		if (i%100 == 0 && j%100 == 0) {System.out.println(" i j " + i + " " + j);}
//				if (k != 0 && j == 0 && k*(height + 1) + j + 1 < buffer.getImageSizeInBytes()) {
//					pos += 24;
//					auxP = auxP.offset(24);
//				}
				
				
				int aux = 0;
				aux = (auxP.get() )& 0xFF;
				//aux <<= 8;
				result[k][j] = aux;
				//bufferPointer = bufferPointer.offset(1);
				if (k*(height + 1) + j + 1 < buffer.getImageSizeInBytes()){
					pos += 1;
					auxP = auxP.offset(1);
				}
				
				aux = ((auxP.get() & 0xFF) );
				aux <<= 8;
				result[k][j] += aux; 
				//bufferPointer = bufferPointer.offset(1);
				if (k*(height + 1) + j + 1 < buffer.getImageSizeInBytes()){
					pos += 1;
					auxP = auxP.offset(1);
				}
				
			}
		}
		//auxP.release();
		//System.out.println("done with toArray, pos is: " + pos + " imgBS is: " + size);
		return result;
		
	}
}
