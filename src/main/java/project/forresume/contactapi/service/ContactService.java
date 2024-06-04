package project.forresume.contactapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import project.forresume.contactapi.domain.Contact;
import project.forresume.contactapi.repository.ContactRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static project.forresume.contactapi.constants.Constant.PHOTO_DIRECTORY;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ContactService {

  private final ContactRepository contactRepository;

  public Page<Contact> getAllContacts(int page, int size) {
    return contactRepository.findAll(PageRequest.of(page, size, Sort.by("name")));
  }

  public Contact getContact(String id) {
    return contactRepository.findById(id).orElseThrow(() -> new RuntimeException("contact not found"));
  }

  public Contact createContact(Contact contact) {
    return contactRepository.save(contact);
  }

  public Contact updateContact(String id, Contact contact) throws Exception {
    Optional<Contact> existingContact = contactRepository.findById(id);

    if(existingContact.isPresent()) {
      Contact updatedContact = existingContact.get();
      updatedContact.setName(contact.getName());
      updatedContact.setEmail(contact.getEmail());
      updatedContact.setAddress(contact.getAddress());
      updatedContact.setStatus(contact.getStatus());
      updatedContact.setTitle(contact.getTitle());
      updatedContact.setPhotoUrl(contact.getPhotoUrl());
      updatedContact.setPhoneNumber(contact.getPhoneNumber());

      return contactRepository.save(updatedContact);
    } else {
      throw new Exception("No contact found with id: "+ id);
    }
  }


  public void deleteContact(Contact contact) {
    contactRepository.delete(contact);
  }

  public String uploadPhoto(String id, MultipartFile file) {
    log.info("Saving picture for user ID: {}", id);
    Contact contact = getContact(id);
    String photoUrl = photoFunction.apply(id, file);
    contact.setPhotoUrl(photoUrl);
    contactRepository.save(contact);
    return photoUrl;
  }

  private final Function<String, String> fileExtension = fileName -> Optional.of(fileName).filter(name -> name.contains("."))
          .map(name -> "." + name.substring(fileName.lastIndexOf(".") + 1)).orElse(".png");

  private BiFunction<String, MultipartFile, String> photoFunction = (id, image) -> {
    String fileName = id + fileExtension.apply(image.getOriginalFilename());
    try{
      Path fileStorageLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();
      if(!Files.exists(fileStorageLocation)) { Files.createDirectories(fileStorageLocation); }
      Files.copy(image.getInputStream(), fileStorageLocation.resolve(fileName), REPLACE_EXISTING);
      return ServletUriComponentsBuilder
              .fromCurrentContextPath()
              .path("/contacts/image/" + fileName).toUriString();
    } catch (Exception e) {
      throw new RuntimeException("unable to save image");
    }
  };
}
