package andorsdkj;

public class AndorSdkJException extends Exception
{

	private static final long serialVersionUID = 1L;
	private int mErrorCode = -1;

	public AndorSdkJException(String pErrorMessage)
	{
		super(pErrorMessage);
	}
	
	public AndorSdkJException(String pErrorMessage, int pErrorCode)
	{
		super(pErrorMessage+" (code="+pErrorCode+")");
		this.mErrorCode = pErrorCode;
	}
		
}
