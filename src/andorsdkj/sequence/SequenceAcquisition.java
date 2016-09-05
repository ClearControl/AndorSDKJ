package andorsdkj.sequence;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import andorsdkj.AndorCamera;
import andorsdkj.AndorSdkJException;
import andorsdkj.enums.CycleMode;

public class SequenceAcquisition implements AutoCloseable
{

	private AndorCamera mAndorCamera;
	private ArrayList<ImageSequenceListener> mListenerList = new ArrayList<>();
	private long mHeight, mWidth, mDepth;
	private ImageSequence mImSec;
	
	//TODO introduce left adn right margins

	public SequenceAcquisition(AndorCamera pAndorCamera, ImageSequence pImageSequence)
	{
		super();
		this.mAndorCamera = pAndorCamera;
		this.mImSec = pImageSequence;
		this.mDepth = mImSec.getDepth();
	}

	public void acquireSequence(long pTimeOut, TimeUnit pTimeUnit) throws AndorSdkJException
	{

		mAndorCamera.setCycleMode(CycleMode.FIXED);
//		this.mHeight = pHeight;
//		this.mWidth = pWidth;
//		this.mDepth = pDepth;
//		
//		if ((pHeight > 2048) || (pHeight < 0) || (pWidth > 2048) || (pWidth < 0)) {
//			throw new java.lang.IllegalArgumentException("Image dimensions are negative or exceed the camera chip size.");
//		}
//		
//		if ((pDepth <= 0)) {
//			throw new java.lang.IllegalArgumentException("The depth of the sequence is 0 or negative.");
//		}
		
		
		mAndorCamera.setFrameCount((int)mDepth);

		
		//mImSec = new ImageSequence(mAndorCamera.getImageSizeInBytes(), mDepth);
		
		

		for (int i = 0; i < mDepth; i++)
		{
			mAndorCamera.enqueueBuffer(mImSec.getImageBufferArray()[i]);
		}

		mAndorCamera.startAcquisition();

		for (int i = 0; i < mDepth; i++)
		{
			mAndorCamera.waitForBuffer(pTimeOut, pTimeUnit);
		}


		//mAndorCamera.stopAcquisition();
		notifyListeners(mImSec);

	}

	public void addListener(ImageSequenceListener pImageSequenceListener)
	{
		mListenerList.add(pImageSequenceListener);
	}

	private void notifyListeners(ImageSequence pImageSequence)
	{
		for (ImageSequenceListener lMultiFrameListener : mListenerList)
		{
			lMultiFrameListener.newImage(this, pImageSequence);
		}
	}

	@Override
	public void close()
	{
		// TODO: is there anything that we have to release?
		
	}
	
	@Override
	public String toString(){
		return "Sequence Acqusition instance for the camera: " + mAndorCamera;
	}
	
	public AndorCamera getCamera()
	{
		return mAndorCamera;
		
	}

}
