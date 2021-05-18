package quantum.draw;

import common.draw.GraphGuiUtil;
import common.draw.ShowFrameBase;
import common.events.InitException;

import java.io.IOException;

public final class ShowFrame extends ShowFrameBase {

    public ShowFrame(String[] args) throws IOException, InitException {
        super(args, "Graph Demo", GraphFrame.PO, new QuantumScheduleFactory(), null);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: show [<1 to auto-start>] <graph file>");
            return;
        }
        try {
            new ShowFrame(args);
        } catch (Exception ex) {
            GraphGuiUtil.handleException(null, ex);
        }
    }
}
