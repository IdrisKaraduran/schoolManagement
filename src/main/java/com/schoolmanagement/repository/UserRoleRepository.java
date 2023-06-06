package com.schoolmanagement.repository;

import com.schoolmanagement.entity.concretes.UserRole;
import com.schoolmanagement.entity.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole,Integer> {

    @Query("select r from UserRole r where r.roleType =?1")//Altta tek parametre oldugu icin 1 yazdik. 3 veya4 veya 2 parametre olsa ve biz hangi parametreyi istersek onu yazacaktik.
    Optional<UserRole> findByERoleEquals(RoleType roleType);

    @Query("select (count(r)>0) from UserRole r where r.roleType = ?1")
    boolean existsByERoleEquals(RoleType roleType);
    //?1 alttaki parametreyi al demek 2 veya 3 tane olsaydi ona gore 1 2 vey 3 yazardik.
}
