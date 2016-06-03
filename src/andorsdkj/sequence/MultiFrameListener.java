package andorsdkj.sequence;

import java.util.ArrayList;

import andorsdkj.ImageBuffer;

public interface MultiFrameListener
{
	void newImage(SequenceAcquisition pSequenceAcquisition, ArrayList<ImageBuffer> pImageBuffer);
}
