package hello;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.ximgproc.Ximgproc;

import java.io.*;

public class TaggedRectangle extends Rectangle implements Serializable, Externalizable {
    private static final long serialVersionUID = -7914233131412062873L;

    public String tag = "";
    public int tagI = 0;

    public TaggedRectangle() {
    }

    public TaggedRectangle(double width, double height) {
        super(width, height);
    }

    public TaggedRectangle(double width, double height, Paint fill) {
        super(width, height, fill);
    }

    public TaggedRectangle(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    public String toString() {
        return "<" + tag + "> (" + this.xProperty().get() + "," + this.yProperty().get() + ") ["
                + this.widthProperty().get() + "x" + this.heightProperty().get() + "]";
    }

    public String toYolo(File image) {
        Size s = Imgcodecs.imread(image.getAbsolutePath()).size();
//        return tagI + " " + 8*this.xProperty().intValue() + " " + 8*this.yProperty().intValue() + " "
//                + 8*this.widthProperty().intValue() + " " + 8*this.heightProperty().intValue();
        return tagI + " " + this.xProperty().intValue()/s.width + " " + this.yProperty().intValue()/s.height + " "
                + this.widthProperty().intValue()/s.width + " " + this.heightProperty().intValue()/s.height;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(tag);
        out.writeDouble(this.getX());
        out.writeDouble(this.getY());
        out.writeDouble(this.getWidth());
        out.writeDouble(this.getHeight());
        // out.writeObject(this.getStroke());
        out.writeDouble(this.getStrokeWidth());
        out.writeDouble(this.getOpacity());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        tag = in.readUTF();
        this.setX(in.readDouble());
        this.setY(in.readDouble());
        this.setWidth(in.readDouble());
        this.setHeight(in.readDouble());
        this.setStroke(Color.GREEN);
        this.setStrokeWidth(in.readDouble());
        this.setOpacity(in.readDouble());
    }
}
