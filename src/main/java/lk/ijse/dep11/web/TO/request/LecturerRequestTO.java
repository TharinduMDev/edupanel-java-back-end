package lk.ijse.dep11.web.TO.request;

import lk.ijse.dep11.web.validation.LecturerImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LecturerRequestTO {
    @NotBlank(message = "Name Can not be empty!")
    @Pattern(regexp = "^[A-Za-z ]+$",message = "Invalid Name : {value}!")
    private String name;
    @NotBlank(message = "Designation can bot be empty!")
    @Length(min = 2,message = "Invalid Designation : {value}!")
    private String designation;
    @NotBlank(message = "Qualification can bot be empty!")
    @Length(min = 2,message = "Invalid qualification : {value}!")
    private String qualification;
    @NotBlank(message = "Type can not be Empty!")
    @Pattern(regexp = "^(full-time|part-time)$",flags = Pattern.Flag.CASE_INSENSITIVE,message = "Invalid Type!")
    private String type;
    @LecturerImage(maximumFileSize = 200*1024)
    private MultipartFile picture;
    @Pattern(regexp = "^http[s]?://.+$",message = "Invalid linkedin url!")
    private String linkedin;
}
