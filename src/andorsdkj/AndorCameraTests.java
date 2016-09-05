package andorsdkj;

import java.util.concurrent.TimeUnit;

import org.bridj.Pointer;
import org.junit.Test;

import andorsdkj.enums.CycleMode;
import andorsdkj.enums.TriggerMode;

public class AndorCameraTests
{
	@Test
	public void MetadataTest(){
		try
		{
			AndorSdkJ lAndorEnv = new AndorSdkJ();
			lAndorEnv.open();
			AndorCamera lAndorCamera = lAndorEnv.openCamera(0);
			lAndorCamera.collectMetadata(true);
			lAndorCamera.collectTimestamp(true);
		//	lAndorCamera.collectFrameInfo(true);
			lAndorCamera.set16PixelEncoding();
			lAndorCamera.setTriggeringMode(TriggerMode.SOFTWARE);
			lAndorCamera.setCycleMode(CycleMode.CONTINUOUS);
			
			int lImSize = lAndorCamera.getImageSizeInBytes();
			Pointer<Byte> lBuffPpointer = Pointer.allocateBytes(lImSize);
			
			ImageBuffer lImageBuffer = new ImageBuffer(lBuffPpointer, lImSize, true, true);
			lAndorCamera.enqueueBuffer(lImageBuffer);
			lAndorCamera.startAcquisition();
		//	Thread.sleep(2000);
			lAndorCamera.SoftwareTrigger();
			lAndorCamera.waitForBuffer(1, TimeUnit.SECONDS);
			System.out.println(lImageBuffer.getMetadata());
			long ts1 = lImageBuffer.getTimestamp();
			lAndorCamera.enqueueBuffer(lImageBuffer);
			
			lAndorCamera.SoftwareTrigger();
			Thread.sleep(1);
			lAndorCamera.waitForBuffer(1, TimeUnit.SECONDS);
			
			System.out.println(lImageBuffer.getMetadata());
			long ts2 = lImageBuffer.getTimestamp();
			long diff = ts2 - ts1;
			System.out.println("clock frequency is: " + lAndorCamera.getTimestampClockFrequency() + " difference between two frames is: " + diff);
			
			lAndorCamera.stopAcquisition();
			lAndorCamera.close();
			
		
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	
}

//  51539607553
//  0 0 0 12 0 0 0 1 0 0 0 0 29 -123 -31 77 0 -128 -32 28 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0