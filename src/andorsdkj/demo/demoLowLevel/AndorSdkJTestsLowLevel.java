package andorsdkj.demo.demoLowLevel;

import static org.junit.Assert.assertTrue;

import org.bridj.Pointer;
import org.junit.Test;

import andorsdkj.ImageBuffer;
import andorsdkj.bindings.AtcoreLibrary;

public class AndorSdkJTestsLowLevel {

    // Assumption: there is at least one real camera connected.

    @Test
    public void testLibraryInstanciation() {
        int lReturnCode = AtcoreLibrary.AT_InitialiseLibrary();
        System.out.println(lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
    }

    @Test
    public void testLibraryAndCountDevices() throws InterruptedException {

        System.out.println("AT_InitialiseLibrary");
        int lReturnCode = AtcoreLibrary.AT_InitialiseLibrary();
        System.out.println("return code=" + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);

        Thread.sleep(1000);

        Pointer<Character> lDeviceCountName =
                Pointer.pointerToWideCString("Device Count");
        Pointer<Long> lNumberDevices = Pointer.allocateLong();

        System.out.println("AT_GetInt");
        lReturnCode =
                AtcoreLibrary.AT_GetInt(AtcoreLibrary.AT_HANDLE_SYSTEM,
                        lDeviceCountName,
                        lNumberDevices);

        System.out.println("return code=" + lReturnCode);
        System.out.println("nb devices=" + lNumberDevices.getLong());
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);

        assertTrue(lNumberDevices.getLong() >= 3);

        lDeviceCountName.release();
        lNumberDevices.release();

        Pointer<Integer> lCameraHandle = Pointer.allocateInt();
        System.out.println("AT_Open");
        lReturnCode = AtcoreLibrary.AT_Open(0, lCameraHandle);
        System.out.println("return code=" + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);

        System.out.println("AT_Close");
        AtcoreLibrary.AT_Close(lCameraHandle.getInt());

        System.out.println("AT_FinaliseLibrary");
        lReturnCode = AtcoreLibrary.AT_FinaliseLibrary();
        System.out.println("return code=" + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
    }

    @Test
    public void testSingleImageAcquisition() throws InterruptedException {

        System.out.println("AT_InitialiseLibrary");
        int lReturnCode = AtcoreLibrary.AT_InitialiseLibrary();
        System.out.println("return code=" + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);

        Thread.sleep(1000);

        Pointer<Character> lDeviceCountName =
                Pointer.pointerToWideCString("Device Count");
        Pointer<Long> lNumberDevices = Pointer.allocateLong();

        System.out.println("AT_GetInt");
        lReturnCode =
                AtcoreLibrary.AT_GetInt(AtcoreLibrary.AT_HANDLE_SYSTEM,
                        lDeviceCountName,
                        lNumberDevices);

        System.out.println("return code=" + lReturnCode);
        System.out.println("nb devices=" + lNumberDevices.getLong());
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);

        assertTrue(lNumberDevices.getLong() >= 3);

        lDeviceCountName.release();
        lNumberDevices.release();

        Pointer<Integer> lCameraHandle = Pointer.allocateInt();
        System.out.println("AT_Open");
        lReturnCode = AtcoreLibrary.AT_Open(0, lCameraHandle);
        System.out.println("return code=" + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);

        /******/

        // Set the pixel Encoding to the desired settings
        // Mono12Packed Data
        Pointer<Character> pixEnc =
                Pointer.pointerToWideCString("Pixel Encoding");
        Pointer<Character> mono12 =
                Pointer.pointerToWideCString("Mono12Packed");
        int pixenc = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(),
                pixEnc,
                mono12); ////////

        System.out.println("pixel encodint is: " + pixenc);
        System.out.println("string pixEnc is: "
                + pixEnc.getWideCString());
        System.out.println("string mono12 is: "
                + mono12.getWideCString());

        // Set the pixel readout rate to slowest

