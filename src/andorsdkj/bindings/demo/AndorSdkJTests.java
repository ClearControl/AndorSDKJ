package andorsdkj.bindings.demo;

import static org.junit.Assert.assertTrue;

import org.bridj.Pointer;
import org.bridj.demangling.Demangler.PointerTypeRef;
import org.junit.Test;

import andorsdkj.bindings.AtcoreLibrary;

public class AndorSdkJTests {

	@Test
	public void testLibraryInstanciation() {
		int lReturnCode = AtcoreLibrary.AT_InitialiseLibrary();
		System.out.println(lReturnCode);
		assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
	}

	@Test
	public void testLibraryAndCountDevices() throws InterruptedException {
		int lReturnCode = AtcoreLibrary.AT_InitialiseLibrary();
		System.out.println("return code="+lReturnCode);
		assertTrue(lReturnCode == AtcoreLibrary.AT_SUCCESS);
		
		Thread.sleep(1000);

		// AT_64 iNumberDevices = 0;
		// AtcoreLibrary.AT_GetInt

		Pointer<Character> lDeviceCountName = Pointer.pointerToWideCString("Device Count");
		Pointer<Long> lNumberDevices = Pointer.allocateLong();

		lReturnCode = AtcoreLibrary.AT_GetInt(AtcoreLibrary.AT_HANDLE_SYSTEM, lDeviceCountName, lNumberDevices);

		System.out.println("return code="+lReturnCode);
		System.out.println("nb devices="+lNumberDevices.getLong());
		
		//assertTrue(lNumberDevices.getLong()>0);

		lDeviceCountName.release();
		lNumberDevices.release();
		
		
		Pointer<Integer> Hndl = Pointer.allocateInt();
		lReturnCode = AtcoreLibrary.AT_Open(0, Hndl);
		System.out.println("return code="+lReturnCode);
		
		
		lReturnCode = AtcoreLibrary.AT_FinaliseLibrary();
		System.out.println("return code="+lReturnCode);
	}

	
}
