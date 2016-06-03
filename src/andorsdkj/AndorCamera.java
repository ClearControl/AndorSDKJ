package andorsdkj;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.bridj.Pointer;

import andorsdkj.bindings.AtcoreLibrary;
import andorsdkj.enums.CycleMode;
import andorsdkj.enums.ReadOutRate;
import andorsdkj.enums.TriggerMode;
import andorsdkj.util.AndorSDKJUtils;

public class AndorCamera implements AutoCloseable
{
	private int mCameraIndex;
	private Pointer<Integer> mCameraHandlePointer;
	public static boolean mDebugMessages = true;

	AndorCamera(int pCameraIndex) throws AndorSdkJException
	{
		if (mDebugMessages)
			System.out.println("AndorCamera: Opening an Andor camera with index: " + pCameraIndex);
		
		mCameraIndex = pCameraIndex;
		mCameraHandlePointer = Pointer.allocateInt();

		AndorSDKJUtils.handleErrorWithException("Cannot open camera for index "
																						+ pCameraIndex,
																						AtcoreLibrary.AT_Open(pCameraIndex,
																																	mCameraHandlePointer));
	}

	@Override
	public void close() throws Exception
	{
		
		if (mDebugMessages)
			System.out.println("AndorCamera: Closing the Andor camera with index: " + this.mCameraIndex);
		
		
		AndorSDKJUtils.handleErrorWithException("Error while closing camera "
																						+ mCameraIndex,
																						AtcoreLibrary.AT_Close(mCameraHandlePointer.getInt()));
	}

	public void setStandardPixelEncoding() throws AndorSdkJException
	{
		if (mDebugMessages)
			System.out.println("AndorCamera: setting standard pixel encoding for camera with index " + this.mCameraIndex);
		
		AndorSDKJUtils.setKeyValue(	this,
																"SimplePreAmpGainControl",
																"12-bit (high well capacity)");
		AndorSDKJUtils.setKeyValue(this, "PixelEncoding", "Mono12Packed");
	}

	public void set16PixelEncoding() throws AndorSdkJException
	{
		if (mDebugMessages)
			System.out.println("AndorCamera: setting 16 bit pixel encoding for camera with index " + this.mCameraIndex);
		
		AndorSDKJUtils.setKeyValue(	this,
																"SimplePreAmpGainControl",
																"16-bit (low noise & high well capacity)");
		AndorSDKJUtils.setKeyValue(this, "PixelEncoding", "Mono16");
	}
	
	public void setTriggeringMode(TriggerMode pAndorTriggerMode) throws AndorSdkJException
	{
		if (mDebugMessages)
			System.out.println("AndorCamera: setting triggering mode to: " + pAndorTriggerMode.name() +" for the camera with index: " + this.mCameraIndex);
		
		AndorSDKJUtils.setKeyValue(	this,
																"TriggerMode",
																pAndorTriggerMode.name());
	}

	public void setCycleMode(CycleMode pAndorCycleMode) throws AndorSdkJException
	{
		if (mDebugMessages)
			System.out.println("AndorCamera: setting the cycling mode to: " + pAndorCycleMode.name() +" for the camera with index " + this.mCameraIndex);
		
		AndorSDKJUtils.setKeyValue(	this,
																"CycleMode",
																pAndorCycleMode.name());
	}

	public void setExposureTimeInSeconds(double pExposureTimeSeconds) throws AndorSdkJException
	{
		if (mDebugMessages)
			System.out.println("AndorCamera: setting the exposure time to: " + pExposureTimeSeconds + " for the camera with index" + this.mCameraIndex);
		
		AndorSDKJUtils.setFloat(this,
														"ExposureTime",
														(float) pExposureTimeSeconds);
	}

	public void setReadoutRate(ReadOutRate pReadOutRate) throws AndorSdkJException
	{
		if (mDebugMessages)
			System.out.println("AndorCamera: setting readout rate to: " + pReadOutRate + " for the camera with index " + this.mCameraIndex);
		
		AndorSDKJUtils.setKeyValue(	this,
																"PixelReadoutRate",
																pReadOutRate.name()
																						.replace('_', ' ')
																						.trim());
	}

	public int getImageSizeInBytes() throws AndorSdkJException
	{
		if (mDebugMessages)
			System.out.print("AndorCamera: getting image size for the camera with index " + this.mCameraIndex);
		
		int lImageSizeInBytes = AndorSDKJUtils.getInt(this,
																									"ImageSizeBytes");
		if (mDebugMessages)
			System.out.println(" Image size in bytes is: " + lImageSizeInBytes);
		
		return lImageSizeInBytes;
	}

	public void startAcquisition() throws AndorSdkJException
	{
		if (mDebugMessages)
			System.out.println("AndorCamera: starting acquisition for the camera with index: " + this.mCameraIndex);
		
		AndorSDKJUtils.setCommand(this, "AcquisitionStart");
	}

	public void stopAcquisition() throws AndorSdkJException
	{
		if (mDebugMessages)
			System.out.println("AndorCamera: stopping the acquisition for the camera with index: " + this.mCameraIndex);
		
		AndorSDKJUtils.setCommand(this, "AcquisitionStop");
	}

	public void allocateAndQueueBuffers(int pNumberOfBuffers) throws AndorSdkJException
	{
		if (mDebugMessages)
			System.out.println("AndorCamera: allocating and queueing _" + pNumberOfBuffers + "_ of buffers for the camera with index: " + this.mCameraIndex);
		
		AndorSDKJUtils.allocateAndQueueBuffers(	this,
																						getImageSizeInBytes(),
																						pNumberOfBuffers);
	}

	public void enqueueBuffer(ImageBuffer pImageBuffer) throws AndorSdkJException
	{
		
		if (mDebugMessages)
			System.out.println("AndorCamera: enqueueing a buffer for the camera with index: " + this.mCameraIndex);
		
		Pointer<Byte> lPointer = pImageBuffer.getPointer();
		int lImageSize = pImageBuffer.getImageSizeInBytes();
		AndorSDKJUtils.queueBuffer(this, lPointer, lImageSize);
	}

	public ImageBuffer waitForBuffer(	long pTimeOut,
																		TimeUnit pTimeUnit) throws AndorSdkJException
	{
		if (mDebugMessages)
			System.out.println("AndorCamera: waiting for a buffer for the camera with index: " + this.mCameraIndex);
		
		ImageBuffer lImageBuffer = AndorSDKJUtils.waitForBuffer(this,
																														pTimeOut,
																														pTimeUnit);
		
		//TODO: collect some of he metadata like the image timestamp. (happening in ImageBuffer)
		
		return lImageBuffer;
	}

	public int getHandle()
	{
		return mCameraHandlePointer.getInt();
	}
	
	//TODO: add get/set width/height

	@Override
	public String toString()
	{
		return String.format(	"AndorCamera [mCameraIndex=%s, mCameraHandlePointer=%s]",
													mCameraIndex,
													mCameraHandlePointer.get());
	}

}
