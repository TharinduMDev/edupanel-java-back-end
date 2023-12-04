package lk.ijse.dep11.web.api;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import lk.ijse.dep11.web.TO.reponse.LecturerResTO;
import lk.ijse.dep11.web.TO.request.LecturerRequestTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import javax.validation.Valid;
import java.io.IOException;
import java.sql.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/lecturers")
@CrossOrigin
public class LecturerHttpController {

    @Autowired
    private DataSource pool;

    @Autowired
    private Bucket bucket;

    private LecturerRequestTO lecturerRequestTO;
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = "multipart/form-data",produces = "application/json")
    public LecturerResTO creatNewLecturer(@ModelAttribute @Valid LecturerRequestTO lecturer){       //to work this we should register StandardServletMultipartResolver in the WebAppConfig
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

                String pictureUrl = null;
                if(lecturer.getPicture() != null && !lecturer.getPicture().isEmpty()){
                    Blob blob = bucket.create(picture, lecturer.getPicture().getInputStream(), lecturer.getPicture().getContentType());
                    pictureUrl = blob.signUrl(1, TimeUnit.DAYS, Storage.SignUrlOption.withV4Signature()).toString();

                }

                connection.commit();
                return new LecturerResTO(lecturerId,lecturer.getName(),lecturer.getDesignation(),lecturer.getQualification(),lecturer.getType(),pictureUrl,lecturer.getLinkedin());
            }catch (Throwable t){
                connection.rollback();
                throw t;
            }finally {
                connection.setAutoCommit(true);
            }



        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    @PatchMapping("/{lecturer-id}")
    public void updateLecturerDetail(){
        System.out.println("updateLecturerDetail()");
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{lecturer-id}")
    public void deleteLecturer( @PathVariable("lecturer-id") int lecturerId){
        try(Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM lecturer WHERE id = ?");
            preparedStatement.setInt(1, lecturerId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(!resultSet.next()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            connection.setAutoCommit(false);
            try {
                PreparedStatement stmIdentify = connection.prepareStatement("SELECT l.id,l.name,l.picture,ftr.`rank` AS ftr,ptr.`rank` AS ptr FROM lecturer l\n" +
                        "    LEFT OUTER JOIN full_time_rank ftr ON l.id = ftr.lecturer_id\n" +
                        "    LEFT OUTER JOIN part_time_rank ptr ON l.id = ptr.lecturer_id\n" +
                        "    WHERE l.id = ?");
                stmIdentify.setInt(1, lecturerId);
                ResultSet rst = stmIdentify.executeQuery();
                rst.next();
                int ftr = rst.getInt("ftr");
                int ptr = rst.getInt("ptr");
                String picture = rst.getString("picture");
                String tableName = ftr > 0 ? "full_time_rank" : "part_time_rank";
                int rank = ftr > 0 ? ftr : ptr ;

                Statement stmDeleteRank = connection.createStatement();
                stmDeleteRank.executeUpdate("DELETE FROM "+tableName+" WHERE rank = "+rank);

                Statement stmShift = connection.createStatement();
                stmShift.executeUpdate("UPDATE "+tableName+" `rank` = `rank`-1 WHERE `rank`> "+rank);

                PreparedStatement stmDeleteLecturer = connection.prepareStatement("DELETE FROM lecturer WHERE id = ?");
                stmDeleteLecturer.setInt(1,lecturerId);
                stmDeleteLecturer.executeUpdate();

                if(picture != null) bucket.get(picture).delete();


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

    @GetMapping
    public void getAllLecturers(){

    }

}
