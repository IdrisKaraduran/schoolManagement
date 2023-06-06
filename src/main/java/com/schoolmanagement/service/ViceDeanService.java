package com.schoolmanagement.service;

import com.schoolmanagement.config.CreateObjectBean;
import com.schoolmanagement.entity.concretes.ViceDean;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.Response.ViceDeanResponse;
import com.schoolmanagement.payload.dto.ViceDeanDto;
import com.schoolmanagement.payload.request.ViceDeanRequest;
import com.schoolmanagement.repository.ViceDeanRepository;
import com.schoolmanagement.utils.CheckParameterUpdateMethod;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViceDeanService {
    private final ViceDeanRepository viceDeanRepository;
    private final AdminService adminService;
    private final ViceDeanDto viceDeanDto;

    private final UserRoleService userRoleService;

    private final PasswordEncoder passwordEncoder;


    public ResponseMessage<ViceDeanResponse> save(ViceDeanRequest viceDeanRequest) {
             adminService.checkDuplicate(viceDeanRequest.getUsername(),
                     viceDeanRequest.getSsn(),viceDeanRequest.getPhoneNumber());

          //Dto -Pojo donusumu
         ViceDean viceDean = createPojoFromDTO(viceDeanRequest);
          //Rol eklenecek ve Password encode edilecek.
          viceDean.setUserRole(userRoleService.getUserRole(RoleType.ASSISTANTMANAGER));
          viceDean.setPassword(passwordEncoder.encode(viceDeanRequest.getPassword()));
          viceDeanRepository.save(viceDean);
          //Response nesnesi olusturulacak
        return ResponseMessage.<ViceDeanResponse>builder()
                .message("ViceDean Saved")
                .httpStatus(HttpStatus.CREATED)
                .object(createViceDeanResponse(viceDean))
                .build();

    }

    private ViceDean createPojoFromDTO(ViceDeanRequest viceDeanRequest){
        return viceDeanDto.dtoViceDean(viceDeanRequest);
    }
    private ViceDeanResponse createViceDeanResponse(ViceDean viceDean){
        return  ViceDeanResponse.builder()
                .userId(viceDean.getId())
                .username(viceDean.getUsername())
                .name(viceDean.getName())
                .ssn(viceDean.getSsn())
                .birthDay(viceDean.getBirthDay())
                .birthPlace(viceDean.getBirthPlace())
                .gender(viceDean.getGender())
                .phoneNumber(viceDean.getPhoneNumber())
                .surname(viceDean.getSurname())
                .build();
    }


    public ResponseMessage<ViceDeanResponse> update(ViceDeanRequest newViceDean, Long managerId) {
         Optional<ViceDean> viceDean = viceDeanRepository.findById(managerId);

         if(viceDean.isEmpty()){
             throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE,managerId));
         }else if (!CheckParameterUpdateMethod.checkParameter(viceDean.get(),newViceDean)){
             adminService.checkDuplicate(newViceDean.getUsername(),newViceDean.getSsn(),newViceDean.getPhoneNumber());
         }

         //Dto Pojo donusumu
        ViceDean updatedViceDean = createUpdateViceDean(newViceDean,managerId);

         //Role ve encode
        updatedViceDean.setPassword(passwordEncoder.encode(newViceDean.getPassword()));

        updatedViceDean.setUserRole(userRoleService.getUserRole(RoleType.ASSISTANTMANAGER));

        viceDeanRepository.save(updatedViceDean);

        return ResponseMessage.<ViceDeanResponse>builder()
                .message("ViceDean Updated")
                .httpStatus(HttpStatus.CREATED)
                .object(createViceDeanResponse(updatedViceDean))
                .build();
    }
    private ViceDean createUpdateViceDean(ViceDeanRequest viceDeanRequest,Long managerId){

        return ViceDean.builder()
                .id(managerId)
                .username(viceDeanRequest.getUsername())
                .ssn(viceDeanRequest.getSsn())
                .name(viceDeanRequest.getName())
                .surname(viceDeanRequest.getSurname())
                .birthPlace(viceDeanRequest.getBirthPlace())
                .birthDay(viceDeanRequest.getBirthDay())
                .phoneNumber(viceDeanRequest.getPhoneNumber())
                .gender(viceDeanRequest.getGender())
                .build();
    }


    public ResponseMessage<?> deleteViceDean(Long managerId) {
        Optional<ViceDean> viceDean = viceDeanRepository.findById(managerId);

        if(viceDean.isEmpty()){
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE,managerId));
        }
        viceDeanRepository.deleteById(managerId);

        return ResponseMessage.builder()
                .message("ViceDean Deleted")
                .httpStatus(HttpStatus.OK)
                .build();
    }


    public ResponseMessage<ViceDeanResponse> getViceDeanById(Long managerId) {
        Optional<ViceDean> viceDean = viceDeanRepository.findById(managerId);

        if(viceDean.isEmpty()){
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE,managerId));
        }

        return ResponseMessage.<ViceDeanResponse>builder()
                .message("ViceDean succesfully")
                .httpStatus(HttpStatus.OK)
                .object(createViceDeanResponse(viceDean.get()))
                .build();

    }


    public List<ViceDeanResponse> getAllViceDean() {

      return  viceDeanRepository.findAll().stream()
              .map(this::createViceDeanResponse)
              .collect(Collectors.toList());
    }


    public Page<ViceDeanResponse> getAllWithPage(int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());
        if(Objects.equals(type,"desc")){
            pageable =PageRequest.of(page,size,Sort.by(sort).descending());
        }
        return  viceDeanRepository.findAll(pageable).map(this::createViceDeanResponse);

    }






















}
