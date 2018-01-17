package andorsdkj.demo;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import andorsdkj.AndorCamera;
import andorsdkj.AndorSdkJ;
import andorsdkj.AndorSdkJException;
import andorsdkj.enums.CycleMode;
import andorsdkj.enums.ReadOutRate;
import andorsdkj.enums.TriggerMode;
import andorsdkj.ImageBuffer;

import static andorsdkj.bindings.util.Buffer16ToArray.toArray;
import static andorsdkj.bindings.util.SavePNG.savePNG;

public class AndorCameraDemo
{

	@Test
	public void OpenAndClose()
	{
		/*--- Simple test to initialize the library, start a camera, stop the camera, close the library ---*/

		// select index 0 (1 and 2 are reserved by simcams)
		int lCameraIndex = 0;
		boolean noErrors = true;

		try (
				// create an AndorSDKJ instance
				AndorSdkJ lAndorEnvironment = new AndorSdkJ();
				AndorCamera lAndorZyla = lAndorEnvironment.openCamera(lCameraIndex);
 )
		{
			System.out.println("Initializing the library and opening a camera... ");
		}
		catch (AndorSdkJException e)
		{
			System.out.println("Exception from the SDK: " + e);
			noErrors = false;
		}
		catch (Exception e)
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

	@Test
	public void SetPixelEncoding()
	{
		int lCameraIndex = 0;
		boolean noErrors = true;

		try
		{
			AndorSdkJ lAndorEnvironment = new AndorSdkJ();
			lAndorEnvironment.open();
			AndorCamera lAndorZyla = lAndorEnvironment.openCamera(lCameraIndex);
			System.out.println("Initialized the library and opening a camera... ");

			System.out.println("Setting the pixel ecoding... ");
			lAndorZyla.setStandardPixelEncoding();
		}
		catch (AndorSdkJException e)
		{
			System.out.println("Exception from the SDK: " + e);
			noErrors = false;
		}
		catch (Exception e)
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
	
	//TODO implement more tests to solve the problem of crashing while acquiring a sequence
	
	@Test
	public void testContinuousMode()
	{
		int lCameraIndex = 0;
		boolean noErrors = true;

		try
		{
			//System.out.println("Initialized the library and opening a camera... ");

            AndorSdkJ lAndorEnvironment = new AndorSdkJ();
            lAndorEnvironment.open();
            AndorCamera lAndorZyla = lAndorEnvironment.openCamera(lCameraIndex);
			//System.out.println("Setting the pixel ecoding... ");
			// setting the camera parameters
			lAndorZyla.set16PixelEncoding();
			lAndorZyla.setCycleMode(CycleMode.CONTINUOUS);
			lAndorZyla.setTriggeringMode(TriggerMode.SOFTWARE);
			lAndorZyla.setExposureTimeInSeconds(0.03);
			lAndorZyla.setReadoutRate(ReadOutRate._100_MHz);
			
			// queueing buffers
			int lNumberOfBuffers = 1;
			lAndorZyla.allocateAndQueueAlignedBuffers(lNumberOfBuffers);
			
		// trying to convert buffer1 to an array
			int height = 2048;
			int width = 2048;
			
			// Triggering and acquiring buffers
			ImageBuffer[] buffers = new ImageBuffer[lNumberOfBuffers];
			int[][] arrayToHoldABuffer = new int[height][width];
			//acq begins
			lAndorZyla.startAcquisition();
			
			for (int i = 0; i < 2; i++)
			{
				lAndorZyla.SoftwareTrigger();
				buffers[0] = lAndorZyla.waitForBuffer(5, TimeUnit.SECONDS);
				System.out.println("about to enter toArray");
				arrayToHoldABuffer = toArray(buffers[0], 2048, 2048);
				lAndorZyla.enqueueBuffer(buffers[0]);
			}
			
			// doing sth with the buffers
			System.out.println("the first 3 bytes in the 1st buffer are: " + buffers[0].getPointer().getByte() + " " + buffers[0].getPointer().getByteAtOffset(1) + " " +buffers[0].getPointer().getByteAtOffset(2));
			
			
			
			arrayToHoldABuffer = toArray(buffers[0], 2048+12, 2048);
//			byte[] dataRavel = new byte[height*width];
//			dataRavel =  buffers[1].getPointer().getBytes(2*width*height);
//			
//			for (int i = 0; i < height; i++)
//			{
//				for (int j = 0; j < width; j++)
//				{
//					arrayToHoldABuffer[i][j] = dataRavel[i*height + j];
//				}
//			}
			String path = "C:\\Users\\myersadmin\\images\\";
			String name = "arrayTest.png"; 					
			savePNG(arrayToHoldABuffer, path, name);
			
			
			
			
			//acq ends
			lAndorZyla.stopAcquisition();
		}
		catch (AndorSdkJException e)
		{
			System.out.println("Exception from the SDK: " + e);
			e.printStackTrace();
			noErrors = false;
		}
		catch (Exception e)
		{
			System.out.println("No SDK exceptions. Other exceptions: " + e);
			noErrors = false;
			e.printStackTrace();
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
