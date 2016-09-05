package andorsdkj.util;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.bridj.Pointer;

import andorsdkj.AndorCamera;
import andorsdkj.AndorSdkJException;
import andorsdkj.ImageBuffer;
import andorsdkj.bindings.AtcoreLibrary;

public class AndorSDKJUtils
{
	public static boolean mDebugMessages = true;

	public static Pointer<Character> getPointer(String pString)
	{
		if (mDebugMessages) 
			System.out.println("Getting a WideCString pointer to the string: " + pString);
		
		Pointer<Character> lWideCStringPointer =
																				Pointer.pointerToWideCString(pString);
		return lWideCStringPointer;
	}

	public static long getLong(String pKey) throws AndorSdkJException
	{
		if (mDebugMessages) 
			System.out.println("Getting a long value for the key: " + pKey);
		
		Pointer<Character> lKeyPointer = getPointer(pKey);
		Pointer<Long> lPointerToLong = Pointer.allocateLong();

		handleError("Cannot retrieve long value for "+ pKey,
								AtcoreLibrary.AT_GetInt(AtcoreLibrary.AT_HANDLE_SYSTEM,
																				lKeyPointer,
																				lPointerToLong));

		final long lLongResult = lPointerToLong.getLong();
		lPointerToLong.release();
		lKeyPointer.release();

		return lLongResult;

	}

	public static int getInt(	AndorCamera pAndorCamera,
														String string) throws AndorSdkJException
	{
		// Is is necessary to return the pointer? Or the user should provide a
		// pointer to store the value in?
		if (mDebugMessages)
			System.out.println("Getting an integer value from the camera: " + pAndorCamera + " for the feature " + string);
			
		Pointer<Long> lStorePointer = Pointer.allocateLong();
		Pointer<Character> lFeatureName =
																		Pointer.pointerToWideCString(string);
		handleErrorWithException("Cannot get long value for the following feature: "
															+ string,
															AtcoreLibrary.AT_GetInt(pAndorCamera.getHandle(),
																											lFeatureName,
																											lStorePointer));

		return lStorePointer.getInt();

	}

	public static void setFloat(AndorCamera pAndorCamera,
															String pKey,
															float pValue) throws AndorSdkJException
	{
		if (mDebugMessages)
			System.out.println("Setting a float value: " + pValue + " for the camera: " + pAndorCamera + " for the feature " + pKey);
		
		Pointer<Character> lKeyPointer = Pointer.pointerToWideCString(pKey);
		handleErrorWithException("Cannot set float value for "+ pKey,
								AtcoreLibrary.AT_SetFloat(pAndorCamera.getHandle(),
																					lKeyPointer,
																					pValue));

	}
	
	public static void setBool(AndorCamera pAndorCamera,
															String pKey,
															boolean pValue) throws AndorSdkJException
	{
		if (mDebugMessages)
			System.out.println("Setting a boolean feature: " + pKey + " to the value: " + pValue + " for the camera: " + pAndorCamera);
		
		Pointer<Character> lKeyPointer = Pointer.pointerToWideCString(pKey);
		handleErrorWithException("Cannot set float value for "+ pKey,
								AtcoreLibrary.AT_SetBool(pAndorCamera.getHandle(),
																					lKeyPointer,
																					pValue ? 1:0));

	}
	
	public static void setInt(AndorCamera pAndorCamera,
															String pKey,
															int pValue) throws AndorSdkJException
	{
		if (mDebugMessages)
			System.out.println("Setting an int value: " + pValue + " for the camera: " + pAndorCamera + " for the feature " + pKey);
		
		Pointer<Character> lKeyPointer = Pointer.pointerToWideCString(pKey);
		handleErrorWithException("Cannot set int value for "+ pKey,
								AtcoreLibrary.AT_SetInt(pAndorCamera.getHandle(),
																					lKeyPointer,
																					pValue));

	}

