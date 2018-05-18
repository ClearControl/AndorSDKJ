package andorsdkj.demo.demoLowLevel;

import org.bridj.BridJ;
import org.bridj.Pointer;

import andorsdkj.bindings.AtcoreLibrary;

public class AcquisitionDemoLowLevel {
	
	private static int toUns(Byte a) {
		int ret = 0;
		int mask = 1;
		int counter = 0;
		
		while (mask <= 255) {
			ret += (a & mask);
			counter++;
			mask <<= 1;
		//	System.out.println(mask);
		}
		
		return ret;
	}

	public static void main(String[] agrs) {
		
		byte q = -1;
		System.out.println("quick test of toUns: ");
		System.out.println("uns -1 is: " + toUns(q));
		
		int i_retCode;

		BridJ.addNativeLibraryDependencies("atmcd64d");
		BridJ.register();

		System.out.println("Initialising...");
		System.out.println();
		i_retCode = AtcoreLibrary.AT_InitialiseLibrary();

		if (i_retCode != AtcoreLibrary.AT_SUCCESS) {
			System.out.println("Error initializing library");
			System.out.println();
		} else {

			Pointer<Long> iNumberDevices = Pointer.allocateLong();
			iNumberDevices.setLong(15);

			// System.out.println("inumdev is " + iNumberDevices);
			// System.out.println("the value is " + iNumberDevices.getLong());
			long tr;

			String s = "Device Count";
			Pointer<Character> fDevCount = Pointer.pointerToWideCString(s);

		//	fDevCount.setWideCString(s); // ("Device Count");

			tr = AtcoreLibrary.AT_GetInt(AtcoreLibrary.AT_HANDLE_SYSTEM, fDevCount, iNumberDevices);

			System.out.println("iNUmberDevices is " + iNumberDevices.getInt());
			System.out.println("tr is " + tr);
			System.out.println("Feature string is " + fDevCount.getWideCString());

			long start = 0;
			long end = 0;
			
			start = System.nanoTime();
			
			if (iNumberDevices.get() <= 0) {
				System.out.println("No cameras detected");
				System.out.println();
			}
			else {
				Pointer<Integer> Hndl = Pointer.allocateInt();
				i_retCode = AtcoreLibrary.AT_Open(0, Hndl); ////////

				if (i_retCode != AtcoreLibrary.AT_SUCCESS) {
					System.out.println("Error conditions, cannot initialize camera");
					System.out.println();
				} else {
					System.out.println("Successfully initialized camera");
					System.out.println();

					// Set the pixel Encoding to the desired settings
					// Mono12Packed Data
					Pointer<Character> pixEnc = Pointer.pointerToWideCString("Pixel Encoding");
					Pointer<Character> mono12 = Pointer.pointerToWideCString("Mono12Packed");
					int pixenc = AtcoreLibrary.AT_SetEnumeratedString(Hndl.getInt(), pixEnc, mono12); ////////

					System.out.println("pixel encodint is: " + pixenc);
					System.out.println("string pixEnc is: " + pixEnc.getWideCString());
					System.out.println("string mono12 is: " + mono12.getWideCString());

					// SEt the pixel readout rate to slowest

					Pointer<Character> pixRead = Pointer.pointerToWideCString("Pixel Readout Rate");
					Pointer<Character> freq = Pointer.pointerToWideCString("100 MHz");
					i_retCode = AtcoreLibrary.AT_SetEnumeratedString(Hndl.getInt(), pixRead, freq);
					if (i_retCode == AtcoreLibrary.AT_SUCCESS) {
						System.out.println("Pixel readout rate set to 100 MHz");
						System.out.println();
					}
					System.out.println("pixel readout err code is " + i_retCode);

					// Set the exposure time of the camera to 10 milliseconds
					Pointer<Character> expTime = Pointer.pointerToWideCString("Exposure Time");
					int exptime = AtcoreLibrary.AT_SetFloat(Hndl.getInt(), expTime, 0.03);
					System.out.println("exp time ret code: " + exptime);

					// Get the number of bytes required to store one frame
					Pointer<Long> ImageSizeBytes = Pointer.allocateLong();
					Pointer<Character> imSizeBytes = Pointer.pointerToWideCString("Image Size Bytes");
					int imBS = AtcoreLibrary.AT_GetInt(Hndl.getInt(), imSizeBytes, ImageSizeBytes);
					System.out.println("image byte size " + imBS);

					System.out.println("print ImageSizeBytes " + ImageSizeBytes.getLong());

					Pointer<Integer> BufferSize = Pointer.allocateInt();
					BufferSize.setInt((int) (ImageSizeBytes.getLong()));

					// Allocate a memory buffer to store one frame
					

					Pointer<Byte> UserBuffer = Pointer.allocateBytes(BufferSize.getInt());
					// Pointer<Pointer<Byte>> UserBuffer =
					// Pointer.allocatePointer();
					// UserBuffer.set(UserBufferP);

					// Pass this buffer to the SDK
					AtcoreLibrary.AT_QueueBuffer(Hndl.getInt(), UserBuffer, BufferSize.getInt());
					start = System.nanoTime();

					// Start the acquisition running
					Pointer<Character> acqStart = Pointer.pointerToWideCString("Acquisition Start");
					AtcoreLibrary.AT_Command(Hndl.getInt(), acqStart);

					System.out.println("Waiting for acquisition...");
					System.out.println();

					// Sleep in this thread until data is ready, in this case
					// set the timeout to infinite for simplicity
					// Pointer<Byte> Buffer = Pointer.allocateByte();
					// System.out.println("print UserBuffer.getREference(): " +
					// UserBuffer);
					System.out.println("print BufferSize " + BufferSize.getInt());
					
					

					Pointer<Pointer<Byte>> testPointer = Pointer.pointerToPointer(UserBuffer);
					// int wait = AtcoreLibrary.AT_WaitBuffer(Hndl.getInt(),
					// testPointer, BufferSize,
					// 10000);
					// System.out.println("wait is " + wait);

					if (AtcoreLibrary.AT_WaitBuffer(Hndl.getInt(), testPointer, BufferSize,
							10000) == AtcoreLibrary.AT_SUCCESS) 
					{
						System.out.println("Acquisition finished successfuly");
						System.out.println("Number bytes received " + ImageSizeBytes.getLong() + "\n");
						System.out.println("Print out the first 20 pixels ");

						Pointer<Byte> Buffer = testPointer.get();
						int counter = -1;
						int fovLin = 2048;
						int pixN = fovLin*fovLin;
						
						long[][] arr = new long[2048][2048];
						//outer:
						for (int i = 0; i < pixN; i+=2) {
						//while(Buffer != null) {
							
							
//							if (counter < 3000) {
//								System.out.println("three bytes: " + Buffer.getByte() + " " + Buffer.offset(1).getByte() + " " + Buffer.offset(2).getByte());
////								if (a ==0 && b == 0 & c == 0) {
////									System.out.println("i k l " + i + " " + k + " " + l);
////									
////								}
//							}
//							
							
							Byte a = Buffer.getByte();
							long LowPixel = (Buffer.getByte() & 0xFF);
							
							
							
							// System.out.println("first lowpixel is " +
							// LowPixel);
							LowPixel <<= 4;
							Buffer = Buffer.offset(1);
							Byte b = Buffer.getByte();
							LowPixel += (Buffer.getByte() & 0xFF) & 0xF;
						//	LowPixel += (((Buffer.getByte() >>> 4) & 0xF));
							counter++;
							
							
							
							int k = (i + 1)/fovLin;
							int l = (i + 1)%fovLin;
							
//							if (counter < 100) {
//								System.out.println("lowPix, k and l are: " + LowPixel + " "+ k + " " +l);
//							}
							
							
							if (k > 2047 || k < 0) {
								throw new java.lang.IllegalArgumentException("k is: " + k);
							}
							if (l > 2047 || l < 0) {
								throw new java.lang.IllegalArgumentException("l is: " + l);
							}
							
							arr[k][l] = LowPixel;
							LowPixel = 0;
							
//							
//							long HighPixel = Buffer.getByte();
//							HighPixel = ((HighPixel & 0xF)<<8);
//							HighPixel += Buffer.offset(1).getByte();
							
							Byte c = Buffer.offset(1).getByte();
							//long HighPixel = 0;
							
							long HighPixel;
							if (counter < 0) {
								System.out.println("Long pixel " + i/fovLin + " " + i%fovLin + ":");
								long ar = (Buffer.offset(1).getByte() & 0xFF);
								System.out.println("right byte: " + ar);
								HighPixel = ar;
								HighPixel <<= 4;
								System.out.println("right byte shifted: " + HighPixel);
								long aux = ((Buffer.getByte() & 0xFF));
								System.out.println("left byte: " + aux);
								aux >>>= 4;
								System.out.println("left byte shifted: " + aux);
								HighPixel += aux;
								System.out.println("hp value: " + HighPixel);
								counter++;
							}
							else {
							
								long ar = (Buffer.offset(1).getByte() & 0xFF);
							
								HighPixel = ar;
								HighPixel <<= 4;
							
								long aux = ((Buffer.getByte() & 0xFF));
							
								aux >>>= 4;
							
								HighPixel += aux;
								counter++;
							}
							
							if (counter < 5000) {
								System.out.println("hi k l " + k + " " + l + " bytes are " + a + " " + b + " " + c );
//								if (a ==0 && b == 0 & c == 0) {
//									System.out.println("i k l " + i + " " + k + " " + l);
//									
//								}
							}
							
							
							k = (i)/fovLin;
							l = (i)%fovLin;
							
							if (counter < 0) {
								System.out.println("highPix, k and l are: " + HighPixel + " " + k + " " +l);
							}
							
							if (k > 2047 || k < 0) {
								throw new java.lang.IllegalArgumentException("k is: " + k);
							}
							if (l > 2047 || l < 0) {
								throw new java.lang.IllegalArgumentException("l is: " + l + " on step: " + counter);
							}
							
							arr[k][l] = HighPixel;
							
							HighPixel = 0;

							//System.out.println(HighPixel);
							//System.out.println(LowPixel);
							Buffer = Buffer.offset(2);
//							
							if (l == 2046) {
								Buffer = Buffer.offset(8);
							}
							

						}
						
						end = System.nanoTime();
						
						System.out.println(String.format("Acq took %f ms", (end-start)/10e6));
						start = System.nanoTime();

						UserBuffer.release();
					//	SavePNG.savePNG(arr, "C:\\Users\\myersadmin\\Documents\\images\\", "buffer.png");
						
						end = System.nanoTime();
						
						System.out.println(String.format("Save took %f ms", (end-start)/10e6));
						
						start = System.nanoTime();
						
					} else {
						System.out.println("Timeout occured check the log file...");
						System.out.println();

					}
					// Stop the acquisition
					
					
					
					Pointer<Character> acqStop = Pointer.allocateChars(16);
					acqStop.setCString("Acquisition Stop");
					AtcoreLibrary.AT_Command(Hndl.getInt(), acqStop);
					AtcoreLibrary.AT_Flush(Hndl.getInt());
				}
				AtcoreLibrary.AT_Close(Hndl.getInt());
			}
			end = System.nanoTime();
			
			System.out.println(String.format("Finalize took %f ms", (end-start)/10e6));
			
		
			AtcoreLibrary.AT_FinaliseLibrary();
		}

		System.out.println();
		System.out.println("Press any key then enter to close");
		System.out.println();

	}

}
