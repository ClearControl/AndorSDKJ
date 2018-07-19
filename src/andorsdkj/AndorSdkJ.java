package andorsdkj;

import andorsdkj.bindings.AtcoreLibrary;
import andorsdkj.util.AndorSDKJUtils;

public class AndorSdkJ implements AutoCloseable
{

	private volatile boolean isInitialized;
	public AndorSdkJ() throws AndorSdkJException
	{
		super();

		isInitialized = false;

	}

	public long getNumberOfCameras() throws AndorSdkJException
	{
		return AndorSDKJUtils.getInt("Device Count");
	}

	public AndorCamera openCamera(int pCameraIndex) throws AndorSdkJException
	{
		AndorCamera lAndorCamera = new AndorCamera(pCameraIndex);
		return lAndorCamera;
	}

	@Override
	public void close() throws AndorSdkJException
	{
		AndorSDKJUtils.handleErrorWithException("Error while finalizing AndorSDK library",
																						AtcoreLibrary.AT_FinaliseLibrary());
	}
	
	public void open() throws AndorSdkJException
	{
		AndorSDKJUtils.handleErrorWithException("Error while initializing AndorSDK library",
																						AtcoreLibrary.AT_InitialiseLibrary());
	}

}
