package com.ll.medium.domain.user.user.repository;

import com.ll.medium.domain.user.user.entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<SiteUser,Long> {


}
