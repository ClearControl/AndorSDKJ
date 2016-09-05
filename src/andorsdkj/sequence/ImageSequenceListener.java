package andorsdkj.sequence;

import java.util.ArrayList;

import andorsdkj.ImageBuffer;

public interface ImageSequenceListener
{
	void newImage(SequenceAcquisition pSequenceAcquisition, ImageSequence pImageSequence);
}
