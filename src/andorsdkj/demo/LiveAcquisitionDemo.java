package andorsdkj.demo;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import andorsdkj.AndorCamera;
import andorsdkj.AndorSdkJ;
import andorsdkj.AndorSdkJException;
import andorsdkj.enums.CycleMode;
import andorsdkj.enums.ReadOutRate;
import andorsdkj.live.LiveAcquisition;

public class LiveAcquisitionDemo
{
	@Test
	public void basicDemo()
	{
		boolean noErrors = true;
		int lCameraIndex = 0;
		try
		{
		//	System.out.println("Initialized the library and opening a camera... ");
			AndorSdkJ lAndorEnvironment = new AndorSdkJ();
			lAndorEnvironment.open();
			AndorCamera lAndorZyla = lAndorEnvironment.openCamera(lCameraIndex);

			System.out.println("Setting the pixel encoding... ");
			lAndorZyla.setStandardPixelEncoding();
			
			lAndorZyla.setExposureTimeInSeconds(0.1);
			lAndorZyla.setReadoutRate(ReadOutRate._100_MHz);
			lAndorZyla.setCycleMode(CycleMode.CONTINUOUS);
			lAndorZyla.allocateAndQueueBuffers(1);
						
			// TODO: @Alex: add setWidthHeight and getWidth and getHeight for camera
			
			LiveAcquisition lLiveAcquisition = new LiveAcquisition(lAndorZyla);
			
			lLiveAcquisition.addListener((s,i)->{System.out.println(i);});
			
			lLiveAcquisition.start();
			
			Thread.sleep(10000);
			
			lLiveAcquisition.stop();
	
			
			//TODO: change image resolution
//			
//			lLiveAcquisition.start();
//			
//			Thread.sleep(2000);
//			
//			lLiveAcquisition.stop();
						
			
		}
		catch (AndorSdkJException e)
		{
			System.out.println("Exception from the SDK: " + e);
			noErrors = false;
		}
		catch (Throwable e)
		{
			System.out.println("No SDK exceptions. Other exceptions: " + e);
			noErrors = false;
		}
		finally
		{
			if (noErrors)
			{
				System.out.println("Done!");
			}
			else
			{
				System.out.println("Ended with exceptions.");
			}
			assertTrue(noErrors);
		}
	}
}
