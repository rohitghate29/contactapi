package project.forresume.contactapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.forresume.contactapi.domain.Contact;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {
  Optional<Contact> findById(String id);
}
