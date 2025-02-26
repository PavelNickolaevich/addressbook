package org.example.addressbook.repository;


import org.example.addressbook.model.AddressBook;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

public interface AddressBookRepository extends ReactiveCrudRepository<AddressBook, Long> {
    Flux<AddressBook> findAllBy(Pageable pageable);
//    @NonNull
    Mono<Void> deleteById(Long id);
}
