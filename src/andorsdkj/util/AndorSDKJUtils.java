package andorsdkj.util;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

import org.bridj.Pointer;

import andorsdkj.AndorCamera;
import andorsdkj.AndorSdkJException;
import andorsdkj.ImageBuffer;
import andorsdkj.bindings.AtcoreLibrary;

public class AndorSDKJUtils {

    public static long getInt(String pKey) throws AndorSdkJException {

        getLogger().info("Getting an integer value for the key: " + pKey);

        Pointer<Character> lKeyPointer = Pointer.pointerToWideCString(pKey);
        Pointer<Long> lPointerToLong = Pointer.allocateLong();

        handleErrorWithException("Cannot retrieve integer value for " + pKey, AtcoreLibrary.AT_GetInt(AtcoreLibrary
                .AT_HANDLE_SYSTEM, lKeyPointer, lPointerToLong));

        final long lLongResult = lPointerToLong.getLong();
        return lLongResult;
    }

    public static int getInt(AndorCamera pAndorCamera, String pFeatureName) throws AndorSdkJException {
        getLogger()
                .info("Getting an integer value from the camera: " + pAndorCamera + " for the feature " + pFeatureName);

        Pointer<Long> lStorePointer = Pointer.allocateLong();
        Pointer<Character> lFeatureName = Pointer.pointerToWideCString(pFeatureName);

        handleErrorWithException("Cannot get a long value for the following feature: " + pFeatureName,
                AtcoreLibrary.AT_GetInt(pAndorCamera
                        .getHandle(), lFeatureName, lStorePointer));

        return lStorePointer.getInt();
    }

    public static void setInt(AndorCamera pAndorCamera, String pFeatureName, int pFeatureValue) throws
            AndorSdkJException {
        getLogger()
                .info("Setting an int value: " + pFeatureValue + " for the camera: " + pAndorCamera + " for the " +
                        "feature " + pFeatureName);

        Pointer<Character> lKeyPointer = Pointer.pointerToWideCString(pFeatureName);
        handleErrorWithException("Cannot set int value for " + pFeatureName,
                AtcoreLibrary.AT_SetInt(pAndorCamera.getHandle(), lKeyPointer,
                        pFeatureValue));

    }

    public static double getFloat(AndorCamera pAndorCamera, String pFeatureName) throws AndorSdkJException {
        getLogger().info("Getting a float value from the camera: " + pAndorCamera + " for the feature " + pFeatureName);

        Pointer<Double> lStorePointer = Pointer.allocateDouble();
        Pointer<Character> lFeatureName = Pointer.pointerToWideCString(pFeatureName);

        handleErrorWithException("Cannot get a float value for the following feature: " + pFeatureName,
                AtcoreLibrary.AT_GetFloat(pAndorCamera
                        .getHandle(), lFeatureName, lStorePointer));

        return lStorePointer.getFloat();
    }

    public static void setFloat(AndorCamera pAndorCamera, String pKey, float pValue) throws AndorSdkJException {
        getLogger()
                .info("Setting a float value: " + pValue + " for the camera: " + pAndorCamera + " for the feature " +
                        pKey);

        Pointer<Character> lKeyPointer = Pointer.pointerToWideCString(pKey);
        handleErrorWithException("Cannot set a float value for " + pKey,
                AtcoreLibrary.AT_SetFloat(pAndorCamera.getHandle(), lKeyPointer, pValue));
    }

    public static boolean getBool(AndorCamera pAndorCamera, String pFeatureName) throws AndorSdkJException {
        getLogger().info("Getting a boolean feature: " + pFeatureName + " for the camera: " + pAndorCamera);

        Pointer<Character> lKeyPointer = Pointer.pointerToWideCString(pFeatureName);
        Pointer<Integer> lValuePointer = Pointer.allocateInt();
        handleErrorWithException("Cannot get a boolean value for " + pFeatureName,
                AtcoreLibrary.AT_GetBool(pAndorCamera.getHandle(), lKeyPointer,
                        lValuePointer));
        return lValuePointer.get() == 1;
    }

    public static void setBool(AndorCamera pAndorCamera, String pFeatureName, boolean pFeatureValue) throws
            AndorSdkJException {
        getLogger()
                .info("Setting a boolean feature: " + pFeatureName + " to the value: " + pFeatureValue + " for the " +
                        "camera: " + pAndorCamera);

        Pointer<Character> lKeyPointer = Pointer.pointerToWideCString(pFeatureName);
        handleErrorWithException("Cannot set boolean value for " + pFeatureName,
                AtcoreLibrary.AT_SetBool(pAndorCamera.getHandle(), lKeyPointer,
                        pFeatureValue ? 1 : 0));
    }

