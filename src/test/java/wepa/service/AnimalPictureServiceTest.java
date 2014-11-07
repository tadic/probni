package wepa.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import wepa.Application;
import wepa.domain.AnimalPicture;
import wepa.repository.AnimalPictureRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class AnimalPictureServiceTest {
    
    @Autowired
    private AnimalPictureRepository animalPictureRepository;
    
    @Autowired
    private AnimalPictureService animalPictureService;
    
    @Test
    public void getLatestShouldReturnEmptyListWhenNoPicturesFound() {
        animalPictureRepository.deleteAllInBatch();
        List<AnimalPicture> latest = animalPictureService.getLatest(10);
        assertNotNull(latest);
        assertTrue(latest.isEmpty());
    }
    
    @Test
    public void getLatestShouldReturnNonEmptyListWhenPicturesFound() {
        animalPictureRepository.deleteAllInBatch();
        createPicture();
        List<AnimalPicture> latest = animalPictureService.getLatest(10);
        assertNotNull(latest);
        assertEquals(1, latest.size());
    }
    
    @Test
    public void getLatestShouldReturnListSortedByAddedDate() throws InterruptedException {
        animalPictureRepository.deleteAllInBatch();
        AnimalPicture first = createPicture();
        Thread.sleep(15); // should be long enough for most modern OS/HW clocks
        AnimalPicture second = createPicture();
        assertTrue(first.getAdded().before(second.getAdded()));
        List<AnimalPicture> latest = animalPictureService.getLatest(10);
        assertNotNull(latest);
        assertEquals(2, latest.size());
        assertEquals(second, latest.get(0));
        assertEquals(first, latest.get(1));
    }
    
    private AnimalPicture createPicture() {
        // TODO: get columns as parameters and set them, when DB level constraints require them
        AnimalPicture picture = new AnimalPicture();
        animalPictureRepository.save(picture);
        return picture;
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void addShouldDisallowNonImageTypes() throws IOException {
        MultipartFile pictureFile = new MockMultipartFile("foo", "bar", "not/image", new byte [10]);
        animalPictureService.add(pictureFile, "not an image");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void addShouldDisallowEmptyImages() throws IOException {
        MultipartFile pictureFile = new MockMultipartFile("foo", "bar", "image/jpeg", new byte [0]);
        animalPictureService.add(pictureFile, "empty image");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void addShouldDisallowBigImages() throws IOException {
        MultipartFile pictureFile = new MockMultipartFile("foo", "bar", "image/jpeg", new byte [6*1024*1024]);
        animalPictureService.add(pictureFile, "big image");
    }
    
    @Test
    public void addShouldAllowValidImage() throws IOException {
        MultipartFile pictureFile = new MockMultipartFile("foo", "bar", "image/jpeg", new byte [10]);
        AnimalPicture picture = animalPictureService.add(pictureFile, "valid image");
        assertNotNull(picture);
    }
}
