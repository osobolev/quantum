package quantum.draw;

import common.draw.ShowFrameBase;
import common.events.InitException;

import java.io.IOException;

public final class ShowFrame extends ShowFrameBase {

    public ShowFrame(String[] args) throws IOException, InitException {
        super(args, "Graph Demo", GraphFrame.PO, new QuantumScheduleFactory());
    }

    public static void main(String[] args) throws Exception {
        new ShowFrame(args);
    }
}
