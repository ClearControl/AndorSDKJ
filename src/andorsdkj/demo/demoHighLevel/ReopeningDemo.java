package andorsdkj.demo.demoHighLevel;

import andorsdkj.AndorCamera;
import andorsdkj.AndorSdkJ;
import andorsdkj.AndorSdkJException;

public class ReopeningDemo {
    public static void main(String[] args) {
        try {
            AndorSdkJ lASDKJ = new AndorSdkJ();
            lASDKJ.open();
            AndorCamera lCamera = lASDKJ.openCamera(0);
            lCamera.close();
//            lASDKJ.close();

            lASDKJ = new AndorSdkJ();
            lASDKJ.open();
            lCamera = lASDKJ.openCamera(0);
//            lCamera.close();
//            lASDKJ.close();
        } catch (AndorSdkJException e) {
            e.printStackTrace();
        }
    }
}
