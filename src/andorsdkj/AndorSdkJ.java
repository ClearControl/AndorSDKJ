package andorsdkj;

import static org.junit.Assert.assertTrue;

import org.bridj.Pointer;

import andorsdkj.bindings.AtcoreLibrary;
import andorsdkj.util.AndorSDKJUtils;

public class AndorSdkJ implements AutoCloseable
{

	public AndorSdkJ() throws AndorSdkJException
	{
		super();

		int lReturnCode = AtcoreLibrary.AT_InitialiseLibrary();
		//System.out.println(lReturnCode);
		if (lReturnCode != AtcoreLibrary.AT_SUCCESS)
		{
			throw new AndorSdkJException("Cannot initialize "
																		+ this.getClass().getName());
		}

	}

	public long getNumberOfCameras() throws AndorSdkJException
	{
		return AndorSDKJUtils.getLong("Device Count");
	}

	public AndorCamera openCamera(int pCameraIndex) throws AndorSdkJException
	{
		AndorCamera lAndorCamera = new AndorCamera(pCameraIndex);
		return lAndorCamera;
	}

	@Override
	public void close() throws Exception
	{
		AndorSDKJUtils.handleErrorWithException("Error while finalizing AndorSDK library",
																						AtcoreLibrary.AT_FinaliseLibrary());
	}

}
