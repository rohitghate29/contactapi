package project.forresume.contactapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.forresume.contactapi.domain.Contact;
import project.forresume.contactapi.service.ContactService;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static project.forresume.contactapi.constants.Constant.PHOTO_DIRECTORY;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

  private final ContactService contactService;

  @PostMapping
  public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
    return ResponseEntity.created(URI.create("/contacts/userID")).body(contactService.createContact(contact));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Contact> updateContact(@RequestBody Contact contact, @PathVariable String id) throws Exception {
    Contact updatedContact = contactService.updateContact(id, contact);
    return ResponseEntity.ok(updatedContact);
  }

  @GetMapping
  public ResponseEntity<Page<Contact>> getContacts(@RequestParam(value = "page", defaultValue = "0") int page,
                                                   @RequestParam(value = "size", defaultValue = "10") int size){
    return ResponseEntity.ok().body(contactService.getAllContacts(page, size));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Contact> getContacts(@PathVariable(value = "id") String id) {
    return ResponseEntity.ok().body(contactService.getContact(id));
  }

  @PutMapping("/photo")
  public ResponseEntity<String> uploadPhoto(@RequestParam("id") String id, @RequestParam("file")MultipartFile file) {
    return ResponseEntity.ok().body(contactService.uploadPhoto(id,file));
  }

  @GetMapping(path = "/image/{filename}", produces = {IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE})
  public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
    return Files.readAllBytes(Paths.get(PHOTO_DIRECTORY + filename));
  }
}
