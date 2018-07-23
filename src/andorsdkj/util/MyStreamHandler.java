package andorsdkj.util;

import java.util.logging.StreamHandler;

public class MyStreamHandler extends StreamHandler{

    public MyStreamHandler() {
        super();
        setOutputStream(System.out);
    }

}
