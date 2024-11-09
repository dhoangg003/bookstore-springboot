package com.example.bookstore.controller;

import com.example.bookstore.model.Book;
import com.example.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping("/test-connection")
    public String testConnection() {
        try {
            // Kiểm tra kết nối và in ra dữ liệu (nếu có)
            bookService.testDatabaseConnection();
            return "Database connection is successful!";
        } catch (Exception e) {
            return "Error connecting to database: " + e.getMessage();
        }
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createBook(@Valid @RequestBody Book book) {
        // Kiểm tra nếu giá là số âm
        if (book.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: Giá không được là số âm.");
        }

        try {
            // Thêm sách vào cơ sở dữ liệu
            bookService.addBook(book);
            // Trả về thông báo thành công
            return ResponseEntity.status(HttpStatus.CREATED).body("Tạo mới sách thành công!");
        } catch (IllegalArgumentException e) {
            // Trường hợp ISBN đã tồn tại
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: ISBN đã có, vui lòng sử dụng ISBN khác.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @Valid @RequestBody Book book) {
        // Kiểm tra xem sách có tồn tại không
        if (!bookService.getBookById(id).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Sách không tồn tại.");
        }

        // Cập nhật sách
        book.setId(id);
        bookService.updateBook(id, book);
        return ResponseEntity.status(HttpStatus.OK).body("Cập nhật sách thành công!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        // Kiểm tra xem sách có tồn tại không
        if (!bookService.getBookById(id).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Sách không tồn tại.");
        }

        try {
            // Xóa sách
            bookService.deleteBook(id);
            // Trả về thông báo xóa thành công
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Xóa sách thành công!");
        } catch (Exception e) {
            // Nếu có lỗi khi xóa
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xóa sách: " + e.getMessage());
        }
    }
}
