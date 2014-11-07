package wepa.controllers;

import java.util.UUID;
import javax.tools.FileObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import wepa.Application;
import wepa.domain.AnimalPicture;
import wepa.repository.AnimalPictureRepository;
import wepa.service.AnimalPictureService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class AnimalPictureControllerTest {
    private final String POST_ADDRESS = "/";

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private AnimalPictureService pictureService;
    
    @Autowired
    private AnimalPictureRepository pictureRepo;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    
        
    @Test
    public void addingNonPictureFileReturnsSamePageAndNiceErrorMessage() throws Exception {
        Long sizeBefore = pictureRepo.count();
        String description = UUID.randomUUID().toString().substring(0, 6);
        String imageName = "pdfdocname";
        String content = UUID.randomUUID().toString().substring(0, 6);
        String messageExpected = "Only image files allowed";
        MockMultipartFile multipartFile = new MockMultipartFile("file", imageName, "pdf", content.getBytes());

        MvcResult res = mockMvc.perform(fileUpload(POST_ADDRESS).file(multipartFile).param("description", description))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

       String messageParam = (String) res.getModelAndView().getModel().get("errorMessage");
        
        assertTrue(sizeBefore==pictureRepo.count());
        assertEquals(messageExpected, messageParam);
        
    }
    
    @Test
    public void addingPictureFileSavesPictureCorrectlyAndRedirectsToSamePage() throws Exception {
        Long sizeBefore = pictureRepo.count();
        String description = UUID.randomUUID().toString().substring(0, 6);
        String imageName = UUID.randomUUID().toString().substring(0, 6);
        String content = UUID.randomUUID().toString().substring(0, 6);
        MockMultipartFile multipartFile = new MockMultipartFile("file", imageName, "image/png", content.getBytes());

        mockMvc.perform(fileUpload(POST_ADDRESS).file(multipartFile).param("description", description))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        AnimalPicture picture = pictureService.getById(new Long(1));
        assertEquals(picture.getName(), imageName);
        assertEquals(picture.getDescription(), description);
        MvcResult res = mockMvc.perform(get("/1"))          
                 .andExpect(status().is2xxSuccessful())            
                 .andReturn();
        
         assertEquals(content, res.getResponse().getContentAsString());
         assertEquals( "image/png", res.getResponse().getContentType());
         assertEquals(sizeBefore + 1, pictureRepo.count());
        
    }

}
