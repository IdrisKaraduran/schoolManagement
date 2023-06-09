package com.schoolmanagement.service;


import com.schoolmanagement.entity.concretes.Dean;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.Response.DeanResponse;
import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.dto.DeanDto;
import com.schoolmanagement.payload.request.DeanRequest;
import com.schoolmanagement.repository.DeanRepository;
import com.schoolmanagement.utils.CheckParameterUpdateMethod;
import com.schoolmanagement.utils.FieldControl;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ObjectStreamClass;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service//Bu tarz annotaion lar ozellestirilmis component oluyor.
@RequiredArgsConstructor

public class DeanService {

    private final AdminService adminService;
    private final DeanRepository deanRepository;
    private final DeanDto deanDto;

    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final FieldControl fieldControl;



    public ResponseMessage<DeanResponse> save(DeanRequest deanRequest) {

        //Duplicate controlu yapiliyor
//        adminService.checkDuplicate(deanRequest.getUsername(),
//                deanRequest.getSsn(),
//                deanRequest.getPhoneNumber());
        fieldControl.checkDuplicate(deanRequest.getUsername(),
                deanRequest.getSsn(),
                deanRequest.getPhoneNumber());

        //DTO -POJO donusumu
       Dean dean = createDTOForDean(deanRequest);

       //Rol setleniyor
        dean.setUserRole(userRoleService
                .getUserRole(RoleType.MANAGER));
       //Password encode edilerek setlendi.Simdi db ye speichern machen
        dean.setPassword(passwordEncoder.encode(dean.getPassword()));

        //Db ye speichern
        Dean savedDean = deanRepository.save(dean);
        //Bu bir pojo ve icinde password var o yuzden boyle gonderemem
        return ResponseMessage.<DeanResponse>builder()
                .message("Dean kaydedildi")
                .httpStatus(HttpStatus.CREATED)
                .object(createDeanResponse(dean)).build();//yardimci method lazim
    }
    private Dean createDTOForDean(DeanRequest deanRequest){
        return deanDto.dtoDean(deanRequest);
    }

    private DeanResponse createDeanResponse(Dean dean){
        return DeanResponse.builder()
                .userId(dean.getId())
                .username(dean.getUsername())
                .name(dean.getName())
                .surname(dean.getSurname())
                .birthDay(dean.getBirthDay())
                .birthPlace(dean.getBirthPlace())
                .phoneNumber(dean.getPhoneNumber())
                .gender(dean.getGender())
                .ssn(dean.getSsn()).build();


    }

    //Not :UpdateById() ***********************
    public ResponseMessage<DeanResponse>
    update(DeanRequest newDean, Long deanId) {

        Optional<Dean> dean = deanRepository.findById(deanId);
        if(!dean.isPresent()){//Gelen objenin ici bossa demis olduk onu konntrol ettik
                              //Burada isEmpty de kullanilabilir
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE));
        }else if(!CheckParameterUpdateMethod.checkParameter(dean.get(),newDean)){
            //! kullanmamizin  nedeni esit degil mi diye kontrol ettim.Method esit mi diye kontrol ediyor cunku
            //Optional yapinin icindeki nesneyi getir ddemek icin get() methodunu kullandim
            // adminService.checkDuplicate(newDean.getUsername(),newDean.getSsn(),newDean.getPhoneNumber());
            fieldControl.checkDuplicate(newDean.getUsername(),newDean.getSsn(),newDean.getPhoneNumber());
            //Tek parametre farkli ise nolacak bunu postmanda test et.
        }
        //guncellenen yeni bilgiler ile Dean objesini kaydedecegiz.
        Dean updatedDean = createUpdatedDean(newDean,deanId);
       //Encode edilmis halde password setlendi
        updatedDean.setPassword(passwordEncoder.encode(newDean.getPassword()));
        deanRepository.save(updatedDean);

        return ResponseMessage.<DeanResponse>builder()
                .message("Dean Updated Successfully")
                .httpStatus(HttpStatus.OK)
                .object(createDeanResponse(updatedDean))
                .build();
    }
    //DTO POJO donusumu//DTO yu POJO ya cevirecegim ve DB ye gonderecegim.
    private Dean createUpdatedDean(DeanRequest deanRequest,Long managerId){
        return Dean.builder()
                .id(managerId)
                .username(deanRequest.getUsername())
                .ssn(deanRequest.getSsn())
                .name(deanRequest.getName())
                .surname(deanRequest.getSurname())
                .birthDay(deanRequest.getBirthDay())
                .birthPlace(deanRequest.getBirthPlace())
                .gender(deanRequest.getGender())
                //Burada password u setlemedik cunku encode edilecek encode ederken setlerim dedik
                .phoneNumber(deanRequest.getPhoneNumber())
                .userRole(userRoleService.getUserRole(RoleType.MANAGER)).build();
    }

    //Not Delete methodu
    public ResponseMessage<?> deleteDean(Long deanId) {

        checkDeanExists(deanId);

//      Optional<Dean> dean = deanRepository.findById(deanId);
//        if(!dean.isPresent()){//Gelen objenin ici bossa demis olduk onu konntrol ettik
//            //Burada isEmpty de kullanilabilir
//            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE));
//        }
        deanRepository.deleteById(deanId);
        return ResponseMessage.builder()
                .message("Dean deleted")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    //Not Id ye gore Dean i getir

    public ResponseMessage<DeanResponse> getDeanById(Long deanId) {

        Optional<Dean> dean = deanRepository.findById(deanId);
        if(!dean.isPresent()){//Gelen objenin ici bossa demis olduk onu konntrol ettik
            //Burada isEmpty de kullanilabilir
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE));
        }

        return ResponseMessage.<DeanResponse>builder()
                .message("Dean Successfully found")
                .httpStatus(HttpStatus.OK)
                .object(createDeanResponse(dean.get()))
                .build();//Frontend de talep vardi o yuzden boyle yazdik
    }


    public List<DeanResponse> getAllDean() {

         return deanRepository.findAll()
                 .stream()
                 .map(this::createDeanResponse)
                 .collect(Collectors.toList());
    }


    //search
    public Page<DeanResponse> search(int page, int size, String sort, String type) {
        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());
        if(Objects.equals(type,"desc")){
            pageable = PageRequest.of(page,size,Sort.by(sort).descending());
        }
        return deanRepository.findAll(pageable).
                map(this::createDeanResponse);
    }

//Tekrarlanan kod blogu icin yazilan method
    private void checkDeanExists(Long deanId){

        Optional<Dean> dean = deanRepository.findById(deanId);
        if(!dean.isPresent()){//Gelen objenin ici bossa demis olduk onu konntrol ettik
            //Burada isEmpty de kullanilabilir
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE));
        }
    }




























}
