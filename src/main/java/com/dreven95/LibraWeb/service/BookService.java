package com.dreven95.LibraWeb.service;

import com.dreven95.LibraWeb.models.Book;
import com.dreven95.LibraWeb.models.Person;
import com.dreven95.LibraWeb.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Page<Book> getBooks(int offset, int limit, String sortField, String sortDesc) {
        Sort sort = sortDesc.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable nextPage = PageRequest.of(offset, limit, sort);
        return bookRepository.findAll(nextPage);
    }

    public void save(Book book, MultipartFile imageFile) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                book.setImageData(imageFile.getBytes());
            }
            bookRepository.save(book);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void save(Book book, MultipartFile imageFile) {
//        try {
//            if (imageFile != null && !imageFile.isEmpty()) {
//                book.setImageData(imageFile.getBytes());
//            }
//            bookRepository.save(book);
//        } catch (IOException e) {
//            // Обработайте исключение соответствующим образом (например, залогируйте или бросьте пользовательское исключение)
//            e.printStackTrace();
//        }
//    }

    public Book findById(int id) {
        return bookRepository.findById(id).orElse(null);
    }

    public void change(int id, Book book) {
        book.setId(id);
        bookRepository.save(book);
    }

    public void delete(int id) {
        bookRepository.findById(id).ifPresent(value -> value.setPerson(null));
        bookRepository.deleteById(id);
    }

    public Person findByBookOwner(int id) {
        return bookRepository.findById(id).map(Book::getPerson).orElse(null);
    }

    @Transactional
    public void assign(int id, Person person) {
        bookRepository.findById(id).ifPresent(book -> {
            book.setPerson(person);
            book.setTimeAssign(new Date());
        });
    }

    @Transactional
    public void release(int id) {
        bookRepository.findById(id).ifPresent(book -> {
            book.setPerson(null);
            book.setTimeAssign(null);
        });
    }

    public List<Book> searchByTitle(String query) {
        return bookRepository.findByTitleStartingWith(query);
    }
}