        Pointer<Character> pixRead = Pointer.pointerToWideCString("Pixel Readout Rate");
        Pointer<Character> freq = Pointer.pointerToWideCString("100 MHz");
        lReturnCode =
                AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(),
                        pixRead,
                        freq);
        if (lReturnCode == AtcoreLibrary.AT_SUCCESS) {
            System.out.println("Pixel readout rate set to 100 MHz");
            System.out.println();
        }
        System.out.println("pixel readout err code is " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);

        // Set the exposure time of the camera to 10 milliseconds
        Pointer<Character> expTime = Pointer.pointerToWideCString("Exposure Time");
        int exptime = AtcoreLibrary.AT_SetFloat(lCameraHandle.getInt(),
                expTime,
                0.03);
        System.out.println("exp time ret code: " + exptime);

        //
		/*Pointer<Character> lCycleMode =
																	Pointer.pointerToWideCString("CycleMode");
		Pointer<Character> lContinuous = Pointer.pointerToWideCString("Continuous");
		lReturnCode =
								AtcoreLibrary.AT_SetEnumeratedString(	lCameraHandle.getInt(),
																											lCycleMode,
																											lContinuous); 
		assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);/**/

        // Get the number of bytes required to store one frame
        Pointer<Long> lImageSizeBytes = Pointer.allocateLong();
        Pointer<Character> lFeatureImageSizeBytes =
                Pointer.pointerToWideCString("Image Size Bytes");
        lReturnCode = AtcoreLibrary.AT_GetInt(lCameraHandle.getInt(),
                lFeatureImageSizeBytes,
                lImageSizeBytes);
        System.out.println("lReturnCode= " + lReturnCode);
        System.out.println("print ImageSizeBytes "
                + lImageSizeBytes.getLong());
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);

        // Allocate a memory buffer to store one frame:

        Pointer<Byte> lUserBuffer =
                Pointer.allocateBytes(lImageSizeBytes.getLong());

        // Pass this buffer to the SDK
        lReturnCode =
                AtcoreLibrary.AT_QueueBuffer(lCameraHandle.getInt(),
                        lUserBuffer,
                        (lImageSizeBytes.getInt()));
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);

        // Start Chrono:
        long start = System.nanoTime();

        // Start the acquisition running
        Pointer<Character> lAcqStartCommand =
                Pointer.pointerToWideCString("Acquisition Start");
        lReturnCode = AtcoreLibrary.AT_Command(lCameraHandle.getInt(),
                lAcqStartCommand);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);

        System.out.println("Waiting for acquisition...");
        System.out.println();

        // Sleep in this thread until data is ready, in this case
        // set the timeout to infinite for simplicity
        // Pointer<Byte> Buffer = Pointer.allocateByte();
        // System.out.println("print UserBuffer.getREference(): " +
        // UserBuffer);
        System.out.println("print BufferSize "
                + lImageSizeBytes.getLong());

        // Allocate pointer in which to put the pointer to the received buffer:
        Pointer<Pointer<Byte>> lReturnedImageBufferPointerPointer =
                Pointer.allocatePointer(Byte.class);
        Pointer<Integer> lReturnedImageBufferSize = Pointer.allocateInt();

        // Waits for the buffer:

        if ((lReturnCode = AtcoreLibrary.AT_WaitBuffer(lCameraHandle.getInt(),
                lReturnedImageBufferPointerPointer,
                lReturnedImageBufferSize,
                10000)) == AtcoreLibrary.AT_SUCCESS) {
            System.out.println("Acquisition finished successfuly");
            System.out.println("Number bytes received "
                    + lReturnedImageBufferSize.getInt() + "\n");

            assertTrue(lReturnedImageBufferSize.getInt() > 0);

            for (int i = 0; i < 32; i++)
                System.out.println(lReturnedImageBufferPointerPointer.get()
                        .get(i));
        }
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);

        // Stop the acquisition
        Pointer<Character> acqStop = Pointer.allocateChars(32);
        acqStop.setWideCString("Acquisition Stop");
        lReturnCode = AtcoreLibrary.AT_Command(lCameraHandle.getInt(),
                acqStop);
        System.out.println("ret from stop: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        lReturnCode = AtcoreLibrary.AT_Flush(lCameraHandle.getInt());
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);

        /******/

        System.out.println("AT_Close");
        lReturnCode = AtcoreLibrary.AT_Close(lCameraHandle.getInt());
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);

        System.out.println("AT_FinaliseLibrary");
        lReturnCode = AtcoreLibrary.AT_FinaliseLibrary();
        System.out.println("return code=" + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
    }

    @Test
    public void testSoftwareTrigger() throws InterruptedException {

        // Initializing the library...
        System.out.print("Initializing the  library... ");
        int lReturnCode = AtcoreLibrary.AT_InitialiseLibrary();
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Identifying the number of devices
        System.out.print("Identifying the number of devices... ");
        Pointer<Long> lNumberDevices = Pointer.allocateLong();
        Pointer<Character> fDeviceCount = Pointer.pointerToWideCString("DeviceCount");
        lReturnCode = AtcoreLibrary.AT_GetInt(AtcoreLibrary.AT_HANDLE_SYSTEM, fDeviceCount, lNumberDevices);
        System.out.print("Return code: " + lReturnCode + ". # of devices is: " + lNumberDevices.getInt());
        assertTrue(lNumberDevices.getLong() > 2);
        System.out.println(" Done! Number of devices: " + lNumberDevices.getLong());

        // Initializing the camera, creating a handle
        System.out.print("Initializing the camera... ");
        Pointer<Integer> lCameraHandle = Pointer.allocateInt();
        lReturnCode = AtcoreLibrary.AT_Open(0, lCameraHandle);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Changing the preamp gain
        System.out.print("Setting the preamp gain so that it matches the encoding we aim at... ");
        Pointer<Character> fPreAmpGain = Pointer.pointerToWideCString("SimplePreAmpGainControl");
        String lPreampGainStr = "12-bit (high well capacity)";
        Pointer<Character> lGain = Pointer.pointerToWideCString(lPreampGainStr);
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fPreAmpGain, lGain);
        System.out.print(" Return code: " + lReturnCode);
        System.out.println(" Done! Pream gain is set to: " + lPreampGainStr);

        // Set pixel encoding
        System.out.print("Setting the pixel encoding... ");
        Pointer<Character> fPixelEncoding = Pointer.pointerToWideCString("PixelEncoding");
        Pointer<Character> lMono12Packed = Pointer.pointerToWideCString("Mono12Packed");
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fPixelEncoding, lMono12Packed);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Set trigger mode
        System.out.print("Setting the triggering mode... ");
        Pointer<Character> fTriggerMode = Pointer.pointerToWideCString("TriggerMode");
        Pointer<Character> lSoftwareTrigger = Pointer.pointerToWideCString("Software");
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fTriggerMode, lSoftwareTrigger);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Setting a cycle mode to continuous
        System.out.print("Setting a cycle mode to continuous... ");
        Pointer<Character> fCycleMode = Pointer.pointerToWideCString("CycleMode");
        Pointer<Character> lContinuousMode = Pointer.pointerToWideCString("Continuous");
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fCycleMode, lContinuousMode);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Setting exposure time
        System.out.print("Setting exposure time... ");
        Pointer<Character> fExposureTime = Pointer.pointerToWideCString("ExposureTime");
        float lExposureTimeSeconds = 0.03f;
        lReturnCode = AtcoreLibrary.AT_SetFloat(lCameraHandle.getInt(), fExposureTime, lExposureTimeSeconds);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done! Set to: " + lExposureTimeSeconds);

        // Setting readout rate
        System.out.print("Setting the readout rate... ");
        Pointer<Character> fReadoutRate = Pointer.pointerToWideCString("PixelReadoutRate");
        Pointer<Character> lReadoutRateMHz = Pointer.pointerToWideCString("100 MHz");
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fReadoutRate, lReadoutRateMHz);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done! Set to 100 MHz ");

        // Set the number of images to acquire and run the acquisition
        System.out.print("Setting the number of images and starting the acquisition... ");
        int lSequenceLength = 5;
        Pointer<Long> lImageSizeBytes = Pointer.allocateLong();
        Pointer<Character> lImSize = Pointer.pointerToWideCString("ImageSizeBytes");
        lReturnCode = AtcoreLibrary.AT_GetInt(lCameraHandle.getInt(), lImSize, lImageSizeBytes);
        System.out.print("Return code: " + lReturnCode + ".");
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done! (received size of the image: " + lImageSizeBytes.getLong() + ").");

        // Queueing a buffer
        System.out.print("Queuing a buffer... ");
        Pointer<Byte> lDefineBuffer = Pointer.allocateBytes(lImageSizeBytes.getLong() + 7);
        while (lDefineBuffer.getPeer() % 8 != 0)
            lDefineBuffer = lDefineBuffer.offset(1);
        lReturnCode = AtcoreLibrary.AT_QueueBuffer(lCameraHandle.getInt(), lDefineBuffer, lImageSizeBytes.getInt());
        System.out.print("Return code: " + lReturnCode + ". Pointer address is: " + lDefineBuffer.getPeer());
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Starting the acquisition
        System.out.print("Starting the acquisition... ");
        Pointer<Character> lAcquisitionStart = Pointer.pointerToWideCString("AcquisitionStart");
        lReturnCode = AtcoreLibrary.AT_Command(lCameraHandle.getInt(), lAcquisitionStart);
        System.out.print(" Return code: " + lReturnCode + ".");
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println("Successfully started the aqcuisition");

        for (int i = 0; i < lSequenceLength; i++) {
            Pointer<Pointer<Byte>> lBuffer = Pointer.allocatePointer(Byte.class);
            Pointer<Integer> lBufferSize = Pointer.allocateInt();


            // Triggering
            Pointer<Character> lSoftwareTriggerShot = Pointer.pointerToWideCString("SoftwareTrigger");
            System.out.print("Trigger pulse: " + i + "... ");
            lReturnCode = AtcoreLibrary.AT_Command(lCameraHandle.getInt(), lSoftwareTriggerShot);
            System.out.print("Return code: " + lReturnCode + ".");
            assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
            System.out.println(" Successfully triggered at iteration: " + i);

            // Waiting for the buffer
            System.out.print("Waiting for the buffer at iteration " + i + "... ");
            lReturnCode = AtcoreLibrary.AT_WaitBuffer(lCameraHandle.getInt(), lBuffer, lBufferSize, 10000);
            System.out.print("Return code: " + lReturnCode + ".");
            assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
            System.out.println(" Successfully received a buffer at iteration: " + i);

            System.out.println("First three bytes of the image " + i + " are: " + lBuffer.get().getByte() +
                    " " + lBuffer.get().offset(1).get() +
                    " " + lBuffer.get().offset(2).get());
            System.out.println("Buffer specs: buffer pointer ---> " + lBuffer.getPointer(Byte.class) + " VB: " + lBuffer
                    .getPointer(Byte.class).getValidBytes());
            ImageBuffer ib = new ImageBuffer(lBuffer.get(), lBufferSize.get());
            //		savePNG(ib, "C:\\Users\\myersadmin\\images\\", "buffer_test.png");


            // Returning the buffer
            lReturnCode = AtcoreLibrary.AT_QueueBuffer(lCameraHandle.getInt(), lBuffer.get(), lBufferSize.getInt());
            System.out.print("Returning the buffer at iteration " + i + "... ");
            assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
            System.out.println("Successfully returned the buffer at iteration: " + i);

        }

        System.out.println("Exit the acquisition loop");

        // Stopping the acquisition
        System.out.print("Stopping the acquisition... ");
        Pointer<Character> lAcquisitionStop = Pointer.pointerToWideCString("AcquisitionStop");
        lReturnCode = AtcoreLibrary.AT_Command(lCameraHandle.getInt(), lAcquisitionStop);
        System.out.print(" Return code: " + lReturnCode + ".");
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println("Successfully stopped the aqcuisition");

        // Closing and finalizing
        System.out.print("Closing the camera... ");
        lReturnCode = AtcoreLibrary.AT_Close(lCameraHandle.getInt());
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        System.out.print("Finalizing the library... ");
        lReturnCode = AtcoreLibrary.AT_FinaliseLibrary();
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");
        System.out.print("Successfully finished the SequenceAcquisition Test");
    }

    @Test
    public void testPixelEncoding() throws InterruptedException {

        // Initializing the library...
        System.out.print("Initializing the  library... ");
        int lReturnCode = AtcoreLibrary.AT_InitialiseLibrary();
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Identifying the number of devices
        System.out.print("Identifying the number of devices... ");
        Pointer<Long> lNumberDevices = Pointer.allocateLong();
        Pointer<Character> fDeviceCount = Pointer.pointerToWideCString("DeviceCount");
        lReturnCode = AtcoreLibrary.AT_GetInt(AtcoreLibrary.AT_HANDLE_SYSTEM, fDeviceCount, lNumberDevices);
        System.out.print("Return code: " + lReturnCode + ". # of devices is: " + lNumberDevices.getInt());
        assertTrue(lNumberDevices.getLong() > 2);
        System.out.println(" Done! Number of devices: " + lNumberDevices.getLong());

        // Initializing the camera, creating a handle
        System.out.print("Initializing the camera... ");
        Pointer<Integer> lCameraHandle = Pointer.allocateInt();
        lReturnCode = AtcoreLibrary.AT_Open(0, lCameraHandle);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Changing the preamp gain
        System.out.print("Setting the preamp gain so that it matches the encoding we aim at... ");
        Pointer<Character> fPreAmpGain = Pointer.pointerToWideCString("SimplePreAmpGainControl");
        String lPreampGainStr = "12-bit (high well capacity)";
        Pointer<Character> lGain = Pointer.pointerToWideCString(lPreampGainStr);
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fPreAmpGain, lGain);
        System.out.print(" Return code: " + lReturnCode);
        System.out.println(" Done! Pream gain is set to: " + lPreampGainStr);

        // Set pixel encoding
        System.out.print("Setting the pixel encoding... ");
        Pointer<Character> fPixelEncoding = Pointer.pointerToWideCString("PixelEncoding");
        Pointer<Character> lMono12Packed = Pointer.pointerToWideCString("Mono12Packed");
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fPixelEncoding, lMono12Packed);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Closing and finalizing
        System.out.print("Closing the camera... ");
        lReturnCode = AtcoreLibrary.AT_Close(lCameraHandle.getInt());
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        System.out.print("Finalizing the library... ");
        lReturnCode = AtcoreLibrary.AT_FinaliseLibrary();
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");
    }

    @Test
    public void testBinning() throws InterruptedException {

        // Initializing the library...
        System.out.print("Initializing the  library... ");
        int lReturnCode = AtcoreLibrary.AT_InitialiseLibrary();
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Identifying the number of devices
        System.out.print("Identifying the number of devices... ");
        Pointer<Long> lNumberDevices = Pointer.allocateLong();
        Pointer<Character> fDeviceCount = Pointer.pointerToWideCString("DeviceCount");
        lReturnCode = AtcoreLibrary.AT_GetInt(AtcoreLibrary.AT_HANDLE_SYSTEM, fDeviceCount, lNumberDevices);
        System.out.print("Return code: " + lReturnCode + ". # of devices is: " + lNumberDevices.getInt());
        assertTrue(lNumberDevices.getLong() > 2);
        System.out.println(" Done! Number of devices: " + lNumberDevices.getLong());

        // Initializing the camera, creating a handle
        System.out.print("Initializing the camera... ");
        Pointer<Integer> lCameraHandle = Pointer.allocateInt();
        lReturnCode = AtcoreLibrary.AT_Open(0, lCameraHandle);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Changing the preamp gain
        System.out.print("Setting the preamp gain so that it matches the encoding we aim at... ");
        Pointer<Character> fPreAmpGain = Pointer.pointerToWideCString("SimplePreAmpGainControl");
        String lPreampGainStr = "12-bit (high well capacity)";
        Pointer<Character> lGain = Pointer.pointerToWideCString(lPreampGainStr);
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fPreAmpGain, lGain);
        System.out.print(" Return code: " + lReturnCode);
        System.out.println(" Done! Pream gain is set to: " + lPreampGainStr);

        // Set pixel encoding
        System.out.print("Setting the pixel encoding... ");
        Pointer<Character> fPixelEncoding = Pointer.pointerToWideCString("PixelEncoding");
        Pointer<Character> lMono12Packed = Pointer.pointerToWideCString("Mono12Packed");
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fPixelEncoding, lMono12Packed);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Set binning
        System.out.print("Setting the binning... ");
        Pointer<Character> lBinningCommandString = Pointer.pointerToWideCString("AOIBinning");
        Pointer<Character> lBinningMode = Pointer.pointerToWideCString("2x2");
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), lBinningCommandString, lBinningMode);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Closing and finalizing
        System.out.print("Closing the camera... ");
        lReturnCode = AtcoreLibrary.AT_Close(lCameraHandle.getInt());
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        System.out.print("Finalizing the library... ");
        lReturnCode = AtcoreLibrary.AT_FinaliseLibrary();
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");
    }

    @Test
    public void testFrameCountSequenceAcquisition() throws InterruptedException {

        // Initializing the library...
        System.out.print("Initializing the  library... ");
        int lReturnCode = AtcoreLibrary.AT_InitialiseLibrary();
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Identifying the number of devices
        System.out.print("Identifying the number of devices... ");
        Pointer<Long> lNumberDevices = Pointer.allocateLong();
        Pointer<Character> fDeviceCount = Pointer.pointerToWideCString("DeviceCount");
        lReturnCode = AtcoreLibrary.AT_GetInt(AtcoreLibrary.AT_HANDLE_SYSTEM, fDeviceCount, lNumberDevices);
        System.out.print("Return code: " + lReturnCode + ". # of devices is: " + lNumberDevices.getInt());
        assertTrue(lNumberDevices.getLong() > 2);
        System.out.println(" Done! Number of devices: " + lNumberDevices.getLong());

        // Initializing the camera, creating a handle
        System.out.print("Initializing the camera... ");
        Pointer<Integer> lCameraHandle = Pointer.allocateInt();
        lReturnCode = AtcoreLibrary.AT_Open(0, lCameraHandle);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Changing the preamp gain
        System.out.print("Setting the preamp gain so that it matches the encoding we aim at... ");
        Pointer<Character> fPreAmpGain = Pointer.pointerToWideCString("SimplePreAmpGainControl");
        String lPreampGainStr = "12-bit (high well capacity)";
        Pointer<Character> lGain = Pointer.pointerToWideCString(lPreampGainStr);
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fPreAmpGain, lGain);
        System.out.print(" Return code: " + lReturnCode);
        System.out.println(" Done! Pream gain is set to: " + lPreampGainStr);

        // Set pixel encoding
        System.out.print("Setting the pixel encoding... ");
        Pointer<Character> fPixelEncoding = Pointer.pointerToWideCString("PixelEncoding");
        Pointer<Character> lMono12Packed = Pointer.pointerToWideCString("Mono12Packed");
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fPixelEncoding, lMono12Packed);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Setting readout rate
        System.out.print("Setting the readout rate... ");
        Pointer<Character> fReadoutRate = Pointer.pointerToWideCString("PixelReadoutRate");
        Pointer<Character> lReadoutRateMHz = Pointer.pointerToWideCString("100 MHz");
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fReadoutRate, lReadoutRateMHz);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done! Set to 100 MHz ");

        // Get the image size
        System.out.print("Getting the image size... ");
        Pointer<Long> lImageSizeBytes = Pointer.allocateLong();
        Pointer<Character> lImSize = Pointer.pointerToWideCString("ImageSizeBytes");
        lReturnCode = AtcoreLibrary.AT_GetInt(lCameraHandle.getInt(), lImSize, lImageSizeBytes);
        System.out.print("Return code: " + lReturnCode + ".");
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done! (received size of the image: " + lImageSizeBytes.getLong() + ").");

        //Setting a cycle mode to fixed
        System.out.print("Setting a cycle mode to continuous... ");
        Pointer<Character> fCycleMode = Pointer.pointerToWideCString("CycleMode");
        Pointer<Character> lContinuousMode = Pointer.pointerToWideCString("Fixed");
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fCycleMode, lContinuousMode);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Set frame count
        System.out.print("Setting the frame count... ");
        Pointer<Character> fFrameCount = Pointer.pointerToWideCString("FrameCount");
        Pointer<Long> lNumFrames = Pointer.allocateLong();
        lNumFrames.setLong(3);
        lReturnCode = AtcoreLibrary.AT_SetInt(lCameraHandle.getInt(), fFrameCount, lNumFrames.getLong());
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done! Set Frame Count to: " + lNumFrames.getLong());


        // Queueing a buffer
        System.out.print("Queuing a buffer 1... ");
        Pointer<Byte> lDefineBuffer1 = Pointer.allocateBytes(lImageSizeBytes.getLong() + 7);

        while (lDefineBuffer1.getPeer() % 8 != 0)
            lDefineBuffer1 = lDefineBuffer1.offset(1);

        lReturnCode = AtcoreLibrary.AT_QueueBuffer(lCameraHandle.getInt(), lDefineBuffer1, lImageSizeBytes.getInt());
        System.out.print("Return code: " + lReturnCode + ". Pointer address is: " + lDefineBuffer1.getPeer());
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Queueing a buffer
        System.out.print("Queuing a buffer 2... ");
        Pointer<Byte> lDefineBuffer2 = Pointer.allocateBytes(lImageSizeBytes.getLong() + 7);

        while (lDefineBuffer2.getPeer() % 8 != 0)
            lDefineBuffer2 = lDefineBuffer2.offset(1);

        lReturnCode = AtcoreLibrary.AT_QueueBuffer(lCameraHandle.getInt(), lDefineBuffer2, lImageSizeBytes.getInt());
        System.out.print("Return code: " + lReturnCode + ". Pointer address is: " + lDefineBuffer2.getPeer());
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Starting the acquisition
        System.out.print("Starting the acquisition... ");
        Pointer<Character> lAcquisitionStart = Pointer.pointerToWideCString("AcquisitionStart");
        lReturnCode = AtcoreLibrary.AT_Command(lCameraHandle.getInt(), lAcquisitionStart);
        System.out.print(" Return code: " + lReturnCode + ".");
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println("Successfully started the aqcuisition");


        // Buffer
        Pointer<Pointer<Byte>> lBuffer = Pointer.allocatePointer(Byte.class);
        Pointer<Integer> lBufferSize = Pointer.allocateInt();

        // Waiting for the buffer
        System.out.print("Waiting for buffer  1");
        lReturnCode = AtcoreLibrary.AT_WaitBuffer(lCameraHandle.getInt(), lBuffer, lBufferSize, 10000);
        System.out.print("Return code: " + lReturnCode + ".");
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);

        System.out.println("First three bytes of the image are: " + lBuffer.get().getByte() +
                " " + lBuffer.get().offset(1).get() +
                " " + lBuffer.get().offset(2).get());

        // Waiting for the buffer
        System.out.print("Waiting for buffer 2 ");
        lReturnCode = AtcoreLibrary.AT_WaitBuffer(lCameraHandle.getInt(), lBuffer, lBufferSize, 10000);
        System.out.print("Return code: " + lReturnCode + ".");
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);

        System.out.println("First three bytes of the image are: " + lBuffer.get().getByte() +
                " " + lBuffer.get().offset(1).get() +
                " " + lBuffer.get().offset(2).get());

        Thread.sleep(5000);

        // Queueing a buffer
        System.out.print("Queuing a buffer 3... ");
        Pointer<Byte> lDefineBuffer3 = Pointer.allocateBytes(lImageSizeBytes.getLong() + 7);

        while (lDefineBuffer3.getPeer() % 8 != 0)
            lDefineBuffer3 = lDefineBuffer3.offset(1);

        lReturnCode = AtcoreLibrary.AT_QueueBuffer(lCameraHandle.getInt(), lDefineBuffer3, lImageSizeBytes.getInt());
        System.out.print("Return code: " + lReturnCode + ". Pointer address is: " + lDefineBuffer3.getPeer());
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Waiting for the buffer
        System.out.print("Waiting for buffer 3 ");
        lReturnCode = AtcoreLibrary.AT_WaitBuffer(lCameraHandle.getInt(), lBuffer, lBufferSize, 10000);
        System.out.print("Return code: " + lReturnCode + ".");
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);

        System.out.println("First three bytes of the image are: " + lBuffer.get().getByte() +
                " " + lBuffer.get().offset(1).get() +
                " " + lBuffer.get().offset(2).get());

        // Stopping the acquisition
        System.out.print("Stopping the acquisition... ");
        Pointer<Character> lAcquisitionStop = Pointer.pointerToWideCString("AcquisitionStop");
        lReturnCode = AtcoreLibrary.AT_Command(lCameraHandle.getInt(), lAcquisitionStop);
        System.out.print(" Return code: " + lReturnCode + ".");
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println("Successfully stopped the aqcuisition");

        // Closing and finalizing
        System.out.print("Closing the camera... ");
        lReturnCode = AtcoreLibrary.AT_Close(lCameraHandle.getInt());
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        System.out.print("Finalizing the library... ");
        lReturnCode = AtcoreLibrary.AT_FinaliseLibrary();
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");
        System.out.print("Successfully finished the SequenceAcquisition Test");
    }


    @Test
    public void testAcquireOneImage() throws InterruptedException {

        // Initializing the library...
        System.out.print("Initializing the  library... ");
        int lReturnCode = AtcoreLibrary.AT_InitialiseLibrary();
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Identifying the number of devices
        System.out.print("Identifying the number of devices... ");
        Pointer<Long> lNumberDevices = Pointer.allocateLong();
        Pointer<Character> fDeviceCount = Pointer.pointerToWideCString("DeviceCount");
        lReturnCode = AtcoreLibrary.AT_GetInt(AtcoreLibrary.AT_HANDLE_SYSTEM, fDeviceCount, lNumberDevices);
        System.out.print("Return code: " + lReturnCode + ". # of devices is: " + lNumberDevices.getInt());
        assertTrue(lNumberDevices.getLong() > 2);
        System.out.println(" Done! Number of devices: " + lNumberDevices.getLong());

        // Initializing the camera, creating a handle
        System.out.print("Initializing the camera... ");
        Pointer<Integer> lCameraHandle = Pointer.allocateInt();
        lReturnCode = AtcoreLibrary.AT_Open(0, lCameraHandle);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Changing the preamp gain
        System.out.print("Setting the preamp gain so that it matches the encoding we aim at... ");
        Pointer<Character> fPreAmpGain = Pointer.pointerToWideCString("SimplePreAmpGainControl");
        String lPreampGainStr = "16-bit (low noise & high well capacity)";
        Pointer<Character> lGain = Pointer.pointerToWideCString(lPreampGainStr);
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fPreAmpGain, lGain);
        System.out.print(" Return code: " + lReturnCode);
        System.out.println(" Done! Pream gain is set to: " + lPreampGainStr);

        // Set pixel encoding
        System.out.print("Setting the pixel encoding... ");
        Pointer<Character> fPixelEncoding = Pointer.pointerToWideCString("PixelEncoding");
        Pointer<Character> lMono12Packed = Pointer.pointerToWideCString("Mono16");
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fPixelEncoding, lMono12Packed);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Setting exposure time
        System.out.print("Setting exposure time... ");
        Pointer<Character> fExposureTime = Pointer.pointerToWideCString("ExposureTime");
        float lExposureTimeSeconds = .1f;
        lReturnCode = AtcoreLibrary.AT_SetFloat(lCameraHandle.getInt(), fExposureTime, lExposureTimeSeconds);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done! Set to: " + lExposureTimeSeconds);

        // Setting readout rate
        System.out.print("Setting the readout rate... ");
        Pointer<Character> fReadoutRate = Pointer.pointerToWideCString("PixelReadoutRate");
        Pointer<Character> lReadoutRateMHz = Pointer.pointerToWideCString("280 MHz");
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fReadoutRate, lReadoutRateMHz);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done! Set to " + lReadoutRateMHz.getWideCString());

        // Get the image size
        System.out.print("Getting the image size... ");
        Pointer<Long> lImageSizeBytes = Pointer.allocateLong();
        Pointer<Character> lImSize = Pointer.pointerToWideCString("ImageSizeBytes");
        lReturnCode = AtcoreLibrary.AT_GetInt(lCameraHandle.getInt(), lImSize, lImageSizeBytes);
        System.out.print("Return code: " + lReturnCode + ".");
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done! (received size of the image: " + lImageSizeBytes.getLong() + ").");

        // Queueing a buffer
        System.out.print("Queuing a buffer... ");
        Pointer<Byte> lDefineBuffer = Pointer.allocateBytes(lImageSizeBytes.getLong() + 7);
        while (lDefineBuffer.getPeer() % 8 != 0)
            lDefineBuffer = lDefineBuffer.offset(1);
        lReturnCode = AtcoreLibrary.AT_QueueBuffer(lCameraHandle.getInt(), lDefineBuffer, lImageSizeBytes.getInt());
        System.out.print("Return code: " + lReturnCode + ". Pointer address is: " + lDefineBuffer.getPeer());
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Starting the acquisition
        System.out.print("Starting the acquisition... ");
        Pointer<Character> lAcquisitionStart = Pointer.pointerToWideCString("AcquisitionStart");
        lReturnCode = AtcoreLibrary.AT_Command(lCameraHandle.getInt(), lAcquisitionStart);
        System.out.print(" Return code: " + lReturnCode + ".");
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println("Successfully started the aqcuisition");


        // Buffer
        Pointer<Pointer<Byte>> lBuffer = Pointer.allocatePointer(Byte.class);
        Pointer<Integer> lBufferSize = Pointer.allocateInt();

        // Waiting for the buffer

        System.out.print("Waiting for buffer  1");
        double t1 = System.nanoTime();
        lReturnCode = AtcoreLibrary.AT_WaitBuffer(lCameraHandle.getInt(), lBuffer, lBufferSize, 10000);
        double t2 = System.nanoTime();
        System.out.print("Return code: " + lReturnCode + ".");
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);


        System.out.println("First three bytes of the image are: " + lBuffer.get().getByte() +
                " " + lBuffer.get().offset(1).get() +
                " " + lBuffer.get().offset(2).get());

        System.out.println(String.format("It took %f ms", (t2 - t1) * 1e-6));

        // Querying the readout time
        System.out.print("Querying the readout time...... ");
        Pointer<Character> lFeature = Pointer.pointerToWideCString("ReadoutTime");
        Pointer<Double> lValue = Pointer.allocateDouble();
        lReturnCode = AtcoreLibrary.AT_GetFloat(lCameraHandle.get(), lFeature, lValue);
        System.out.print("Return code: " + lReturnCode);
        System.out.println(String.format("Readout time is %.3f.", lValue.get()));
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");


        // Stopping the acquisition
        System.out.print("Stopping the acquisition... ");
        Pointer<Character> lAcquisitionStop = Pointer.pointerToWideCString("AcquisitionStop");
        lReturnCode = AtcoreLibrary.AT_Command(lCameraHandle.getInt(), lAcquisitionStop);
        System.out.print(" Return code: " + lReturnCode + ".");
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println("Successfully stopped the aqcuisition");

        // Closing and finalizing
        System.out.print("Closing the camera... ");
        lReturnCode = AtcoreLibrary.AT_Close(lCameraHandle.getInt());
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        System.out.print("Finalizing the library... ");
        lReturnCode = AtcoreLibrary.AT_FinaliseLibrary();
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");
    }

    @Test
    public void testFullAOIControlAvailability() throws InterruptedException {

        // Initializing the library...
        System.out.print("Initializing the  library... ");
        int lReturnCode = AtcoreLibrary.AT_InitialiseLibrary();
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Identifying the number of devices
        System.out.print("Identifying the number of devices... ");
        Pointer<Long> lNumberDevices = Pointer.allocateLong();
        Pointer<Character> fDeviceCount = Pointer.pointerToWideCString("DeviceCount");
        lReturnCode = AtcoreLibrary.AT_GetInt(AtcoreLibrary.AT_HANDLE_SYSTEM, fDeviceCount, lNumberDevices);
        System.out.print("Return code: " + lReturnCode + ". # of devices is: " + lNumberDevices.getInt());
        assertTrue(lNumberDevices.getLong() > 2);
        System.out.println(" Done! Number of devices: " + lNumberDevices.getLong());

        // Initializing the camera, creating a handle
        System.out.print("Initializing the camera... ");
        Pointer<Integer> lCameraHandle = Pointer.allocateInt();
        lReturnCode = AtcoreLibrary.AT_Open(0, lCameraHandle);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Identifying the availability of FullAOICOntrol
        System.out.print("Identifying the number of devices... ");
        Pointer<Character> fFullAOI = Pointer.pointerToWideCString("FullAOIControl");
        Pointer<Integer> wtf = Pointer.allocateInt();
        lReturnCode = AtcoreLibrary.AT_GetBool(lCameraHandle.getInt(), fFullAOI, wtf);
        System.out.print("wtf is: " + wtf.getInt());

        // Closing and finalizing
        System.out.print("Closing the camera... ");
        lReturnCode = AtcoreLibrary.AT_Close(lCameraHandle.getInt());
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        System.out.print("Finalizing the library... ");
        lReturnCode = AtcoreLibrary.AT_FinaliseLibrary();
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");
    }

    @Test
    public void testReadoutTime() throws InterruptedException {

        // Initializing the library...
        System.out.print("Initializing the  library... ");
        int lReturnCode = AtcoreLibrary.AT_InitialiseLibrary();
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Identifying the number of devices
        System.out.print("Identifying the number of devices... ");
        Pointer<Long> lNumberDevices = Pointer.allocateLong();
        Pointer<Character> fDeviceCount = Pointer.pointerToWideCString("DeviceCount");
        lReturnCode = AtcoreLibrary.AT_GetInt(AtcoreLibrary.AT_HANDLE_SYSTEM, fDeviceCount, lNumberDevices);
        System.out.print("Return code: " + lReturnCode + ". # of devices is: " + lNumberDevices.getInt());
        assertTrue(lNumberDevices.getLong() > 2);
        System.out.println(" Done! Number of devices: " + lNumberDevices.getLong());

        // Initializing the camera, creating a handle
        System.out.print("Initializing the camera... ");
        Pointer<Integer> lCameraHandle = Pointer.allocateInt();
        lReturnCode = AtcoreLibrary.AT_Open(0, lCameraHandle);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Changing the preamp gain
        System.out.print("Setting the preamp gain so that it matches the encoding we aim at... ");
        Pointer<Character> fPreAmpGain = Pointer.pointerToWideCString("SimplePreAmpGainControl");
        String lPreampGainStr = "16-bit (low noise & high well capacity)";
        Pointer<Character> lGain = Pointer.pointerToWideCString(lPreampGainStr);
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fPreAmpGain, lGain);
        System.out.print(" Return code: " + lReturnCode);
        System.out.println(" Done! Pream gain is set to: " + lPreampGainStr);

        // Set pixel encoding
        System.out.print("Setting the pixel encoding... ");
        Pointer<Character> fPixelEncoding = Pointer.pointerToWideCString("PixelEncoding");
        Pointer<Character> lMono12Packed = Pointer.pointerToWideCString("Mono16");
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fPixelEncoding, lMono12Packed);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Setting readout rate
        System.out.print("Setting the readout rate... ");
        Pointer<Character> fReadoutRate = Pointer.pointerToWideCString("PixelReadoutRate");
        Pointer<Character> lReadoutRateMHz = Pointer.pointerToWideCString("280 MHz");
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fReadoutRate, lReadoutRateMHz);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done! Set to 100 MHz ");

        // Querying the readout time
        System.out.print("Querying the readout time...... ");
        Pointer<Character> lFeature = Pointer.pointerToWideCString("ReadoutTime");
        Pointer<Double> lValue = Pointer.allocateDouble();
        lReturnCode = AtcoreLibrary.AT_GetFloat(lCameraHandle.get(), lFeature, lValue);
        System.out.print("Return code: " + lReturnCode);
        System.out.println(String.format("Readout time is %.3f.", lValue.get()));
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Closing and finalizing
        System.out.print("Closing the camera... ");
        lReturnCode = AtcoreLibrary.AT_Close(lCameraHandle.getInt());
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        System.out.print("Finalizing the library... ");
        lReturnCode = AtcoreLibrary.AT_FinaliseLibrary();
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");
    }

    @Test
    public void testReopen() throws InterruptedException {

        // Initializing the library...
        System.out.print("Initializing the  library... ");
        int lReturnCode = AtcoreLibrary.AT_InitialiseLibrary();
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

//        // Identifying the number of devices
//        System.out.print("Identifying the number of devices... ");
//        Pointer<Long> lNumberDevices = Pointer.allocateLong();
//        Pointer<Character> fDeviceCount = Pointer.pointerToWideCString("DeviceCount");
//        lReturnCode = AtcoreLibrary.AT_GetInt(AtcoreLibrary.AT_HANDLE_SYSTEM, fDeviceCount, lNumberDevices);
//        System.out.print("Return code: " + lReturnCode + ". # of devices is: " + lNumberDevices.getInt());
//        assertTrue(lNumberDevices.getLong() > 2);
//        System.out.println(" Done! Number of devices: " + lNumberDevices.getLong());
//
//        // Initializing the camera, creating a handle
//        System.out.print("Initializing the camera... ");
//        Pointer<Integer> lCameraHandle = Pointer.allocateInt();
//        lReturnCode = AtcoreLibrary.AT_Open(0, lCameraHandle);
//        System.out.print("Return code: " + lReturnCode);
//        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
//        System.out.println(" Done!");
//
//        // Changing the preamp gain
//        System.out.print("Setting the preamp gain so that it matches the encoding we aim at... ");
//        Pointer<Character> fPreAmpGain = Pointer.pointerToWideCString("SimplePreAmpGainControl");
//        String lPreampGainStr = "16-bit (low noise & high well capacity)";
//        Pointer<Character> lGain = Pointer.pointerToWideCString(lPreampGainStr);
//        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fPreAmpGain, lGain);
//        System.out.print(" Return code: " + lReturnCode);
//        System.out.println(" Done! Pream gain is set to: " + lPreampGainStr);
//
//        // Set pixel encoding
//        System.out.print("Setting the pixel encoding... ");
//        Pointer<Character> fPixelEncoding = Pointer.pointerToWideCString("PixelEncoding");
//        Pointer<Character> lMono12Packed = Pointer.pointerToWideCString("Mono16");
//        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fPixelEncoding, lMono12Packed);
//        System.out.print("Return code: " + lReturnCode);
//        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
//        System.out.println(" Done!");
//
//        // Setting readout rate
//        System.out.print("Setting the readout rate... ");
//        Pointer<Character> fReadoutRate = Pointer.pointerToWideCString("PixelReadoutRate");
//        Pointer<Character> lReadoutRateMHz = Pointer.pointerToWideCString("280 MHz");
//        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fReadoutRate, lReadoutRateMHz);
//        System.out.print("Return code: " + lReturnCode);
//        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
//        System.out.println(" Done! Set to 100 MHz ");
//
//        // Flushing
//        System.out.print("Flushing buffers... ");
//        lReturnCode = AtcoreLibrary.AT_Flush(lCameraHandle.getInt());
//        System.out.print("Return code: " + lReturnCode);
//        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
//        System.out.println(" Done!");
//
//        // Closing and finalizing
//        System.out.print("Closing the camera... ");
//        lReturnCode = AtcoreLibrary.AT_Close(lCameraHandle.getInt());
//        System.out.print("Return code: " + lReturnCode);
//        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
//        System.out.println(" Done!");

        System.out.print("Finalizing the library... ");
        lReturnCode = AtcoreLibrary.AT_FinaliseLibrary();
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");


        Thread.sleep(2000);
        // ------ repeat! -----//
        // Initializing the library...
        System.out.print("Initializing the  library... ");
         lReturnCode = AtcoreLibrary.AT_InitialiseLibrary();
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Identifying the number of devices
        System.out.print("Identifying the number of devices... ");
        Pointer<Long> lNumberDevices = Pointer.allocateLong();
        Pointer<Character> fDeviceCount = Pointer.pointerToWideCString("DeviceCount");
        lReturnCode = AtcoreLibrary.AT_GetInt(AtcoreLibrary.AT_HANDLE_SYSTEM, fDeviceCount, lNumberDevices);
        System.out.print("Return code: " + lReturnCode + ". # of devices is: " + lNumberDevices.getInt());
        assertTrue(lNumberDevices.getLong() > 2);
        System.out.println(" Done! Number of devices: " + lNumberDevices.getLong());

        // Initializing the camera, creating a handle
        System.out.print("Initializing the camera... ");
        Pointer<Integer> lCameraHandle = Pointer.allocateInt();
        lReturnCode = AtcoreLibrary.AT_Open(0, lCameraHandle);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Changing the preamp gain
        System.out.print("Setting the preamp gain so that it matches the encoding we aim at... ");
        Pointer<Character> fPreAmpGain = Pointer.pointerToWideCString("SimplePreAmpGainControl");
        String lPreampGainStr = "16-bit (low noise & high well capacity)";
        Pointer<Character> lGain = Pointer.pointerToWideCString(lPreampGainStr);
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fPreAmpGain, lGain);
        System.out.print(" Return code: " + lReturnCode);
        System.out.println(" Done! Pream gain is set to: " + lPreampGainStr);

        // Set pixel encoding
        System.out.print("Setting the pixel encoding... ");
        Pointer<Character> fPixelEncoding = Pointer.pointerToWideCString("PixelEncoding");
        Pointer<Character> lMono12Packed = Pointer.pointerToWideCString("Mono16");
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fPixelEncoding, lMono12Packed);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Setting readout rate
        System.out.print("Setting the readout rate... ");
        Pointer<Character> fReadoutRate = Pointer.pointerToWideCString("PixelReadoutRate");
        Pointer<Character> lReadoutRateMHz = Pointer.pointerToWideCString("280 MHz");
        lReturnCode = AtcoreLibrary.AT_SetEnumeratedString(lCameraHandle.getInt(), fReadoutRate, lReadoutRateMHz);
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done! Set to 100 MHz ");

        // Flushing
        System.out.print("Flushing buffers... ");
        lReturnCode = AtcoreLibrary.AT_Flush(lCameraHandle.getInt());
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        // Closing and finalizing
        System.out.print("Closing the camera... ");
        lReturnCode = AtcoreLibrary.AT_Close(lCameraHandle.getInt());
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");

        System.out.print("Finalizing the library... ");
        lReturnCode = AtcoreLibrary.AT_FinaliseLibrary();
        System.out.print("Return code: " + lReturnCode);
        assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
        System.out.println(" Done!");
    }

}