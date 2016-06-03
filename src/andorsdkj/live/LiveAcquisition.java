package andorsdkj.live;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import andorsdkj.AndorCamera;
import andorsdkj.AndorSdkJException;
import andorsdkj.ImageBuffer;
import andorsdkj.bindings.AtcoreLibrary;

public class LiveAcquisition
{
	private final AndorCamera mAndorCamera;

	private ArrayList<SingleFrameListener> mListenerList = new ArrayList<>();

	private volatile boolean mStopSignal;

	private CountDownLatch mStoppedSignal;

	private long mTimeOutInMilliseconds = 10000;

	public LiveAcquisition(AndorCamera pAndorCamera)
	{
		super();
		this.mAndorCamera = pAndorCamera;

		addListener((s, i) -> {
			try
			{
				reEnqueue(i);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		});
	}

	private void reEnqueue(ImageBuffer pImageBuffer) throws AndorSdkJException
	{
		mAndorCamera.enqueueBuffer(pImageBuffer);
	}

	public void addListener(SingleFrameListener pSingleFrameListener)
	{
		mListenerList.add(pSingleFrameListener);
	}
	
	private void notifyListeners(ImageBuffer pNewImageBuffer)
	{
		for(SingleFrameListener lSingleFrameListener : mListenerList)
		{
			lSingleFrameListener.newImage(this, pNewImageBuffer);
		}
	}

	public void start() throws AndorSdkJException
	{
	//	mAndorCamera.allocateAndQueueBuffers(1);

		Runnable lRunnable = () -> {

			mStoppedSignal = new CountDownLatch(1);
			mStopSignal = false;
			while (!mStopSignal)
			{
				try
				{
					System.out.println("LOOP");
					ImageBuffer lNewImageBuffer = mAndorCamera.waitForBuffer(mTimeOutInMilliseconds , TimeUnit.SECONDS);
					
					notifyListeners(lNewImageBuffer);
					//mAndorCamera.enqueueBuffer(lNewImageBuffer);
				}
				catch (Throwable e)
				{
					e.printStackTrace();
					mStopSignal = true;
				}
			}
			mStoppedSignal.countDown();

		};

		Thread lThread = new Thread(lRunnable,
																this.getClass().getName() + "Thread");
		lThread.setDaemon(true);
		lThread.setPriority(Thread.MAX_PRIORITY);

		
		
		mAndorCamera.startAcquisition();
		lThread.start();
		
		

	}

	public void stop() throws AndorSdkJException
	{
		
		
		mStopSignal = true;
		try
		{
			if (mStoppedSignal != null)
				mStoppedSignal.await(1, TimeUnit.SECONDS);
			
			mAndorCamera.stopAcquisition();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		
	}

	public AndorCamera getAndorCamera()
	{
		return mAndorCamera;
	}

}
