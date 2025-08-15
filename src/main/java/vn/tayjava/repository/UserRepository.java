package vn.tayjava.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tayjava.model.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);

    @Query(value = "select u from UserEntity u where u.status='ACTIVE' " +
            " and (lower(u.firstName) like :keyword" +
            " or lower(u.lastName) like :keyword" +
            " or lower(u.username) like :keyword" +
            " or lower(u.email) like :keyword" +
            " or lower(u.phone) like :keyword)")
    Page<UserEntity> searchByKeyword(@Param("keyword")String keyword, Pageable pageable);






}
