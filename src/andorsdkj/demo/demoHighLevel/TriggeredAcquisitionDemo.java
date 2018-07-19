package andorsdkj.demo.demoHighLevel;

import andorsdkj.AndorCamera;
import andorsdkj.AndorSdkJ;
import andorsdkj.AndorSdkJException;
import andorsdkj.ImageBuffer;
import andorsdkj.enums.CycleMode;
import andorsdkj.enums.ReadOutRate;
import andorsdkj.enums.TriggerMode;

import java.util.concurrent.*;

public class TriggeredAcquisitionDemo {
    public static void main(String[] args) {
        try {
            AndorSdkJ lAsdkj = new AndorSdkJ();
            lAsdkj.open();
            AndorCamera lCamera = lAsdkj.openCamera(0);
            lCamera.setOverlapReadoutMode(true);
            lCamera.set16PixelEncoding();
            lCamera.setReadoutRate(ReadOutRate._280_MHz);
            lCamera.allocateAndQueueAlignedBuffers(5);
            lCamera.setTriggeringMode(TriggerMode.SOFTWARE);
            lCamera.setExposureTimeInSeconds(0.1);
            lCamera.setCycleMode(CycleMode.CONTINUOUS);

            System.out.println("is overlap? - " + lCamera.getOverlapReadoutMode());


            lCamera.startAcquisition();

            ScheduledThreadPoolExecutor lExecutor = new ScheduledThreadPoolExecutor(20);

            int lNumTimePoints = 10;
            double t0 = System.nanoTime();

            double t11;


            Future<?> f = lExecutor.scheduleAtFixedRate(() -> {
                try {



                    lCamera.SoftwareTrigger();


                    double t1 = System.nanoTime();
                    System.out.println(String.format("---> Trigger: %.3f", (t1 - t0) * 1e-6));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 0, 100, TimeUnit.MILLISECONDS);

            System.out.println();


            for (int i = 0; i < lNumTimePoints; i++) {
                int ind = i;


                double t1 = System.nanoTime();

                ImageBuffer lImageBuffer = lCamera.waitForBuffer(10, TimeUnit.SECONDS);

                double t25 = System.nanoTime();


                System.out.println(String.format("Wait for buffer %d took %.3f ms.", i, (t25 - t1) * 1e-6));
                lCamera.enqueueBuffer(lImageBuffer);

                System.out.println();
            }


            double t2 = System.nanoTime();
            f.cancel(false);


            try {
                Thread.sleep(1000);
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
