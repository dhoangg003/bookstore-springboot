package com.example.bookstore.service;

import com.example.bookstore.model.Book;
import com.example.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    public void testDatabaseConnection() {
        // Thử lấy tất cả các sách từ database để kiểm tra kết nối
        bookRepository.findAll();
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Book addBook(Book book) {
        // Kiểm tra nếu ISBN đã tồn tại trong cơ sở dữ liệu
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new IllegalArgumentException("ISBN đã tồn tại");
        }
        return bookRepository.save(book);
    }

    public Book updateBook(Long id, Book book) {
        // Kiểm tra nếu sách không tồn tại trước khi cập nhật
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Sách không tồn tại");
        }
        book.setId(id);
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        // Kiểm tra nếu sách không tồn tại trước khi xóa
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Sách không tồn tại");
        }
        bookRepository.deleteById(id);
    }
}
