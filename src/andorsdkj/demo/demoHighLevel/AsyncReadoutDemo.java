package andorsdkj.demo.demoHighLevel;

import andorsdkj.AndorCamera;
import andorsdkj.AndorSdkJ;
import andorsdkj.AndorSdkJException;
import andorsdkj.ImageBuffer;
import andorsdkj.enums.CycleMode;
import andorsdkj.enums.ReadOutRate;
import andorsdkj.enums.TriggerMode;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AsyncReadoutDemo {
    public static void main(String[] args) {
        try {
            AndorSdkJ lAsdkj = new AndorSdkJ();
            lAsdkj.open();
            AndorCamera lCamera = lAsdkj.openCamera(0);
            lCamera.setOverlapReadoutMode(true);
            lCamera.set16PixelEncoding();
            lCamera.setReadoutRate(ReadOutRate._280_MHz);
            lCamera.allocateAndQueueAlignedBuffers(5);
            lCamera.setTriggeringMode(TriggerMode.INTERNAL);
            lCamera.setExposureTimeInSeconds(0.1);
            lCamera.setCycleMode(CycleMode.CONTINUOUS);

//            System.out.println("overlap mode: " + lCamera.getOverlapReadoutMode());


            lCamera.startAcquisition();

            ExecutorService lExecutor = Executors.newFixedThreadPool(20);


            int lNumTimePoints = 10;

            double t0 = System.nanoTime();

            for (int i = 0; i < lNumTimePoints; i++) {
//                lCamera.SoftwareTrigger();
//                Thread.sleep(100);
                double t1 = System.nanoTime();
                int ind = i;

                System.out.println(String.format("After the %d trigger: %.3f ms.", i, (t1 - t0) * 1e-6));

                ImageBuffer lImageBuffer = lCamera.waitForBuffer(10, TimeUnit.SECONDS);
                lCamera.enqueueBuffer(lImageBuffer);
//                lExecutor.submit(() -> {
//                    try {
//                        System.out.println("--- start with buff " + ind);
//                        ImageBuffer lImageBuffer = lCamera.waitForBuffer(10, TimeUnit.SECONDS);
//                        lCamera.enqueueBuffer(lImageBuffer);
//                        System.out.println("--- processed buff " + ind);
//                    } catch (AndorSdkJException e) {
//                        e.printStackTrace();
//                    }
//                });

//                Thread.sleep(100);
                System.out.println();
            }


            double t2 = System.nanoTime();


            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            lCamera.stopAcquisition();
            lCamera.close();

            lAsdkj.close();
            lExecutor.shutdownNow();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
