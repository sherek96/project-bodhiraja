package com.pirivena_project.pirivena;

import com.pirivena_project.pirivena.dto.ReturnBookRequest;
import com.pirivena_project.pirivena.modal.Book;
import com.pirivena_project.pirivena.modal.BookLending;
import com.pirivena_project.pirivena.modal.LibraryMember;
import com.pirivena_project.pirivena.repository.BookLendingRepository;
import com.pirivena_project.pirivena.repository.BookRepository;
import com.pirivena_project.pirivena.repository.FundingPoolRepository;
import com.pirivena_project.pirivena.repository.IncomeCategoryRepository;
import com.pirivena_project.pirivena.repository.LibraryMemberRepository;
import com.pirivena_project.pirivena.service.TransactionIncomeService;
import com.pirivena_project.pirivena.service.impl.BookLendingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookLendingServiceImplTests {

    @Mock private BookLendingRepository lendingRepository;
    @Mock private BookRepository bookRepository;
    @Mock private LibraryMemberRepository memberRepository;
    @Mock private FundingPoolRepository poolRepository;
    @Mock private IncomeCategoryRepository incomeCategoryRepository;
    @Mock private TransactionIncomeService incomeService;
    @InjectMocks private BookLendingServiceImpl service;

    @Test
    void returningLastOverdueLoanReactivatesMemberAfterLoanIsSaved() {
        LocalDate issueDate = LocalDate.of(2026, 7, 1);
        LocalDate dueDate = LocalDate.of(2026, 7, 10);

        Book book = new Book();
        book.setId(3);
        book.setTitle("Test Book");
        book.setTotalCopies(2);
        book.setAvailableCopies(1);

        LibraryMember member = new LibraryMember();
        member.setId(7);
        member.setStatus("SUSPENDED");

        BookLending lending = new BookLending();
        lending.setId(11);
        lending.setBook(book);
        lending.setLibraryMember(member);
        lending.setIssueDate(issueDate);
        lending.setDueDate(dueDate);
        lending.setStatus("OVERDUE");

        ReturnBookRequest request = new ReturnBookRequest();
        request.setReturnDate(dueDate);

        when(lendingRepository.findById(11)).thenReturn(Optional.of(lending));
        when(bookRepository.findByIdForUpdate(3)).thenReturn(Optional.of(book));
        when(lendingRepository.saveAndFlush(lending)).thenReturn(lending);
        when(lendingRepository.existsByLibraryMemberIdAndStatus(7, "OVERDUE")).thenReturn(false);

        BookLending result = service.returnBook(11, request);

        InOrder order = inOrder(lendingRepository);
        order.verify(lendingRepository).saveAndFlush(lending);
        order.verify(lendingRepository).existsByLibraryMemberIdAndStatus(7, "OVERDUE");
        verify(memberRepository).save(member);
        assertEquals("RETURNED", result.getStatus());
        assertEquals("ACTIVE", member.getStatus());
        assertEquals(2, book.getAvailableCopies());
    }
}
