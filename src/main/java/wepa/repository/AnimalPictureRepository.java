package wepa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wepa.domain.AnimalPicture;

public interface AnimalPictureRepository extends JpaRepository<AnimalPicture, Long> {

}
