package andorsdkj;


import org.bridj.Pointer;

public class ImageBuffer
{

	private Pointer<Byte> mPointer;
	private int mImageSizeInBytes;
	private boolean mMetadataEnabled;
	private boolean mTimeStampEnabled;
	private boolean mFrameInfoEnabled;
	private long mTimeStamp;
	
	//TODO: add metadata parsing and bla bla..
	
	//TODO: keep two fileds for width and height (from meta data) + the additional 12 pixels for the width,
	// Question: is it always 12 pixels for all ROI (x,y,width,height)

	public ImageBuffer(Pointer<Byte> pPointer, int pImageSizeInBytes, boolean pMetadataEnabled, boolean pTimeStampEnabled)
	{
		super();
		this.mPointer = pPointer;
		this.mImageSizeInBytes = pImageSizeInBytes;
		this.mMetadataEnabled = pMetadataEnabled;
		this.mTimeStampEnabled = pTimeStampEnabled;

		
		if (mTimeStampEnabled && mMetadataEnabled) {
			Pointer<Byte> pByte = mPointer.offset(mImageSizeInBytes-16);
			long aux = pByte.getLong();
			this.mTimeStamp = aux;
		}
		else{
			this.mTimeStamp = -1;
		}
		
	}
	
	public ImageBuffer(Pointer<Byte> pPointer, int pImageSizeInBytes)
	{
		this(pPointer, pImageSizeInBytes, false, false);
	}
	
	public Pointer<Byte> getPointer()
	{
		return mPointer;
	}

	public int getImageSizeInBytes()
	{
		return mImageSizeInBytes;
	}

	@Override
	public String toString()
	{
		return String.format(	"ImageBuffer [mPointer=%s, mImageSizeInBytes=%s]",
													mPointer,
													mImageSizeInBytes);
	}
	
	public int getStride(){
		if (!mFrameInfoEnabled){
			return -1;
		}
		else{
			return 1;
		}
	}
	
	public String getMetadata(){
		if (!mMetadataEnabled) {
			return "Metadata is not enabled.";
		}
		else{
			String lRetStr = "Metadata parsing:\n";
			Pointer<Byte> pByte = mPointer.offset(mImageSizeInBytes-16);
			long aux = pByte.getLong();
			this.mTimeStamp = aux;
			lRetStr += aux;
			//long lHolder = (pByte.offset(-1).getByte() << 8) + pByte.offset(-2).getByte();
			lRetStr += " TimeStamp: ";
//			for (int i = 0; i < 40; i++)
//			{
//				lRetStr += " " + pByte.offset(-i).getByte();
//			}
//			pByte = pByte.offset(-2);
			return lRetStr;
			
		}
	}
	
	public long getTimestamp(){
		return mTimeStamp;
		}
	
}
