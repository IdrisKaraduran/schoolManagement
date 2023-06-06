package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.Admin;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.payload.Response.AdminResponse;
import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.request.AdminRequest;

import com.schoolmanagement.repository.*;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/*
@Entity ile annote :bangbang: edilmeyen :bangbang: A isminde bir sinifim olsun ve @Entity ile annote ettigim B sinifini A sinifindan extend edersem , Database deki B tablosunda A sinifina ait field lar gozukur mu ?? cevap hayir ise bunun cozumu nedir ???
 @MappedSuperclass
 @SuperBuilder --> ilgili sinifin field'larini bu classdan türetilen siniflara aktarirken (sadece java tarafinda)
@MappedSuperclass --> annotation'u da db de table olusturmamasina ragmen türettigi entitylere field'larini aktariyor ve türetilen entity siniflarinin db de kolonlarinin olusmasini sagliyor
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;
    private final ViceDeanRepository viceDeanRepository;
    private final DeanRepository deanRepository;
    private final TeacherRepository teacherRepository;
    private final GuestUserRepository guestUserRepository;
    private final UserRoleService userRoleService;

    private final PasswordEncoder passwordEncoder;

    // Not: save()  *******************************************************
    public ResponseMessage save(AdminRequest request) {

        // !!! Girilen username - ssn- phoneNumber unique mi kontrolu
        //6 tane repository ye gidip kontrol edecem.
        checkDuplicate(request.getUsername(),request.getSsn(),request.getPhoneNumber());

        //Admin nesnesi builder ile olusturalim
        Admin admin = createAdminForSave(request);
        admin.setBuilt_in(false);

        if(Objects.equals(request.getUsername(),"Admin")) admin.setBuilt_in(true);

        //Admin Rolu veriliyor.
        admin.setUserRole(userRoleService.getUserRole(RoleType.ADMIN));

        //Not:password plain text --> encode
        //Password encode ediliyor.
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));


        Admin savedDate= adminRepository.save(admin);

        return ResponseMessage.<AdminResponse>builder()
                .message("Admin saved")
                .httpStatus(HttpStatus.CREATED)
                .object(createResponse(savedDate))//Pojo dto
                .build();




    }
//Donen deger niye void varsa exception firlatacak o yuzden void olacak.
    public void checkDuplicate(String username, String ssn, String phone){
        if(adminRepository.existsByUsername(username) ||
                deanRepository.existsByUsername(username) ||
                studentRepository.existsByUsername(username) ||
                teacherRepository.existsByUsername(username) ||
                viceDeanRepository.existsByUsername(username) ||
                guestUserRepository.existsByUsername(username)) {
            throw new ConflictException(String.format(Messages.ALREADY_REGISTER_MESSAGE_USERNAME, username));
        } else if(adminRepository.existsBySsn(ssn) ||
                deanRepository.existsBySsn(ssn) ||
                studentRepository.existsBySsn(ssn) ||
                teacherRepository.existsBySsn(ssn) ||
                viceDeanRepository.existsBySsn(ssn) ||
                guestUserRepository.existsBySsn(ssn)) {
            throw new ConflictException(String.format(Messages.ALREADY_REGISTER_MESSAGE_SSN, ssn));
        } else if (adminRepository.existsByPhoneNumber(phone) ||
                deanRepository.existsByPhoneNumber(phone) ||
                studentRepository.existsByPhoneNumber(phone) ||
                teacherRepository.existsByPhoneNumber(phone) ||
                viceDeanRepository.existsByPhoneNumber(phone) ||
                guestUserRepository.existsByPhoneNumber(phone)) {
            throw new ConflictException(String.format(Messages.ALREADY_REGISTER_MESSAGE_PHONE_NUMBER, phone));
        }
        //Dortlu besli olursa ne yapacagim.
        //Varargs ile yap

    }

    protected Admin createAdminForSave(AdminRequest request){
        return Admin.builder().
                username(request.getUsername()).
                name(request.getName()).
                surname(request.getSurname()).
                password(request.getPassword()).
                ssn(request.getSsn()).
                birthDay(request.getBirthDay()).
                birthPlace(request.getBirthPlace()).
                phoneNumber(request.getPhoneNumber()).
                gender(request.getGender()).build();
    }

    private AdminResponse createResponse(Admin  admin){

        return AdminResponse.builder()
                .userId(admin.getId())
                .username(admin.getUsername())
                .name(admin.getName())
                .surname(admin.getSurname())
                .phoneNumber(admin.getPhoneNumber())
                .gender(admin.getGender())
                .ssn(admin.getSsn())
                .build();
    }


    public Page<Admin> getAllAdmin(Pageable pageable) {
        return adminRepository.findAll(pageable);
    }

    public String deleteAdmin(Long id) {

        Optional<Admin> admin = adminRepository.findById(id);
        //Optinal olmasinin nedeni null pointer almayalim diye yapiyoruz.
        //orELseThrow lada handle edebilirim.
        if (admin.isPresent() && admin.get().isBuilt_in()) { //Admin nesnesinin dolu olarak gelip gelmedigini kontrol ediyor
            throw new ConflictException(Messages.NOT_PERMITTED_METHOD_MESSAGE);
        }
        if (admin.isPresent()) {
            adminRepository.deleteById(id);

            return "Admin is deleted successfully";
        }

        return Messages.NOT_FOUND_USER_MESSAGE;


    }

    public long countAllAdmin() {

        return adminRepository.count();//Long donecegi icin Long
    }
}