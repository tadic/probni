package wepa.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import wepa.domain.AnimalPicture;
import wepa.repository.AnimalPictureRepository;

@Service
public class AnimalPictureService {
    
    @Autowired
    private AnimalPictureRepository pictureRepo;
    
    public AnimalPicture getById(Long id) {
        return pictureRepo.findOne(id);
    }
    
    /**
     * Gets <code>maxCount</code> latest animal pictures, newest first.
     * 
     * @param maxCount  maximum number to fetch.
     * 
     * @return  a list of latest animal pictures.
     */
    public List<AnimalPicture> getLatest(int maxCount) {
        Pageable limit = new PageRequest(0, maxCount, Direction.DESC, "added");
        return pictureRepo.findAll(limit).getContent();
    }
    
    public AnimalPicture add(MultipartFile file, String description) throws IllegalArgumentException, IOException {
        validate(file);
        AnimalPicture pic = new AnimalPicture();
        pic.setAdded(new Date());
        pic.setDescription(description);
        pic.setContentType(file.getContentType());
        pic.setName(file.getOriginalFilename());
        pic.setImage(file.getBytes());
        return pictureRepo.save(pic);
    }
    
    private void validate(MultipartFile file) throws IllegalArgumentException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        } else if (file.getSize() > (5*1024*1024)) {
            throw new IllegalArgumentException("File size must be less than 5MB");
        }
        if (!file.getContentType().startsWith("image/")){
            throw new IllegalArgumentException("Only image files allowed");
        }
    }
    
}
