package andorsdkj.sequence.demo;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import andorsdkj.AndorCamera;
import andorsdkj.AndorSdkJ;
import andorsdkj.AndorSdkJException;
import andorsdkj.enums.CycleMode;
import andorsdkj.sequence.ImageSequence;
import andorsdkj.sequence.SequenceAcquisition;

public class SequenceAcquisitionTests {
	@Test
	public void ActuisitionTest() {
		try {
			AndorSdkJ lAndorEnv = new AndorSdkJ();
			lAndorEnv.open();
			AndorCamera lAndorCamera = lAndorEnv.openCamera(0);
			lAndorCamera.set16PixelEncoding();
			lAndorCamera.setCycleMode(CycleMode.CONTINUOUS);
			lAndorCamera.setExposureTimeInSeconds(0.05);

			int lStackDepth = 10;
			//lAndorCamera.setFrameCount(lStackDepth);

			ImageSequence lImSec = new ImageSequence(lAndorCamera.getImageSizeInBytes(), 1024, 1024, lStackDepth);

			SequenceAcquisition lSeqAcq = new SequenceAcquisition(lAndorCamera, lImSec);
			lSeqAcq.addListener((a, s) -> {
				System.out.println("Acquired sequense: " + s + "for the acquisition: " + a);
			});

			lAndorCamera.startAcquisition();
			lSeqAcq.acquireSequence(5, TimeUnit.SECONDS);
			System.out.println("waiting before the second acq.........");
			Thread.sleep(2000);
			
			lSeqAcq.acquireSequence(5, TimeUnit.SECONDS);
			lAndorCamera.stopAcquisition();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