    public static void setEnumeratedString(AndorCamera pAndorCamera, String pFeatureName, String pFeatureValue)
            throws AndorSdkJException {
        getLogger()
                .info("Setting the feature: " + pFeatureName + " for the camera: " + pAndorCamera + " to the value "
                        + pFeatureValue);

        Pointer<Character> lKey = Pointer.pointerToWideCString(pFeatureName);
        Pointer<Character> lValue = Pointer.pointerToWideCString(pFeatureValue);

        String lErrorMessage = String.format("Cannot set %s:%s for camera %s \n", lKey, lValue, pAndorCamera);
        handleErrorWithException(lErrorMessage,
                AtcoreLibrary.AT_SetEnumeratedString(pAndorCamera.getHandle(), lKey, lValue));
    }

    public static void setCommand(AndorCamera pAndorCamera, String pCommandString) throws AndorSdkJException {
        getLogger().info("Setting a command: " + pCommandString + " for the camera: " + pAndorCamera);

        Pointer<Character> lCommandString = Pointer.pointerToWideCString(pCommandString);
        handleErrorWithException(
                "Cannot perfrom the command: " + pCommandString + " for the camera " + pAndorCamera.getHandle(),
                AtcoreLibrary
                        .AT_Command(pAndorCamera.getHandle(), lCommandString));
    }

    public static void allocateAndQueueBuffers(AndorCamera pAndorCamera, int pImageSizeBytes, int pNumberOfBuffers)
            throws AndorSdkJException {
        getLogger().info("Allocating and queueing _" + pNumberOfBuffers + "_ buffers for the camera " + pAndorCamera);

        if (pNumberOfBuffers <= 0) {
            throw new AndorSdkJException("Number of buffers cannot be 0 or negative.");
        }
        for (int i = 0; i < pNumberOfBuffers; i++) {
            Pointer<Byte> lDefineBuffer = Pointer.allocateBytes(pImageSizeBytes);
            handleErrorWithException("Cannot queue a buffer for camera: " + pAndorCamera.getHandle(),
                    AtcoreLibrary.AT_QueueBuffer(pAndorCamera
                            .getHandle(), lDefineBuffer, pImageSizeBytes));
        }
    }

    public static void allocateAndQueueAlignedBuffers(AndorCamera pAndorCamera, int pImageSizeBytes, int
            pNumberOfBuffers, ArrayList<ImageBuffer>
                                                              BufferArray) throws AndorSdkJException {
        getLogger()
                .info("Allocating and queueing _" + pNumberOfBuffers + "_ alligned buffers for the camera " +
                        pAndorCamera);

        if (pNumberOfBuffers <= 0) {
            throw new AndorSdkJException("Number of buffers cannot be 0 or negative.");
        }

        for (int i = 0; i < pNumberOfBuffers; i++) {
            allocateAndQueueAlignedBuffer(pAndorCamera, pImageSizeBytes, BufferArray);
        }
    }

    public static void allocateAndQueueAlignedBuffer(AndorCamera pAndorCamera, int pImageSizeBytes,
                                                     ArrayList<ImageBuffer> BufferArray) throws
            AndorSdkJException {

        getLogger().info("Allocating and queueing an aligned buffer for the camera " + pAndorCamera);

        Pointer<Byte> lDefineBuffer = Pointer.allocateBytes(pImageSizeBytes + 7);

        // alignment
        while (lDefineBuffer.getPeer() % 8 != 0) {
            System.out.println("pointer for buffer is: " + lDefineBuffer.getPeer());
            lDefineBuffer = lDefineBuffer.offset(1);
        }

        BufferArray.add(new ImageBuffer(lDefineBuffer, pImageSizeBytes));
        handleErrorWithException("Cannot queue a buffer for camera: " + pAndorCamera.getHandle(),
                AtcoreLibrary.AT_QueueBuffer(pAndorCamera
                        .getHandle(), lDefineBuffer, pImageSizeBytes));
    }

    public static void queueBuffer(AndorCamera pAndorCamera, Pointer<Byte> pBuffer, int pImageSize) throws
            AndorSdkJException {
        getLogger().info("Queueing a buffer for the camera " + pAndorCamera);
        handleErrorWithException("Cannot queue a buffer for camera: " + pAndorCamera.getHandle(),
                AtcoreLibrary.AT_QueueBuffer(pAndorCamera
                        .getHandle(), pBuffer, pImageSize));
    }

    //    public static Pointer<Byte> waitBuffer(AndorCamera pAndorCamera, long pTimeOut, TimeUnit pTimeUnit) throws
    // AndorSdkJException {
    //
    //        if (mDebugMessages)
    //            System.out.println("Waiting a buffer for the camera " + pAndorCamera);
    //
    //        Pointer<Pointer<Byte>> lBuffer = Pointer.allocatePointer(Byte.class);
    //        Pointer<Integer> lBufferSize = Pointer.allocateInt();
    //
    //        handleErrorWithException("Error in waiting buffer for camera: " + pAndorCamera.getHandle(),
    // AtcoreLibrary.AT_WaitBuffer(pAndorCamera
    //                .getHandle(), lBuffer, lBufferSize, Math.toIntExact(TimeUnit.MILLISECONDS.convert(pTimeOut,
    // pTimeUnit))));
    //        return lBuffer.get();
    //    }

