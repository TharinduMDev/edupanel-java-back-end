package lk.ijse.dep11.web.api;

import lk.ijse.dep11.web.TO.request.LecturerRequestTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import javax.validation.Valid;
import java.sql.*;

@RestController
@RequestMapping("/api/v1/lecturers")
@CrossOrigin
public class LecturerHttpController {

    @Autowired
    private DataSource pool;
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = "multipart/form-data",produces = "application/json")
    public void creatNewLecturer(@ModelAttribute @Valid LecturerRequestTO lecturer){       //to work this we should register StandardServletMultipartResolver in the WebAppConfig
        try (Connection connection = pool.getConnection()) {
            connection.setAutoCommit(false);
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO lecturer (name, designation, qualifications, linkedin) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, lecturer.getName());
                preparedStatement.setString(2, lecturer.getDesignation());
                preparedStatement.setString(3, lecturer.getQualification());

                preparedStatement.setString(4, lecturer.getLinkedin());
                preparedStatement.executeUpdate();
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                generatedKeys.next();
                int lecturerId = generatedKeys.getInt(1);
                String picture = lecturerId + "-" + lecturer.getName();
                if (lecturer.getPicture() != null || !lecturer.getPicture().isEmpty()) {
                    PreparedStatement stmUpdateLecturer = connection.prepareStatement("UPDATE lecturer SET picture = ? WHERE id = ?");
                    stmUpdateLecturer.setString(1, picture);
                    stmUpdateLecturer.setInt(2, lecturerId);
                    stmUpdateLecturer.executeUpdate();
                }

                final String table = lecturer.getType().equalsIgnoreCase("full-time") ? "full_time_rank" : "part_time_rank";

                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT `rank` FROM "+ table + " ORDER BY `rank` DESC LIMIT 1");
                int rank;
                if(!resultSet.next()){
                    rank = 1;
                }else{
                    rank = resultSet.getInt("rank") + 1;
                }
                PreparedStatement stmUpdateFullTime = connection.prepareStatement("INSERT INTO "+table+" (lecturer_id, `rank`) VALUES (?,?)");
                stmUpdateFullTime.setInt(1,rank);
                stmUpdateFullTime.setInt(2,rank);
                stmUpdateFullTime.executeUpdate();

                connection.commit();
            }catch (Throwable t){
                connection.rollback();
            }finally {
                connection.setAutoCommit(true);
            }



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
