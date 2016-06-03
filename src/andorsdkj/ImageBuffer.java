package andorsdkj;

import org.bridj.Pointer;

public class ImageBuffer
{

	private Pointer<Byte> mPointer;
	private int mImageSizeInBytes;
	
	//TODO: add metadata parsing and bla bla..
	
	//TODO: keep two fileds for width and height (from meta data) + the additional 12 pixels for the width,
	// Question: is it always 12 pixels for all ROI (x,y,width,height)

	public ImageBuffer(Pointer<Byte> pPointer, int pImageSizeInBytes)
	{
		super();
		this.mPointer = pPointer;
		this.mImageSizeInBytes = pImageSizeInBytes;
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
	
	

}
