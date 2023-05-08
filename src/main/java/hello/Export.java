package hello;

import javafx.scene.control.ListView;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class Export {

    private static void deleteDirectoryStream(Path path) throws IOException {
        Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    }


    private static void log(String msg) {
        System.out.println(msg);
    }

    /**
     * @throws IOException
     */
    public static void yolo(File workDirectoy, HashMap<File, ArrayList<TaggedRectangle>> store, ListView<String> tagListView, List<File> images) throws Exception {
        String _yolo = workDirectoy.getPath() + File.separator + "custom_dataset";

        createYoloFolder(_yolo);
        writeTrainShV6(_yolo);
        writeDatasetYaml(_yolo, tagListView);

        log("Shuffling tagged images");
        ArrayList trainingImage = new ArrayList<>(images);
        Collections.shuffle(trainingImage);

        int ind = (int) (trainingImage.size() * 0.8);
        List<File> trainImages = trainingImage.subList(0, ind);
        List<File> valImages = trainingImage.subList(ind, trainingImage.size());

        writeImagesForTarget(store, _yolo, "train", trainImages);
        writeImagesForTarget(store, _yolo, "val", valImages);

        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(new File(_yolo));
        }
    }

    private static void writeImagesForTarget(HashMap<File, ArrayList<TaggedRectangle>> store, String _yolo, String target, List<File> targetImages) throws IOException {
        File baseFolderImages = new File(_yolo + File.separator + "images" + File.separator + target);
        File baseFolderLabels = new File(_yolo + File.separator + "labels" + File.separator + target);
        baseFolderImages.mkdirs();
        baseFolderLabels.mkdirs();

        for(int index = 0; index< targetImages.size(); index++) {

            File i = targetImages.get(index);

            ArrayList<TaggedRectangle> rects = store.get(i);
            if (rects.size() == 0)
                continue;

            File j = new File(baseFolderImages.getAbsolutePath() + File.separator + i.getName());
            Files.copy(i.toPath(), j.toPath());

            String txtj = _yolo + File.separator + "labels" + File.separator + target + File.separator + j.getName().substring(0, j.getName().lastIndexOf(".")) + ".txt";
            FileWriter jtxt = new FileWriter(txtj);
            for (TaggedRectangle r : rects) {
                jtxt.write(r.toYolo(j) + "\n");
            }
            jtxt.close();
        }
        log("Image folder "+ target +" completed");
    }

    private static void writeDatasetYaml(String _yolo, ListView<String> tagListView) throws IOException {
        FileWriter output = new FileWriter(_yolo + File.separator + "dataset.yml");
        String base = "# Please insure that your custom_dataset are put in same parent dir with YOLOv6_DIR\n" +
                "train: ../custom_dataset/images/train # train images\n" +
                "val: ../custom_dataset/images/val # val images\n" +
                "#test: ../custom_dataset/images/test # test images (optional)\n" +
                "\n" +
                "# whether it is coco dataset, only coco dataset should be set to True.\n" +
                "is_coco: False\n" +
                "\n" +
                "# Classes\n" +
                "nc: COUNT  # number of classes\n" +
                "names: [CLASSES]  # class names\n";
        List<String> tags = tagListView.getItems().stream().map(e -> {
            return "'" + e + "'";
        }).toList();
        String tagNames = String.join(",",tags);
        String content = base.replaceAll("CLASSES", tagNames).replaceAll("COUNT", ""+tags.size());
        output.write(content);
        output.close();
    }

    private static void writeTrainShV6(String _yolo) throws Exception {
        FileWriter train_sh = new FileWriter(_yolo + File.separator + "trainv6.sh");
        String train = "python tools/train.py --batch 32 --conf configs/yolov6s_finetune.py --data ../custom_data/dataset.yaml --fuse_ab --device cpu";
        train_sh.write(train);
        train_sh.close();
        log("generated train.sh");
    }

    private static void createYoloFolder(String _yolo) throws IOException {
        try {
            deleteDirectoryStream(Path.of(_yolo));
        } catch(Exception e) {

        }
        new File(_yolo).mkdirs();
        log("Yolo Dir Created");
    }

//
//
//    private static void writeTrainSh(String _yolo) throws IOException {
//        // script train.sh
//        FileWriter train_sh = new FileWriter(_yolo + File.separator + "train.sh");
//        train_sh.write("wget -N https://pjreddie.com/media/files/darknet53.conv.74\n");
//        train_sh.write("darknet detector train obj_data.txt yolo.cfg darknet53.conv.74\n");
//        train_sh.close();
//        log("generated train.sh");
//    }

//    private static void writeBackFolder(String _yolo) {
//        // backup folder
//        File weights = new File(_yolo + File.separator + "weights");
//        weights.mkdir();
//        log("generated weights folder");
//    }
//
//    private static void writeValidTxt(String _yolo, String file, List<File> valImages) throws IOException {
//        // valid.txt
//        FileWriter valid_txt = new FileWriter(_yolo + File.separator + file);
//        for (File f : valImages) {
//            valid_txt.append(f.getPath().replaceAll(_yolo + "/", "") + "\n");
//        }
//        valid_txt.close();
//        log("Write valid txt");
//    }
//
//    private static void writeTrainTxt(String _yolo, List<File> trainImages) throws IOException {
//        // train.txt
//        FileWriter train_txt = new FileWriter(_yolo + File.separator + "train.txt");
//        for (File f : trainImages) {
//            train_txt.append(f.getPath().replaceAll(_yolo + "/", "") + "\n");
//        }
//        train_txt.close();
//        log("generated train.txt");
//    }

//    private static void writeYoloCfg(File yolo) throws IOException {
//        // yolo.cfg
//        URL yoloCfg = Export.class.getResource("/yolo.cfg");
//        Path path = Paths.get(yoloCfg.getPath());
//        Charset charset = StandardCharsets.UTF_8;
//        String content = Files.readString(path, charset);
//        /*
//        CUSTOMIZE YOLO CONFIG
//         */
//        content = content.replaceAll("classes=2", "classes=" + classCount);
//        content = content.replaceAll("filters=18", "filters=" + 3 * (5 + classCount));
//        /*
//        CUSTOMIZE YOLO CONFIG
//         */
//        Files.write(Paths.get(yolo + File.separator + "yolo.cfg"), content.getBytes(charset));
//        log("generated yolo.cfg");
//    }
//
//    private static void writeObjNames(ListView<String> tagListView, String _yolo) throws IOException {
//        FileWriter names = new FileWriter(_yolo + File.separator + "obj_names.txt");
//        for (String str : tagListView.getItems()) {
//            names.write(str + "\n");
//        }
//        names.close();
//        log("generated obj_names.txt");
//    }
//
//    private static void writeObjData(ListView<String> tagListView, String _yolo) throws IOException {
//        FileWriter data = new FileWriter(_yolo + File.separator + "obj_data.txt");
//        data.write("classes\t= " + tagListView.getItems().size() + "\n");
//        data.write("train\t= train.txt" + "\n");
//        data.write("valid\t= test.txt" + "\n");
//        data.write("names\t= obj_names.txt" + "\n");
//        data.write("backup\t= weights" + "\n");
//        data.close();
//        log("generated obj_data.txt");
//    }
}
