package com.cargo.logistic_management.repository;

    import com.cargo.logistic_management.entity.Customer;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    @Repository
    public interface CustomerRepository extends JpaRepository<Customer, Long> {
    }