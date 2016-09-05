package andorsdkj.sequence;

import org.bridj.Pointer;

import andorsdkj.ImageBuffer;

public class ImageSequence {

	private ImageBuffer[] mSequence;
	private volatile long mWidth, mHeight, mDepth;
	private volatile int mImageSizeInBytes;

	/**
	 * ImageSequence constructor with no predefined memory location. The space
	 * to store the image sequence with the specified parameters is allocated in
	 * the constructor.
	 * 
	 * @param pImageSizeinBytes
	 *            - full size of a single image (with paddings, usually
	 *            requested from the camera)
	 * @param pHeight
	 *            - actual height, number of lines
	 * @param pWidth
	 *            - width with padding
	 * @param pDepth
	 *            - number of images in the sequence
	 */
	public ImageSequence(int pImageSizeinBytes, long pWidth, long pHeight, long pDepth) {
		super();

		// initializing dimensions
		this.mDepth = pDepth;
		this.mHeight = pHeight;
		this.mWidth = pWidth;

		// initializing the full size of a frame
		this.mImageSizeInBytes = pImageSizeinBytes;

		// creating an array of buffers to store the coming frames
		this.mSequence = new ImageBuffer[(int) pDepth];

		// allocating off-heap memory to store the image
		Pointer<Byte> arrBegin = Pointer.allocateBytes(pDepth * mImageSizeInBytes + 8);

		// alligning memory pointer
		while (arrBegin.getPeer() % 8 != 0)
			arrBegin = arrBegin.offset(1);

		// initializing pointers for every buffer
		for (int i = 0; i < mSequence.length; i++) {
			mSequence[i] = new ImageBuffer(arrBegin.offset(i * mImageSizeInBytes), mImageSizeInBytes);
		}
	}

	/**
	 * ImageSequence constructor with a predefined memory location. Asks for a
	 * Pointer<Byte> parameter and uses this memory to store the images.
	 * 
	 * @param pImageSizeinBytes
	 *            - full size of a single image (with paddings, usually
	 *            requested from the camera)
	 * @param pHeight
	 *            - actual height, number of lines
	 * @param pWidth
	 *            - width with padding
	 * @param pDepth
	 *            - number of images in the sequence
	 * @param pBufferPointer
	 */

	public ImageSequence(int pImageSizeinBytes, long pHeight, long pWidth, long pDepth, Pointer<Byte> pBufferPointer) {
		super();

		if (pBufferPointer.getValidBytes() < pDepth * pImageSizeinBytes) {
			throw new java.lang.IllegalArgumentException("Provided pointer doesn't have enough valid bytes to store the image sequence.");
		}

		// initializing dimension
		this.mDepth = pDepth;
		this.mWidth = pWidth;
		this.mHeight = pHeight;

		// initializing the full size of a frame
		this.mImageSizeInBytes = pImageSizeinBytes;

		// creating an array of buffers to store the coming frames
		this.mSequence = new ImageBuffer[(int) pDepth];

		// alligninig memory
		while (pBufferPointer.getPeer() % 8 != 0)
			pBufferPointer = pBufferPointer.offset(1);

		// initializing pointers for every buffer
		for (int i = 0; i < mSequence.length; i++) {
			mSequence[i] = new ImageBuffer(pBufferPointer.offset(i * mImageSizeInBytes), mImageSizeInBytes);
		}
	}

	public int getImageSizeInBytes() {
		return mImageSizeInBytes;
	}

	public ImageBuffer[] getImageBufferArray() {
		return this.mSequence;
	}

	public long getWidth() {
		return mWidth;
	}

	public long getHeight() {
		return mHeight;
	}

	public long getDepth() {
		return mDepth;
	}

	@Override
	public String toString() {
		return "ImageSequence h=" + mHeight + ", w=" + mWidth + ", d=" + mDepth + ", ImSizeInBytes=" + mImageSizeInBytes;
	}
}
