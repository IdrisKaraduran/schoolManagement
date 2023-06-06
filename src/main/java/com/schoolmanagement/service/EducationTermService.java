package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.EducationTerm;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.Response.EducationTermResponse;
import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.request.EducationTermRequest;
import com.schoolmanagement.repository.EducationTermRepository;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationTermService {

    private final EducationTermRepository educationTermRepository;

    //Save Methodu
    public ResponseMessage<EducationTermResponse> save(EducationTermRequest request) {

        //Son kayit tarihi donemin baslangic tarihinden sonra olmamali
        if(request.getLastRegistrationDate().isAfter(request.getStartDate())){
            throw new ResourceNotFoundException(Messages.EDUCATION_START_DATE_IS_EARLIER_THAN_LAST_REGISTRATION_DATE);
        }

        //Bitis tarihi baslangic tarihinden once olmamali
        if(request.getEndDate().isBefore(request.getStartDate())){
            throw new ResourceNotFoundException(Messages.EDUCATION_END_DATE_IS_EARLIER_THAN_LAST_START_DATE);
        }
        //Ayni education Term ve baslangic tarihine sahip birden fazla kayit varmi kontrol et
        if(educationTermRepository.existsByTermAndYear(request.getTerm(),request.getStartDate().getYear())){
            throw new ResourceNotFoundException(Messages.EDUCATION_TERM_IS_ALREADY_EXISTS_BY_TERM_AND_YEAR_MESSAGE);
        }

        //save methoduna dto pojo donusumu yapip gonderecez.
        EducationTerm savedEducationTerm =educationTermRepository.save(createEducationTerm(request));

        //Response objesi olusturuluyor.
        return ResponseMessage.<EducationTermResponse>builder()
                .message("Education Term Olusturuldu")
                .object(createEducationTermResponse(savedEducationTerm))
                .httpStatus(HttpStatus.CREATED)
                .build();


    }

    private EducationTerm createEducationTerm(EducationTermRequest request){
        return EducationTerm.builder()
                .term(request.getTerm())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .lastRegistrationDate(request.getLastRegistrationDate())
                .build();
    }

    private EducationTermResponse createEducationTermResponse(EducationTerm response){

        return EducationTermResponse.builder()
                .id(response.getId())
                .term(response.getTerm())
                .startDate(response.getStartDate())
                .endDate(response.getEndDate())
                .lastRegistrationDate(response.getLastRegistrationDate())
                .build();
    }


    public EducationTermResponse get(Long id) {

        //Ya yoksa kontrolu
        if(!educationTermRepository.existsByIdEquals(id)){
            throw new ResourceNotFoundException(String.format(Messages.EDUCATION_TERM_NOT_FOUND_MESSAGE ,id));
        }

        //Pojo -Dto donusumu ile Response hazirlaniyor.
        return createEducationTermResponse(educationTermRepository.findByIdEquals(id));
//FindById ile calisir ama ogrenmek iicin boyle yazdik



    }


    public List<EducationTermResponse> getAll() {

        return educationTermRepository.findAll().stream()
                .map(this::createEducationTermResponse)
                .collect(Collectors.toList());
    }

    public Page<EducationTermResponse> getAllWithPage(int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());
        if(Objects.equals(type,"desc")){
            pageable=PageRequest.of(page,size, Sort.by(sort).ascending());

        }
        return educationTermRepository.findAll(pageable).map(this::createEducationTermResponse);

    }


    public ResponseMessage<?> delete(Long id) {

        //Acaba silinecek id li term var mi
        if(!educationTermRepository.existsById(id)){
            throw new ResourceNotFoundException(String.format(Messages.EDUCATION_TERM_NOT_FOUND_MESSAGE,id));
        }


        educationTermRepository.deleteById(id);
        return ResponseMessage.builder()
                .message("Education Term deleted successfully")
                .httpStatus(HttpStatus.CREATED)
                .build();
    }


    public ResponseMessage<EducationTermResponse> update(Long id, EducationTermRequest request) {

        //Acaba update edilecek idli term var mi
        if(!educationTermRepository.existsById(id)){
            throw new ResourceNotFoundException(String.format(Messages.EDUCATION_TERM_NOT_FOUND_MESSAGE,id));
        }
        //Bunu privat checkId isimli method olustur ve method uzerinden kullan.
        //save ile update kullanilan ortak kullanimlari bir method uzerinden cagir.
        //getStartDate ve lastRegistrationDate kontrolu yap.
        if(request.getStartDate() != null && request.getLastRegistrationDate() != null){
            if(request.getLastRegistrationDate().isAfter(request.getStartDate())){
                throw new ResourceNotFoundException(Messages.EDUCATION_START_DATE_IS_EARLIER_THAN_LAST_REGISTRATION_DATE);
            }
        }
        if(request.getStartDate() != null && request.getEndDate() !=null){
            if(request.getEndDate().isBefore(request.getStartDate())){
                throw new ResourceNotFoundException(Messages.EDUCATION_END_DATE_IS_EARLIER_THAN_LAST_START_DATE);
            }
        }

        ResponseMessage.ResponseMessageBuilder<EducationTermResponse> responseResponseMessageBuilder =
                ResponseMessage.builder();//Donecek olan objeyi bos olusturup sonra setliyor.

       EducationTerm updated = createUpdatedEducationTerm(id,request);
       educationTermRepository.save(updated);

       return responseResponseMessageBuilder
               .object(createEducationTermResponse(updated))
               .message("Education succesfully updated")
               .build();

    }

    private EducationTerm createUpdatedEducationTerm(Long id,EducationTermRequest educationTermRequest){
        return EducationTerm.builder()
                .id(id)
                .term(educationTermRequest.getTerm())
                .startDate(educationTermRequest.getStartDate())
                .endDate(educationTermRequest.getEndDate())
                .lastRegistrationDate(educationTermRequest.getLastRegistrationDate())
                .build();
    }























}
