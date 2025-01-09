package org.niiish32x.sugarsms.suposperson.app.impl;

import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.api.person.dto.*;
import org.niiish32x.sugarsms.common.enums.ApiEnum;
import org.niiish32x.sugarsms.common.enums.CompanyEnum;
import org.niiish32x.sugarsms.suposperson.app.SuposPersonService;
import org.niiish32x.sugarsms.app.tools.SuposUserMocker;
import org.niiish32x.sugarsms.common.request.SuposRequestManager;
import org.niiish32x.sugarsms.common.result.Result;
import org.niiish32x.sugarsms.suposperson.app.command.SavePersonCommand;
import org.niiish32x.sugarsms.suposperson.app.external.PersonPageQueryRequest;
import org.niiish32x.sugarsms.suposperson.domain.entity.*;
import org.niiish32x.sugarsms.suposperson.domain.repo.SuposPersonRepo;
import org.niiish32x.sugarsms.user.domain.entity.UserEO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * PersonServiceImpl
 *
 * @author shenghao ni
 * @date 2024.12.08 18:41
 */

@Service
@Slf4j
public class SuposPersonServiceImpl implements SuposPersonService {

    @Resource
    SuposRequestManager requestManager;

    @Autowired
    SuposPersonRepo suposPersonRepo;


    @Override
    public Result<List<SuposPersonEO>> getAllPerson() {
        return Result.success(suposPersonRepo.findByCode());
    }

    @Override
    public Result<List<SuposPersonDTO>> searchPeronFromSupos(PersonPageQueryRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();

        try {
            if (!request.isGetAll()) {
                queryMap = request.buildQueryMap();
                HttpResponse response = requestManager.suposApiGet(ApiEnum.PERSON_GET_API_V25.value, headerMap, queryMap);
                PersonsResponse personsResponse = JSON.parseObject(response.body(), PersonsResponse.class);
                if (response.isOk()) {
                    return Result.success(personsResponse.getList());
                } else {
                    log.error("Failed to fetch persons from Supos: {}", response.body());
                    return Result.error(JSON.toJSONString(personsResponse));
                }
            }else {
                PersonsResponse res = new PersonsResponse();
                res.setList(new ArrayList<>());
                int pageNo = 1;

                while (true) {
                    queryMap = request.buildQueryMap();
                    queryMap.put("pageNo",String.valueOf(pageNo));
                    System.out.println(JSON.toJSONString(queryMap));
                    HttpResponse response = requestManager.suposApiGet(ApiEnum.PERSON_GET_API_V25.value, headerMap, queryMap);
                    PersonsResponse personsResponse = JSON.parseObject(response.body(), PersonsResponse.class);

                    if (!response.isOk()) {
                        log.error("Failed to fetch persons from Supos: {}", response.body());
                        return Result.error(JSON.toJSONString(personsResponse));
                    }

                    if (personsResponse.getList() == null || personsResponse.getList().isEmpty()) {
                        break;
                    }

                    res.getList().addAll(personsResponse.getList());
                    pageNo++;
                }

                return Result.success(res.getList());
            }

        }catch (Exception e) {
            log.error("Error occurred while fetching persons from Supos", e);
            return Result.error("An error occurred while fetching persons from Supos");
        }
    }


    @Override
    public Result<SuposPersonDTO>  getOnePersonByPersonCode(PersonCodesDTO personCodesDTO) {

        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();
        String join = String.join(",", personCodesDTO.getPersonCodes());
        queryMap.put("personCodes", join);
        queryMap.put("current","1");
        HttpResponse response = requestManager.suposApiGet(ApiEnum.PERSON_GET_API.value, headerMap, queryMap);

        PersonsResponse dto = JSON.parseObject(response.body(), PersonsResponse.class);

        return Result.success(dto.getList().get(0)) ;
    }

    @Override
    public Result addPerson(String code) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();

        SuposPersonAddRequest request = new SuposPersonAddRequest(code);

        List<SuposPersonAddRequest> list = new ArrayList<>();
        list.add(request);

        Map<String,Object> jsonMap = new HashMap<>();

        jsonMap.put("addPersons" , list);

        HttpResponse response = requestManager.suposApiPost(ApiEnum.PERSON_BATCH_POST_API.value, headerMap, queryMap,JSON.toJSONString(jsonMap));

