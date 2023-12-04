package lk.ijse.dep11.web.api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lecturers")
@CrossOrigin
public class LecturerHttpController {
    @PostMapping
    public void creatNewLecturer(){
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
