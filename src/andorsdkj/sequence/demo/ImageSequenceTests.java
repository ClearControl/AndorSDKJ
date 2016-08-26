package andorsdkj.sequence.demo;

import static org.junit.Assert.*;

import org.bridj.Pointer;
import org.junit.*;

import andorsdkj.sequence.ImageSequence;

public class ImageSequenceTests {
	@Test
	public void TestImageSequenceConstruction(){
		int lHeight = 100;
		int lWidth = 100;
		int lDepth = 100;
		
		int lImageSizeInBytes = lHeight*lWidth*lDepth*8;
	
		ImageSequence lImageSequence = new ImageSequence(lImageSizeInBytes, lHeight, lWidth, lDepth);
		
		System.out.println("ImageSequence created:\n" + lImageSequence);
		
		assertTrue(lHeight == lImageSequence.getHeight());
		assertTrue(lWidth == lImageSequence.getWidth());
		assertTrue(lDepth == lImageSequence.getDepth());
		assertTrue(lImageSizeInBytes == lImageSequence.getImageSizeInBytes());
		
		Pointer<Byte> lPointer = Pointer.allocateBytes(lDepth*lImageSizeInBytes);
		lImageSequence = new ImageSequence(lImageSizeInBytes, lHeight, lWidth, lDepth, lPointer);
		
	}
}
