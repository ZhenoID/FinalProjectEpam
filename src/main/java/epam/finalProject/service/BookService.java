package epam.finalProject.service;

import epam.finalProject.entity.Author;
import epam.finalProject.entity.Book;

import java.util.List;

/**
 * Service interface for book-related operations.
 */
public interface BookService {

    /**
     * Adds a new book to the system.
     *
     * @param book the book to add
     * @return true if successful
     */
//    boolean addBook(Book book);

    /**
     * Deletes a book.
     *
     * @param book the book to delete
     * @return true if deleted successfully
     */
    boolean deleteBook(Book book);

    /**
     * Modifies an existing book.
     *
     * @param book the book with updated data
     * @return true if updated successfully
     */
    boolean changeBook(Book book);

    /**
     * Retrieves all books.
     *
     * @return list of books
     */
    List<Book> findAll();


    Book findById(Long id);

    boolean saveBookWithAuthor(Book book, Author author);
}

