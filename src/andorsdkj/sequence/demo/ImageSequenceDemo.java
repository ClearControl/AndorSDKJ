package andorsdkj.sequence.demo;

import static andorsdkj.bindings.util.Buffer16ToArray.toArray;
import static andorsdkj.bindings.util.SavePNG.savePNG;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import andorsdkj.AndorCamera;
import andorsdkj.AndorSdkJ;
import andorsdkj.AndorSdkJException;
import andorsdkj.ImageBuffer;
import andorsdkj.enums.ReadOutRate;
import andorsdkj.sequence.ImageSequence;
import andorsdkj.sequence.SequenceAcquisition;

public class ImageSequenceDemo {
	@Test
	public void ImageSequenceTest() {
		boolean noErrors = true;
		int lCameraIndex = 0;
		try {
			AndorSdkJ lAndorEnvironment = new AndorSdkJ();
			lAndorEnvironment.open();
			AndorCamera lAndorZyla = lAndorEnvironment.openCamera(lCameraIndex);
			lAndorZyla.set16PixelEncoding();
			lAndorZyla.setExposureTimeInSeconds(0.1);
			lAndorZyla.setReadoutRate(ReadOutRate._100_MHz);

			ImageSequence lImSec = new ImageSequence(lAndorZyla.getImageSizeInBytes(), 1024, 1024, 5);
			SequenceAcquisition lSequenceAcquisition = new SequenceAcquisition(lAndorZyla, lImSec);

			lSequenceAcquisition.addListener((s, i) -> {
				System.out.println(i);

				for (int j = 0; j < i.getDepth(); j++) {
					ImageBuffer lImageBufferToProcess = new ImageBuffer(i.getImageBufferArray()[j].getPointer(), i.getImageSizeInBytes());

					int lHeight = 0;
					int lWidth = 0;
					try {
						lHeight = s.getCamera().getFrameHeight();
						lWidth = s.getCamera().getStrideInPixels(2);
						System.out.println("listener: width is: " + lWidth + " height is: " + lHeight);
					} catch (Exception e) {

						e.printStackTrace();
					}

					int[][] BufferArray = toArray(lImageBufferToProcess, lWidth, lHeight);
					savePNG(BufferArray, "C:\\Users\\myersadmin\\images\\", "seq_" + j + ".png");
				}

			});

			lSequenceAcquisition.acquireSequence(5, TimeUnit.SECONDS);

		} catch (AndorSdkJException e) {
			System.out.println("Exception from the SDK: " + e);
			e.printStackTrace();
			noErrors = false;
		} catch (Throwable e) {
			System.out.println("No SDK exceptions. Other exceptions: " + e);
			e.printStackTrace();
			noErrors = false;
		} finally {
			if (noErrors) {
				System.out.println("Done!");
			} else {
				System.out.println("Ended with exceptions.");
			}
			assertTrue(noErrors);
		}
	}
}
