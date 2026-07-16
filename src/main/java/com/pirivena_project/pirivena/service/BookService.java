package com.pirivena_project.pirivena.service;
import com.pirivena_project.pirivena.modal.Book;
import java.util.List;
public interface BookService { Book save(Book book); List<Book> getAll(); void delete(Integer id); }