        return response.isOk() ? Result.success(JSON.toJSONString(response.body())) : Result.error(JSON.toJSONString(response.body()));
    }

    @Override
    public Result updatePerson(SuposPersonUpdateRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();


        Map<String,Object> jsonMap = new HashMap<>();

        jsonMap.put("updatePersons" , Arrays.asList(request));

        HttpResponse response = requestManager.suposApiPost(ApiEnum.PERSON_BATCH_POST_API.value, headerMap, queryMap,JSON.toJSONString(jsonMap));

        log.info("update person : {}" ,JSON.toJSONString(response.body()));

        return response.isOk() ? Result.success(request) : Result.error("修改失败: " + JSON.toJSONString(response));
    }

    @Override
    public Result savePerson(SavePersonCommand command) {

        SuposPersonDTO personDTO = command.getSuposPersonDTO();

        GenderEO genderEO = GenderEO.builder()
                .code(personDTO.getGender().getCode())
                .name(personDTO.getGender().getName())
                .build();

        StatusEO statusEO = StatusEO.builder()
                .code(personDTO.getStatus().getCode())
                .name(personDTO.getStatus().getName())
                .build();



        MainPositionEO mainPositionEO = MainPositionEO.builder()
                .code(personDTO.getMainPosition().getCode())
                .name(personDTO.getMainPosition().getName())
                .build();


        TitleEO titleEO = null;
        if (personDTO.getTitle() != null){
            titleEO = TitleEO.builder()
                    .code(personDTO.getTitle().getCode())
                    .name(personDTO.getTitle().getName())
                    .build();
        }

        EducationEO educationEO = null;
        if (personDTO.getQualification() != null) {
            educationEO = EducationEO.builder()
                    .code(personDTO.getEducation().getCode())
                    .name(personDTO.getEducation().getName())
                    .build();
        }

        List<DepartmentEO> departmentEOS = new ArrayList<>();

        for (DepartmentDTO departmentDTO : personDTO.getDepartments()) {
            DepartmentEO departmentEO = DepartmentEO.builder()
                    .code(departmentDTO.getCode())
                    .name(departmentDTO.getName())
                    .build();
            departmentEOS.add(departmentEO);
        }

        List<CompanyEO> companyEOS = new ArrayList<>();

        for (CompanyDTO companyDTO : personDTO.getCompanies()) {
            CompanyEO companyEO = CompanyEO.builder()
                    .code(companyDTO.getCode())
                    .name(companyDTO.getName())
                    .build();
            companyEOS.add(companyEO);
        }


        UserEO userEO = null;
        if (personDTO.getUser() != null){
            userEO = UserEO.builder()
                    .personCode(personDTO.getCode())
                    .personName(personDTO.getName())
                    .build();
        }


        List<PositionEO> positionEOS = new ArrayList<>();

        for (PositionDTO positionDTO : personDTO.getPositions()) {
            PositionEO positionEO = PositionEO.builder()
                    .code(positionDTO.getCode())
                    .name(positionDTO.getName())
                    .build();
            positionEOS.add(positionEO);
        }

        DirectLeaderEO directLeaderEO = null;

        if (personDTO.getDirectLeader() != null) {
            directLeaderEO = DirectLeaderEO.builder()
                    .code(personDTO.getDirectLeader().getCode())
                    .name(personDTO.getDirectLeader().getName())
                    .build();
        }

        GrandLeaderEO grandLeaderEO = null;

        if (personDTO.getGrandLeader() != null) {
            grandLeaderEO = GrandLeaderEO.builder()
                    .code(personDTO.getGrandLeader().getCode())
                    .name(personDTO.getGrandLeader().getName())
                    .build();
        }


        SuposPersonEO suposPersonEO = SuposPersonEO.builder()
                .code(personDTO.getCode())
                .name(personDTO.getName())
                .valid(personDTO.getValid())
                .gender(genderEO)
                .status(statusEO)
                .mainPosition(mainPositionEO)
                .entryDate(personDTO.getEntryDate())
                .title(titleEO)
                .qualification(personDTO.getQualification())
                .education(educationEO)
                .major(personDTO.getMajor())
                .idNumber(personDTO.getIdNumber())
                .phone(personDTO.getPhone())
                .email(personDTO.getEmail())
                .avatarUrl(personDTO.getAvatarUrl())
                .signUrl(personDTO.getSignUrl())
                .departments(departmentEOS)
                .companies(companyEOS)
                .user(userEO)
                .positions(positionEOS)
                .modifyTime(personDTO.getModifyTime())
                .directLeader(directLeaderEO)
                .grandLeader(grandLeaderEO)
                .deleted(0)
                .build();

        boolean res = suposPersonRepo.save(suposPersonEO);

        return res ? Result.success(personDTO) : Result.error("保存失败");
    }

    @Override
    public Result syncPersonsFromSupos(CompanyEnum companyEnum) {
        PersonPageQueryRequest request = PersonPageQueryRequest.builder()
                .companyCode(companyEnum.DEFAULT.value)
                .getAll(true)
                .build();
        Result<List<SuposPersonDTO>> listResult = searchPeronFromSupos(request);

        Preconditions.checkArgument(listResult.isSuccess(),"获取人员列表失败");

        for (SuposPersonDTO suposPersonDTO : listResult.getData()) {
            SavePersonCommand savePersonCommand = new SavePersonCommand(suposPersonDTO);
            Result res = savePerson(savePersonCommand);
            Preconditions.checkArgument(res ==null || res.isSuccess(), res.getMessage());
        }

        return Result.success("同步完成");
    }

    @Override
    public Result mockPerson() {
        for (int i = 0 ; i < 10 ; i++) {
            String username = SuposUserMocker.generateUsername();
            Result res = addPerson(username);
            if(!Objects.equals(res.getCode(),200)) {
                return res;
            }
        }

        return Result.success("mock完成");
    }
}
