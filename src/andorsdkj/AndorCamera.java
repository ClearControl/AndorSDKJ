package andorsdkj;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.bridj.Pointer;

import andorsdkj.bindings.AtcoreLibrary;
import andorsdkj.enums.CycleMode;
import andorsdkj.enums.ReadOutRate;
import andorsdkj.enums.TriggerMode;
import andorsdkj.util.AndorSDKJUtils;

import static andorsdkj.util.AndorSDKJUtils.getLogger;


public class AndorCamera implements AutoCloseable {

    private int mCameraIndex;
    private Pointer<Integer> mCameraHandlePointer;
    public boolean mDebugMessages = true;
    private ArrayList<ImageBuffer> lImageBufferArray;

    public AndorCamera(int pCameraIndex) throws AndorSdkJException {

        getLogger().info("Opening an Andor camera with index: " + pCameraIndex);

        mCameraIndex = pCameraIndex;
        mCameraHandlePointer = Pointer.allocateInt();
        lImageBufferArray = new ArrayList<>();

        this.open();
        this.flushBuffers(); // necessary?
    }

    public void open() throws AndorSdkJException {
        AndorSDKJUtils.handleErrorWithException("Cannot open camera for index " + mCameraIndex, AtcoreLibrary.AT_Open(mCameraIndex,
                mCameraHandlePointer));
    }

    @Override
    public void close() throws AndorSdkJException {
        getLogger().info("Emptying buffers and closing the Andor camera with index: " + this.mCameraIndex);

        this.flushBuffers();
        AndorSDKJUtils.handleErrorWithException("Error while closing camera " + mCameraIndex, AtcoreLibrary.AT_Close(mCameraHandlePointer.getInt()));
    }

    public void setStandardPixelEncoding() throws AndorSdkJException {
        getLogger().info("Setting standard pixel encoding for camera with index " + this.mCameraIndex);
        AndorSDKJUtils.setEnumeratedString(this, "SimplePreAmpGainControl", "12-bit (high well capacity)");
        AndorSDKJUtils.setEnumeratedString(this, "PixelEncoding", "Mono12Packed");
    }

    public void setBinning(int pCoupling) throws AndorSdkJException {
        getLogger().info("Setting binning for camera with index " + this.mCameraIndex);
        String lBinningString = pCoupling + "x" + pCoupling;
        AndorSDKJUtils.setEnumeratedString(this, "AOIBinning", lBinningString);
    }

    public void set16PixelEncoding() throws AndorSdkJException {
        getLogger().info("Setting 16 bit pixel encoding for camera with index " + this.mCameraIndex);
        AndorSDKJUtils.setEnumeratedString(this, "SimplePreAmpGainControl", "16-bit (low noise & high well capacity)");
        AndorSDKJUtils.setEnumeratedString(this, "PixelEncoding", "Mono16");
    }

    public void setTriggeringMode(TriggerMode pAndorTriggerMode) throws AndorSdkJException {
        getLogger().info("Setting triggering mode to: " + pAndorTriggerMode.name() + " for the camera with index: " + this
                    .mCameraIndex);
        String str = pAndorTriggerMode.toString().substring(0, 1) + pAndorTriggerMode.toString().substring(1).toLowerCase();
        AndorSDKJUtils.setEnumeratedString(this, "TriggerMode", str);
    }

    public void setFrameCount(int pNumberOfImages) throws AndorSdkJException {
        getLogger().info("Setting FrameCount to: " + pNumberOfImages + " for the camera with index " + this.mCameraIndex);
        AndorSDKJUtils.setInt(this, "FrameCount", pNumberOfImages);
    }

    public void SoftwareTrigger() throws AndorSdkJException {
        getLogger().info("Software trigger for the camera with index: " + this.mCameraIndex);
        AndorSDKJUtils.setCommand(this, "SoftwareTrigger");
    }

    public void setCycleMode(CycleMode pAndorCycleMode) throws AndorSdkJException {
        getLogger().info("Setting the cycling mode to: " + pAndorCycleMode.name() + " for the camera with index " + this
                    .mCameraIndex);
        String str = pAndorCycleMode.toString().substring(0, 1) + pAndorCycleMode.toString().substring(1).toLowerCase();
        AndorSDKJUtils.setEnumeratedString(this, "CycleMode", str);
    }

    public void setExposureTimeInSeconds(double pExposureTimeSeconds) throws AndorSdkJException {
        getLogger().info("Setting the exposure time to: " + pExposureTimeSeconds + " for the camera with index" + this
                    .mCameraIndex);
        AndorSDKJUtils.setFloat(this, "ExposureTime", (float) pExposureTimeSeconds);
    }

    public void setReadoutRate(ReadOutRate pReadOutRate) throws AndorSdkJException {
        getLogger().info("Setting readout rate to: " + pReadOutRate + " for the camera with index " + this.mCameraIndex);
        AndorSDKJUtils.setEnumeratedString(this, "PixelReadoutRate", pReadOutRate.name().replace('_', ' ').trim());
    }

    public int getImageSizeInBytes() throws AndorSdkJException {
        getLogger().info("Getting image size for the camera with index " + this.mCameraIndex);

        int lImageSizeInBytes = AndorSDKJUtils.getInt(this, "ImageSizeBytes");
        getLogger().info("Image size in bytes is: " + lImageSizeInBytes);

        return lImageSizeInBytes;
    }

    public void startAcquisition() throws AndorSdkJException {
        getLogger().info("Starting acquisition for the camera with index: " + this.mCameraIndex);
        AndorSDKJUtils.setCommand(this, "AcquisitionStart");
    }

