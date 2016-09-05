package andorsdkj.sequence.demo;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import andorsdkj.AndorCamera;
import andorsdkj.AndorSdkJ;
import andorsdkj.AndorSdkJException;
import andorsdkj.sequence.ImageSequence;
import andorsdkj.sequence.SequenceAcquisition;

public class AndorCameraAcquireSequenceDemo {
	@Test
	public void ActuisitionTest() {
		try {
			AndorSdkJ lAndorEnv = new AndorSdkJ();
			lAndorEnv.open();
			AndorCamera lAndorCamera = lAndorEnv.openCamera(0);

			ImageSequence lImSec = new ImageSequence(lAndorCamera.getImageSizeInBytes(), 1024, 1024, 5);

			SequenceAcquisition lSeqAcq = new SequenceAcquisition(lAndorCamera, lImSec);
			lSeqAcq.addListener((a, s) -> {
				System.out.println("Acquired sequense: " + s + "for the acquisition: " + a);
			});

			lSeqAcq.acquireSequence(5, TimeUnit.SECONDS);
		} catch (AndorSdkJException e) {
			e.printStackTrace();
		}
	}
}
