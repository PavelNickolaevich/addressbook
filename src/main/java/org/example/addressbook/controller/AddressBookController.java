package org.example.addressbook.controller;

import org.example.addressbook.model.AddressBook;
import org.example.addressbook.repository.AddressBookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class AddressBookController {

    private static final Logger logger = LoggerFactory.getLogger(AddressBookController.class);
    final AddressBookRepository repository;

    @Autowired
    public AddressBookController(AddressBookRepository repository) {
        this.repository = repository;
    }


    @GetMapping("/test")
    public String getTest() {
        return "Hello world";
    }

    @GetMapping("/reactive-test")
    public Mono<String> getReactiveTest() {
        return Mono.just("Hello reactive world");
    }

    @GetMapping("addressbooks")
    public Flux<AddressBook> getAddressBook(@RequestParam("page") Optional<Integer> pageOpt,
                                            @RequestParam("size") Optional<Integer> sizeOpt) {
        Integer page = pageOpt.orElse(0);
        Integer size = sizeOpt.orElse(100);
        PageRequest pageRequest = PageRequest.of(page, size);
        logger.info("getAddressBook: {}", pageRequest);
        return repository.findAllBy(pageRequest);
    }

    @GetMapping("addressbooks/{id}")
    public Mono<AddressBook> getAddressBook(@PathVariable("id") Long id) {
        logger.info("getAddressBook: {}", id);
        return repository.findById(id);
    }

    @PostMapping("addressbook")
    @Transactional
    public Mono<AddressBook> save(@RequestBody AddressBook addressBook) {
        logger.info("Save: {}", addressBook);
        return repository.save(addressBook);
    }

    @DeleteMapping("addressbooks/{id}")
    public Mono<ResponseEntity<Object>> deleteAddressBook(@PathVariable("id") Long id) {
        logger.info("getAddressBook: {}", id);
        return repository.existsById(id)
                .flatMap(exists -> {
                    if (exists) {
                        return repository.deleteById(id)
                                .then(Mono.just(new ResponseEntity<>(HttpStatus.NO_CONTENT)))
                                .doOnSuccess(v -> logger.info("Address book with id {} deleted successfully.", id));
                    } else {
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                })
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(throwable -> {
                    logger.error("An error occurred while deleting address book with id {}: {}", id, throwable.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                });

    }

}
