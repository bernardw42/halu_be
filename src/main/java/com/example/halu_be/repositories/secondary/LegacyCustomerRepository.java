package com.example.halu_be.repositories.secondary;

import com.example.halu_be.models.secondary.LegacyCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LegacyCustomerRepository extends JpaRepository<LegacyCustomer, Integer> {

    Optional<LegacyCustomer> findByNationalId(String nationalId);

    // return a List of rows; even if there's only one, Spring will map each row to an Object[]
    @Query(
      value = "SELECT lc.username, lc.email " +
              "FROM legacy_customers lc " +
              "WHERE lc.id = :legacyCustomerId",
      nativeQuery = true
    )
    List<Object[]> findUsernameAndEmailByIdManual(@Param("legacyCustomerId") int legacyCustomerId);
}
