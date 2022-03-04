import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class MainTest {
    @Test
    public void isExtensionAcceptableTest() {
        assertTrue(Main.isExtensionAcceptable("aa.jpg"));
        assertTrue(Main.isExtensionAcceptable("bcd d.jpeg"));
        assertTrue(Main.isExtensionAcceptable("dog.png"));
        assertFalse(Main.isExtensionAcceptable("aa.txt"));
        assertFalse(Main.isExtensionAcceptable("pig.mp4"));
        assertFalse(Main.isExtensionAcceptable("cat.avi"));
    }

    @Test
    public void ReadingImageTest() {
        ImageInfo.Images images = Main.getImageInfoProto("src/test/resources/images");

        Map<String, Long> sizeMap = new HashMap<>();
        sizeMap.put("image1.jpeg", 100L);
        sizeMap.put("image2.jpg", 200L);
        sizeMap.put("Java-Logo.png", 46239L);

        assertEquals(images.getImagesCount(), 3);
        for (ImageInfo.Images.Image image : images.getImagesList()) {
            Long size = sizeMap.get(image.getFilename());
            assertNotNull(size);
            assertEquals(size.longValue(), image.getSize());
        }
    }

    @Test
    public void WriteProtoBufTest() {
        ImageInfo.Images images = Main.getImageInfoProto("src/test/resources/images");

        Map<String, Long> sizeMap = new HashMap<>();
        sizeMap.put("image1.jpeg", 100L);
        sizeMap.put("image2.jpg", 200L);
        sizeMap.put("Java-Logo.png", 46239L);

        try {
            FileOutputStream output = new FileOutputStream("tmp.data");
            images.writeTo(output);
            output.close();

            FileInputStream input = new FileInputStream("tmp.data");
            ImageInfo.Images readImages = ImageInfo.Images.parseFrom(input);

            assertEquals(readImages.getImagesCount(), 3);
            for (ImageInfo.Images.Image image : readImages.getImagesList()) {
                Long size = sizeMap.get(image.getFilename());
                assertNotNull(size);
                assertEquals(size.longValue(), image.getSize());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
