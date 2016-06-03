package andorsdkj.live;

import andorsdkj.ImageBuffer;

public interface SingleFrameListener
{
	void newImage(LiveAcquisition pLiveAcquisition, ImageBuffer pImageBuffer);
}
