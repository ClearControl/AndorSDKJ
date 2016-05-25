package andorsdkj.demo;

import static org.junit.Assert.*;

import org.junit.Test;

import andorsdkj.AndorCamera;
import andorsdkj.AndorSdkJ;
import andorsdkj.AndorSdkJException;

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
				AndorCamera lAndorZyla = lAndorEnvironment.openCamera(lCameraIndex);)
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

		try (
				// create an AndorSDKJ instance
				AndorSdkJ lAndorEnvironment = new AndorSdkJ();
				AndorCamera lAndorZyla = lAndorEnvironment.openCamera(lCameraIndex);)
		{
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
}