	public static void setKeyValue(	AndorCamera pAndorCamera,
																	String pKey,
																	String pValue) throws AndorSdkJException
	{

		
		if (mDebugMessages)
			System.out.println("Setting a feature: " + pKey + " for the camera: " + pAndorCamera + " to value " + pValue);
		
		Pointer<Character> lKey = Pointer.pointerToWideCString(pKey);
		Pointer<Character> lValue = Pointer.pointerToWideCString(pValue);
		String lErrorMessage = String.format(	"Cannot set %s:%s for camera %s \n",
																					lKey,
																					lValue,
																					pAndorCamera);
		handleErrorWithException(lErrorMessage,
								AtcoreLibrary.AT_SetEnumeratedString(	pAndorCamera.getHandle(),
																											lKey,
																											lValue));

		lKey.release();
		lValue.release();
	}

	public static void setCommand(AndorCamera pAndorCamera,
																String pCommandString) throws AndorSdkJException
	{
		if (mDebugMessages)
			System.out.println("Setting a command: " + pCommandString + " for the camera: " + pAndorCamera);

		Pointer<Character> lCommandString =
																			Pointer.pointerToWideCString(pCommandString);
		handleErrorWithException("Cannot perfrom the command: " + pCommandString + " for the camera "
															+ pAndorCamera.getHandle(),
															AtcoreLibrary.AT_Command(	pAndorCamera.getHandle(),
																												lCommandString));
	}

	public static void allocateAndQueueBuffers(	AndorCamera pAndorCamera,
																							int pImageSizeBytes,
																							int pNumberOfBuffers) throws AndorSdkJException
	{
		if (mDebugMessages)
			System.out.println("Allocating and queueing _" + pNumberOfBuffers + "_ buffers for the camera " + pAndorCamera);
		
		if (pNumberOfBuffers <= 0)
		{
			throw new AndorSdkJException("Number of buffers cannot be negative.");
		}
		for (int i = 0; i < pNumberOfBuffers; i++)
		{
			Pointer<Byte> lDefineBuffer =
																	Pointer.allocateBytes(pImageSizeBytes);
			handleErrorWithException("Cannot queue a buffer for camera: "
																+ pAndorCamera.getHandle(),
																AtcoreLibrary.AT_QueueBuffer(	pAndorCamera.getHandle(),
																															lDefineBuffer,
																															pImageSizeBytes));
		}

	}
	
	public static void allocateAndQueueAlignedBuffers(	AndorCamera pAndorCamera,
																							int pImageSizeBytes,
																							int pNumberOfBuffers, ArrayList<ImageBuffer> BufferArray) throws AndorSdkJException
	{
		if (mDebugMessages)
			System.out.println("Allocating and queueing _" + pNumberOfBuffers + "_ buffers for the camera " + pAndorCamera);
		
		if (pNumberOfBuffers <= 0)
		{
			throw new AndorSdkJException("Number of buffers cannot be negative.");
		}
		
	//	BufferArray = new ArrayList<>();
		
		for (int i = 0; i < pNumberOfBuffers; i++)
		{
			allocateAndQueueAlignedBuffer(pAndorCamera, pImageSizeBytes, BufferArray);
		}

	}
	

	public static void allocateAndQueueAlignedBuffer(AndorCamera pAndorCamera,
																										int pImageSizeBytes, ArrayList<ImageBuffer> BufferArray) throws AndorSdkJException
	{
		
		if (mDebugMessages)
			System.out.println("Allocating and queueing an aligned buffer for the camera " + pAndorCamera);
		
		Pointer<Byte> lDefineBuffer =
																Pointer.allocateBytes(pImageSizeBytes + 7);
		// alignment
		while (lDefineBuffer.getPeer() % 8 != 0)
			System.out.println("pointer for buffer is: " + lDefineBuffer.getPeer());
			lDefineBuffer = lDefineBuffer.offset(1);
//		long initPeer = lDefineBuffer.getPeer();
//		long finPeer = (initPeer + 7) & ~7;
//		lDefineBuffer = lDefineBuffer.offset(finPeer - initPeer);
		
		
		BufferArray.add(new ImageBuffer(lDefineBuffer, pImageSizeBytes));	
		handleErrorWithException("Cannot queue a buffer for camera: "
															+ pAndorCamera.getHandle(),
															AtcoreLibrary.AT_QueueBuffer(	pAndorCamera.getHandle(),
																														lDefineBuffer,
																														pImageSizeBytes));
	}