    public static ImageBuffer waitForBuffer(AndorCamera pAndorCamera, long pTimeOut, TimeUnit pTimeUnit) throws
            AndorSdkJException {

        getLogger().info("Waiting for and returning a buffer for the camera " + pAndorCamera);

        Pointer<Pointer<Byte>> lPointerPointerBuffer = Pointer.allocatePointer(Byte.class);
        Pointer<Integer> lPointerBufferSize = Pointer.allocateInt();

        double t1 = System.nanoTime();
        handleErrorWithException("Error in waiting buffer for camera: ",
                AtcoreLibrary.AT_WaitBuffer(pAndorCamera.getHandle(),
                        lPointerPointerBuffer, lPointerBufferSize,
                        (int) TimeUnit.MILLISECONDS.convert(pTimeOut, pTimeUnit)));

        double t2 = System.nanoTime();

        getLogger().info(String.format("Buffer acquired in %.2f ms, proceeding to IB construction", (t2 - t1) * 1e-6));

        ImageBuffer lImageBuffer = new ImageBuffer(lPointerPointerBuffer.get(), lPointerBufferSize.get());

        getLogger()
                .info("Checking the ImageBuffer construction inside the wait-for-buffer method ---> " + "IB pointer: " +
                        "" + lImageBuffer
                        .getPointer
                                () + " pointer VB: " + lImageBuffer.getPointer()
                        .getValidBytes() + " image size: " + lImageBuffer.getImageSizeInBytes());

        getLogger().info(" --- Done ---");
        return lImageBuffer;
    }

    public static void handleErrorWithException(String pErrorMessage, int pErrorCode) throws AndorSdkJException {
        if (pErrorCode != AtcoreLibrary.AT_SUCCESS) {
            throw new AndorSdkJException(pErrorMessage, pErrorCode);
        }
    }

    //    public static boolean handleError(String pErrorMessage, int pErrorCode) throws AndorSdkJException {
    //        return (pErrorCode != AtcoreLibrary.AT_SUCCESS);
    //
    //    }

    public static String getString(AndorCamera pAndorCamera, String pFeatureName) throws AndorSdkJException {
        getLogger().info("GetString function for camera " + pAndorCamera);

        Pointer<Character> lFeatureName = Pointer.pointerToWideCString(pFeatureName);
        Pointer<Character> lFeatureValue = Pointer.allocateChars(10);

        handleErrorWithException("Cannot get string for feature " + pFeatureName + " from camera " + pAndorCamera + ".",
                AtcoreLibrary.AT_GetString
                        (pAndorCamera.getHandle(), lFeatureName, lFeatureValue, 10));
        return lFeatureValue.getWideCString();
    }

    public static String getEnumeratedString(AndorCamera pAndorCamera, String pFeatureName) throws AndorSdkJException {
        getLogger().info("GetEnumeratedString function for camera " + pAndorCamera);

        Pointer<Character> lFeatureName = Pointer.pointerToWideCString(pFeatureName);
        Pointer<Integer> lFeatureValue = Pointer.allocateInt();
        Pointer<Character> lFeatureValueString = Pointer.allocateChars(10);


        handleErrorWithException("Cannot get enum index for camera " + pAndorCamera,
                AtcoreLibrary.AT_GetEnumIndex(pAndorCamera.getHandle(),
                        lFeatureName, lFeatureValue));

        handleErrorWithException("getting get enum value for camera " + pAndorCamera,
                AtcoreLibrary.AT_GetEnumStringByIndex(pAndorCamera.getHandle
                        (), lFeatureName, lFeatureValue.get().intValue(), lFeatureValueString, 10));
        return lFeatureValueString.getWideCString();
    }

    private static Logger mLogger = null;

    public static Logger getLogger() {
        if (mLogger == null) {
            mLogger = Logger.getLogger("AndorLogger");
            mLogger.setUseParentHandlers(false);
            mLogger.setLevel(Level.ALL);
            Handler lHandler = new MyHandler();
//            Handler lHandler = new ConsoleHandler();
            Formatter lFormatter = new Formatter() {
                @Override
                public String format(LogRecord record) {
                    String[] str = record.getSourceClassName().split("\\.");
                    return "[" + str[str.length - 1] + "]: " + record.getMessage()+"\n";
                }
            };

            lHandler.setFormatter(lFormatter);
//            lHandler.
            mLogger.addHandler(lHandler);

//            new ConsoleHandler()
        }
        return mLogger;
    }

    public static void setLogger(Logger pLogger){
        mLogger = pLogger;



        mLogger.setUseParentHandlers(false);
        mLogger.setLevel(Level.ALL);
        Handler lHandler = new MyHandler();

//            Handler lHandler = new ConsoleHandler();
        Formatter lFormatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                String[] str = record.getSourceClassName().split("\\.");
                return "[" + str[str.length - 1] + "]: " + record.getMessage()+"\n";
            }
        };

        lHandler.setFormatter(lFormatter);
//            lHandler.
        mLogger.addHandler(lHandler);
    }


}
