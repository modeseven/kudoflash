package com.flash.repository;

import com.flash.domain.Kudo;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Kudo entity.
 */
@SuppressWarnings("unused")
public interface KudoRepository extends JpaRepository<Kudo,Long> {

    @Query("select kudo from Kudo kudo where kudo.user.login = ?#{principal.username}")
    List<Kudo> findByUserIsCurrentUser();

}
