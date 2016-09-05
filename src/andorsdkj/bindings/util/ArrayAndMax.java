package andorsdkj.bindings.util;
import java.util.Arrays;

public class ArrayAndMax
{
	private final int maxElem;
	private final int[][] array;
	
	public ArrayAndMax(int[][] BufferArray){
		
	// exception handling        
    if (BufferArray == null) {
        throw new java.lang.NullPointerException("Array is null.");
    }
    
    int height = BufferArray.length;
    
    if (height == 0) {
        throw new java.lang.IllegalArgumentException("Array is empty.");
    }
         
    int width = BufferArray[0].length;
    
    if (width == 0) {
        throw new java.lang.IllegalArgumentException("first string of the input array is empty.");
    }
		
		int aux = 0;
		
		this.array = new int[height][width];
		
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				if (BufferArray[i][j] > aux) {
					aux = BufferArray[i][j];
				}
				this.array[i][j] = BufferArray[i][j];
			}
		}
		this.maxElem = aux;
	}

	public int getMax(){
		return maxElem;
	}
	
}
