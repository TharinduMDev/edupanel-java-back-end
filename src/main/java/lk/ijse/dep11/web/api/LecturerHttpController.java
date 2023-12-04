package lk.ijse.dep11.web.api;

import lk.ijse.dep11.web.TO.request.LecturerRequestTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/lecturers")
@CrossOrigin
public class LecturerHttpController {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = "multipart/form-data",produces = "application/json")
    public void creatNewLecturer(@ModelAttribute @Valid LecturerRequestTO lecturer){       //to work this we should register StandardServletMultipartResolver in the WebAppConfig
        System.out.println(lecturer);
        System.out.println("creatNewLecturer()");
    }
    @PatchMapping("/{lecturer-id}")
    public void updateLecturerDetail(){
        System.out.println("updateLecturerDetail()");
    }

    @DeleteMapping("/{lecturer-id}")
    public void deleteLecturer(){
        System.out.println("deleteLecturer()");
    }

    @GetMapping
    public void getAllLecturers(){

    }

}
