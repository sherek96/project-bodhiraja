package com.pirivena_project.pirivena.service;
import com.pirivena_project.pirivena.dto.ReturnBookRequest;
import com.pirivena_project.pirivena.modal.BookLending;
import java.util.List;
public interface BookLendingService { BookLending issue(BookLending lending); BookLending returnBook(Integer id, ReturnBookRequest request); List<BookLending> getAll(); void refreshOverdues(); }
