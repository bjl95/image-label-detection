import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.stub.GrpcImageAnnotatorStub;
import com.google.cloud.vision.v1.stub.ImageAnnotatorStubSettings;
import com.google.common.flogger.FluentLogger;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static String API_KEY = "AIzaSyBHcPBmwcJWXKqJp2dpHaJ91v9-YJBo3nY";

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    static boolean isExtensionAcceptable(String filename) {
        return filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".jpeg");
    }

    static ImageInfo.Images getImageInfoProto(String dirName) {
        ImageInfo.Images.Builder imagesBuilder = ImageInfo.Images.newBuilder();

        // Iterate all image files in the given directory.
        File dir = new File(dirName);
        for (File file : dir.listFiles()) {
            try {
                String filename = file.getName();
                logger.atInfo().log("Found file '%s'", filename);

                // Filter files by their extension.
                if (!isExtensionAcceptable(filename)) {
                    logger.atInfo().log("File '%s' is filtered out for having invalid extension.", filename);
                    continue;
                }
                Path path = Paths.get(file.getPath());
                BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
                long creationTime = attr.creationTime().toMillis();
                long modifiedTime = attr.lastModifiedTime().toMillis();
                long size = Files.size(path);
                ImageInfo.Images.Image.Builder image = ImageInfo.Images.Image.newBuilder()
                        .setFilename(file.getName())
                        .setSize(size)
                        .setCreatedTime(creationTime)
                        .setModifiedTime(modifiedTime);
                imagesBuilder.addImages(image);

                logger.atInfo().log("Add file '%s', size = %d, creationTime = %d, modifiedTime = %d",
                        filename, size, creationTime, modifiedTime);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return imagesBuilder.build();
    }

    public static Image readImage(Path path) throws IOException {
        byte[] data = Files.readAllBytes(path);
        ByteString imgBytes = ByteString.copyFrom(data);
        Image img = Image.newBuilder().setContent(imgBytes).build();
        return img;
    }

    public static void getImageLabels(String imagePath) {
        try {
            Image img = readImage(Paths.get(imagePath));
            Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
            AnnotateImageRequest request =
                    AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();

            BatchAnnotateImagesRequest batchRequest = BatchAnnotateImagesRequest
                    .newBuilder()
                    .addRequests(request)
                    .build();
            ImageAnnotatorStubSettings settings = ImageAnnotatorStubSettings.newBuilder()
                    .setApiKey(API_KEY)
                    .build();
            GrpcImageAnnotatorStub stub = GrpcImageAnnotatorStub.create(settings);

            BatchAnnotateImagesResponse response = stub.batchAnnotateImagesCallable().call(batchRequest);
            for (int i = 0; i < response.getResponsesCount(); ++i) {
                AnnotateImageResponse res = response.getResponses(i);
                for (int j = 0; j < res.getLabelAnnotationsCount(); ++j) {
                    EntityAnnotation ann = res.getLabelAnnotations(i);
                    logger.atInfo().log("Found label '%s' with score %f\n", ann.getDescription(), ann.getScore());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        if (args.length != 1) {
//            System.out.println("Usage: java Main <filename>");
//            return;
//        }
//
//        String filename = args[0];
        getImageLabels("src/test/resources/images/donut.jpg");
    }
}