	public static void queueBuffer(	AndorCamera pAndorCamera,
																	Pointer<Byte> pBuffer,
																	int pImageSize) throws AndorSdkJException
	{
		if (mDebugMessages)
			System.out.println("Queueing a buffer for the camera " + pAndorCamera);
		
		handleErrorWithException("Cannot queue a buffer for camera: "
															+ pAndorCamera.getHandle(),
															AtcoreLibrary.AT_QueueBuffer(	pAndorCamera.getHandle(),
																														pBuffer,
																														pImageSize));

	}

	public static Pointer<Byte> waitBuffer(	AndorCamera pAndorCamera,
																					long pTimeOut,
																					TimeUnit pTimeUnit) throws AndorSdkJException
	{
		
		if (mDebugMessages)
			System.out.println("Waiting a buffer for the camera " + pAndorCamera);
		
		Pointer<Pointer<Byte>> lBuffer = Pointer.allocatePointer(Byte.class);
		Pointer<Integer> lBufferSize = Pointer.allocateInt();

		handleErrorWithException("Error in waiting buffer for camera: "
															+ pAndorCamera.getHandle(),
															AtcoreLibrary.AT_WaitBuffer(pAndorCamera.getHandle(),
																													lBuffer,
																													lBufferSize,
																													Math.toIntExact(TimeUnit.MILLISECONDS.convert(pTimeOut,
																																																				pTimeUnit))));
		return lBuffer.get();
	}

	public static ImageBuffer waitForBuffer(AndorCamera pAndorCamera,
																					long pTimeOut,
																					TimeUnit pTimeUnit) throws AndorSdkJException
	{
		
		if (mDebugMessages)
			System.out.println("waiting for and returning a buffer for the camera " + pAndorCamera);
		
		Pointer<Pointer<Byte>> lPointerPointerBuffer = Pointer.allocatePointer(Byte.class);
		Pointer<Integer> lPointerBufferSize = Pointer.allocateInt();
		System.out.println("allocated pointers");
		handleErrorWithException(	"Error in waiting buffer for camera: ",
															AtcoreLibrary.AT_WaitBuffer(pAndorCamera.getHandle(),
																													lPointerPointerBuffer,
																													lPointerBufferSize,
																													(int) TimeUnit.MILLISECONDS.convert(pTimeOut,
																																															pTimeUnit)));
		System.out.println("buffer acquired, proceeding to IB construction");

		ImageBuffer lImageBuffer = new ImageBuffer(	lPointerPointerBuffer.get(),
																								lPointerBufferSize.get());
		System.out.println("checking the ImageBuffer construction inside the wait-for-buffer method ---> IB pointer: " + lImageBuffer.getPointer() + " pointer VB: " + lImageBuffer.getPointer().getValidBytes() + " image size: " + lImageBuffer.getImageSizeInBytes() );
		
	

		lPointerPointerBuffer.release();
		lPointerBufferSize.release();

		return lImageBuffer;
	}

	public static void handleErrorWithException(String pErrorMessage,
																							int pErrorCode) throws AndorSdkJException
	{
		if (pErrorCode != AtcoreLibrary.AT_SUCCESS)
		{
			throw new AndorSdkJException(pErrorMessage, pErrorCode);
		}

	}

	public static boolean handleError(String pErrorMessage,
																		int pErrorCode) throws AndorSdkJException
	{
		return (pErrorCode != AtcoreLibrary.AT_SUCCESS);

	}

}