    public void stopAcquisition() throws AndorSdkJException {
        getLogger().info("Stopping the acquisition for the camera with index: " + this.mCameraIndex);
        AndorSDKJUtils.setCommand(this, "AcquisitionStop");
    }

    public void allocateAndQueueBuffers(int pNumberOfBuffers) throws AndorSdkJException {
        getLogger().info("Allocating and queueing _" + pNumberOfBuffers + "_ of buffers for the camera with index: " + this
                    .mCameraIndex);
        AndorSDKJUtils.allocateAndQueueBuffers(this, getImageSizeInBytes(), pNumberOfBuffers);
    }

    public void allocateAndQueueAlignedBuffers(int pNumberOfBuffers) throws AndorSdkJException {
        getLogger().info("Allocating and queueing _" + pNumberOfBuffers + "_ of buffers for the camera with index: " + this
                    .mCameraIndex);
        AndorSDKJUtils.allocateAndQueueAlignedBuffers(this, getImageSizeInBytes(), pNumberOfBuffers, lImageBufferArray);
    }

    public void enqueueBuffer(ImageBuffer pImageBuffer) throws AndorSdkJException {
        getLogger().info("Enqueueing a buffer for the camera with index: " + this.mCameraIndex);
        Pointer<Byte> lPointer = pImageBuffer.getPointer();
        int lImageSize = pImageBuffer.getImageSizeInBytes();
        AndorSDKJUtils.queueBuffer(this, lPointer, lImageSize);
    }

    public ImageBuffer waitForBuffer(long pTimeOut, TimeUnit pTimeUnit) throws AndorSdkJException {
        getLogger().info("Waiting for a buffer for the camera with index: " + this.mCameraIndex);
        ImageBuffer lImageBuffer = AndorSDKJUtils.waitForBuffer(this, pTimeOut, pTimeUnit);

        // TODO: collect some of he metadata like the image timestamp.
        // (happening in ImageBuffer)

        return lImageBuffer;
    }

    public int getHandle() {
        return mCameraHandlePointer.getInt();
    }

    public void flushBuffers() throws AndorSdkJException {
        getLogger().info("Flushing buffers for the camera with index: " + this.mCameraIndex);
        AndorSDKJUtils.handleErrorWithException("Sth wrong with flushing buffers for the camera: " + this.mCameraIndex, AtcoreLibrary.AT_Flush(this
                .getHandle()));
    }

    // TODO: add get/set width/height

    @Override
    public String toString() {
        return String.format("AndorCamera [mCameraIndex=%s, mCameraHandlePointer=%s]", mCameraIndex, mCameraHandlePointer.get());
    }

    public void setFrameWidth(int pWidth) throws AndorSdkJException {
        getLogger().info("Setting image width for the Andor Camera " + this.mCameraIndex);
        AndorSDKJUtils.setInt(this, "AOIWidth", pWidth);
    }

    public void setFrameHeight(int pHeight) throws AndorSdkJException {
        getLogger().info("Setting image height for the Andor Camera " + this.mCameraIndex);
        AndorSDKJUtils.setInt(this, "AOIHeight", pHeight);
    }

    public void collectMetadata(boolean pFlag) throws AndorSdkJException {
        getLogger().info("Setting metadata to: " + pFlag + "for camera: " + this.mCameraIndex);
        AndorSDKJUtils.setBool(this, "MetadataEnable", pFlag);
    }

    public void collectTimestamp(boolean pFlag) throws AndorSdkJException {
        getLogger().info("Setting collectTimestep to: " + pFlag + "for camera: " + this.mCameraIndex);
        AndorSDKJUtils.setBool(this, "MetadataTimestamp", pFlag);
    }

    public void collectFrameInfo(boolean pFlag) throws AndorSdkJException {
        getLogger().info("Setting collectFrameInfo to: " + pFlag + "for camera: " + this.mCameraIndex);
        AndorSDKJUtils.setBool(this, "MetadataFrameInfo", pFlag);
    }

    public int getTimestampClockFrequency() throws AndorSdkJException {
        getLogger().info("Getting TimestampClockFrequency for camera: " + this.mCameraIndex);
        return AndorSDKJUtils.getInt(this, "TimestampClockFrequency");
    }

    public int getStrideInPixels(int pBytesPerPixel) throws AndorSdkJException {
        return AndorSDKJUtils.getInt(this, "AOIStride") / pBytesPerPixel;
    }

    public int getFrameHeight() throws AndorSdkJException {
        return AndorSDKJUtils.getInt(this, "AOIHeight");
    }

    public int getFrameWidth() throws AndorSdkJException {
        return AndorSDKJUtils.getInt(this, "AOIWidth");
    }

    public void setOverlapReadoutMode(boolean pFlag) throws AndorSdkJException {
        AndorSDKJUtils.setBool(this, "Overlap", pFlag);
    }

    public boolean getOverlapReadoutMode() throws AndorSdkJException {
        return AndorSDKJUtils.getBool(this, "Overlap");
    }

    public double getExternalTriggerDelay() throws AndorSdkJException {
        return AndorSDKJUtils.getFloat(this, "ExternalTriggerDelay");
    }

    public void setDebugMessagesOn(boolean pFlag) {
        mDebugMessages = pFlag;
        if (mDebugMessages)
            getLogger().setLevel(Level.ALL);
        else
            getLogger().setLevel(Level.OFF);
    }

    public String getReadOutRate() throws AndorSdkJException {
        //	    return AndorSDKJUtils.getString(this, "PixelReadoutRate", 2, TimeUnit.SECONDS);
        return AndorSDKJUtils.getEnumeratedString(this, "PixelReadoutRate");
    }
}
