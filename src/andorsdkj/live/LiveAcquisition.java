package andorsdkj.live;

import static andorsdkj.bindings.util.Buffer16ToArray.toArray;
import static andorsdkj.bindings.util.SavePNG.savePNG;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.bridj.Pointer;

import andorsdkj.AndorCamera;
import andorsdkj.AndorSdkJException;
import andorsdkj.ImageBuffer;
import andorsdkj.bindings.util.ArrayAndMax;

public class LiveAcquisition {

	private boolean triggerControl;

	private volatile int[] coords = new int[2];

	private final AndorCamera mAndorCamera;

	private ArrayList<SingleFrameListener> mListenerList = new ArrayList<>();

	private volatile boolean mStopSignal;

	private CountDownLatch mStoppedSignal;

	private long mTimeOutInMilliseconds = 10000;

	private volatile boolean correctPos = false;

	private volatile AtomicInteger count = new AtomicInteger(0);

	private ImageBuffer lNewImageBuffer;

	public LiveAcquisition(AndorCamera pAndorCamera) {
		super();
		this.mAndorCamera = pAndorCamera;
		this.coords[0] = 0;
		this.coords[1] = 0;
	}

	public synchronized int[] getCorrCoords() {
		this.triggerControl = true;
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.coords;
	}

	public synchronized void corrPos(boolean value) {
		this.correctPos = value;
	}

	private void reEnqueue(ImageBuffer pImageBuffer) throws AndorSdkJException {
		this.mAndorCamera.enqueueBuffer(pImageBuffer);
	}

	public void addListener(SingleFrameListener pSingleFrameListener) {
		mListenerList.add(pSingleFrameListener);
	}

	private void notifyListeners(ImageBuffer pNewImageBuffer) {
		for (SingleFrameListener lSingleFrameListener : mListenerList) {
			lSingleFrameListener.newImage(this, pNewImageBuffer);
		}
	}

	public void start() throws AndorSdkJException {
		// mAndorCamera.allocateAndQueueBuffers(1);
		Runnable lRunnable = () -> {

			mStoppedSignal = new CountDownLatch(1);
			mStopSignal = false;
			Object lock = new Object();
			while (!mStopSignal) {
				try {

					System.out.println("LOOP: " + (count.get() + 1));

					synchronized (this) {
						mAndorCamera.SoftwareTrigger();
						lNewImageBuffer = mAndorCamera.waitForBuffer(mTimeOutInMilliseconds, TimeUnit.MILLISECONDS);

						if (triggerControl) {
							System.out.println("in trigger control");
							triggerControl = false;
							int sizeint = lNewImageBuffer.getImageSizeInBytes();
							Pointer<Byte> holder = Pointer.allocateBytes(sizeint);
							lNewImageBuffer.getPointer().copyTo(holder, sizeint);

							ImageBuffer lImageBufferToProcess = new ImageBuffer(holder, sizeint);
							int[][] BufferArray = toArray(lImageBufferToProcess, 2048, 2048);
							ArrayAndMax aux = new ArrayAndMax(BufferArray);

							savePNG(BufferArray, "C:\\Users\\myersadmin\\images\\", "array1.png");
							notify();
						}

					}

					count.incrementAndGet();

					notifyListeners(lNewImageBuffer);

					mAndorCamera.enqueueBuffer(lNewImageBuffer);
				} catch (Throwable e) {
					e.printStackTrace();
					mStopSignal = true;
				}
			}
			mStoppedSignal.countDown();

		};

		Thread lThread = new Thread(lRunnable, this.getClass().getName() + "Thread");
		lThread.setDaemon(true);
		lThread.setPriority(Thread.MAX_PRIORITY);

		mAndorCamera.startAcquisition();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lThread.start();

	}

	public void stop() throws AndorSdkJException {

		mStopSignal = true;
		try {
			if (mStoppedSignal != null)
				mStoppedSignal.await(1, TimeUnit.SECONDS);

			mAndorCamera.stopAcquisition();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public AndorCamera getAndorCamera() {
		return mAndorCamera;
	}

}
