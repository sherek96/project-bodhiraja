package com.pirivena_project.pirivena.service.impl;

import com.pirivena_project.pirivena.dto.ReturnBookRequest;
import com.pirivena_project.pirivena.modal.*;
import com.pirivena_project.pirivena.repository.*;
import com.pirivena_project.pirivena.service.BookLendingService;
import com.pirivena_project.pirivena.service.TransactionIncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookLendingServiceImpl implements BookLendingService {
    private static final int MAX_ACTIVE_LOANS = 3;
    private static final BigDecimal DAILY_FINE = new BigDecimal("10.00");
    private static final String LIBRARY_FUND = "Library Fund";
    private static final String LIBRARY_FEES = "Library Fees";

    private final BookLendingRepository lendingRepository;
    private final BookRepository bookRepository;
    private final LibraryMemberRepository memberRepository;
    private final FundingPoolRepository poolRepository;
    private final IncomeCategoryRepository incomeCategoryRepository;
    private final TransactionIncomeService incomeService;

    @Override
    @Transactional
    public BookLending issue(BookLending lending) {
        refreshOverdues();
        validateIssueReferences(lending);

        LibraryMember member = memberRepository.findById(lending.getLibraryMember().getId())
                .orElseThrow(() -> new IllegalArgumentException("Library member was not found."));
        validateBorrowingEligibility(member);

        Book book = bookRepository.findByIdForUpdate(lending.getBook().getId())
                .orElseThrow(() -> new IllegalArgumentException("Book was not found."));
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("This title has no available copies.");
        }

        LocalDate issueDate = lending.getIssueDate() == null ? LocalDate.now() : lending.getIssueDate();
        LocalDate dueDate = lending.getDueDate() == null ? issueDate.plusDays(7) : lending.getDueDate();
        validateLoanDates(issueDate, dueDate);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        lending.setBook(book);
        lending.setLibraryMember(member);
        lending.setIssueDate(issueDate);
        lending.setDueDate(dueDate);
        lending.setStatus("ISSUED");
        lending.setFineAmount(BigDecimal.ZERO);
        return lendingRepository.save(lending);
    }

    private void validateIssueReferences(BookLending lending) {
        if (lending == null || lending.getBook() == null || lending.getBook().getId() == null
                || lending.getLibraryMember() == null || lending.getLibraryMember().getId() == null) {
            throw new IllegalArgumentException("Book and library member are required.");
        }
    }

    private void validateBorrowingEligibility(LibraryMember member) {
        if (!"ACTIVE".equals(member.getStatus())) {
            throw new IllegalStateException("This library member is suspended and cannot borrow books.");
        }
        if (lendingRepository.existsByLibraryMemberIdAndStatus(member.getId(), "OVERDUE")) {
            member.setStatus("SUSPENDED");
            memberRepository.save(member);
            throw new IllegalStateException("Member has an overdue book and has been suspended.");
        }
        long activeLoans = lendingRepository.countByLibraryMemberIdAndStatusIn(
                member.getId(), List.of("ISSUED", "OVERDUE"));
        if (activeLoans >= MAX_ACTIVE_LOANS) {
            throw new IllegalStateException("A library member may borrow a maximum of three active books.");
        }
    }

    private void validateLoanDates(LocalDate issueDate, LocalDate dueDate) {
        if (!dueDate.isAfter(issueDate)) {
            throw new IllegalArgumentException("Due date must be after issue date.");
        }
        if (dueDate.isAfter(issueDate.plusDays(14))) {
            throw new IllegalArgumentException("Due date cannot be more than two weeks after issue date.");
        }
    }

    @Override
    @Transactional
    public BookLending returnBook(Integer id, ReturnBookRequest request) {
        BookLending lending = lendingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lending record was not found."));
        if ("RETURNED".equals(lending.getStatus())) {
            throw new IllegalStateException("This book has already been returned.");
        }

        LocalDate returnDate = request == null || request.getReturnDate() == null
                ? LocalDate.now() : request.getReturnDate();
        if (returnDate.isBefore(lending.getIssueDate())) {
            throw new IllegalArgumentException("Return date cannot be before issue date.");
        }

        BigDecimal fine = calculateFine(lending, returnDate);
        if (fine.signum() > 0) lending.setFineIncome(recordFineIncome(lending, returnDate, fine));

        restoreBookCopy(lending);
        lending.setReturnDate(returnDate);
        lending.setFineAmount(fine);
        lending.setStatus("RETURNED");
        BookLending returnedLending = lendingRepository.saveAndFlush(lending);
        reactivateMemberWhenEligible(lending.getLibraryMember());
        return returnedLending;
    }

    private BigDecimal calculateFine(BookLending lending, LocalDate returnDate) {
        long lateDays = Math.max(0, ChronoUnit.DAYS.between(lending.getDueDate(), returnDate));
        return DAILY_FINE.multiply(BigDecimal.valueOf(lateDays));
    }

    private TransactionIncome recordFineIncome(BookLending lending, LocalDate returnDate, BigDecimal fine) {
        FundingPool pool = poolRepository.findByNameIgnoreCase(LIBRARY_FUND)
                .orElseThrow(() -> new IllegalStateException("Library Fund is not configured."));
        IncomeCategory category = incomeCategoryRepository.findByNameIgnoreCase(LIBRARY_FEES)
                .orElseThrow(() -> new IllegalStateException("Library Fees income category is not configured."));
        TransactionIncome income = new TransactionIncome();
        income.setAmount(fine);
        income.setDateReceived(returnDate);
        income.setDescription("Library overdue fine for '" + lending.getBook().getTitle() + "'");
        income.setFundingPool(pool);
        income.setIncomeCategory(category);
        return incomeService.saveIncome(income);
    }

    private void restoreBookCopy(BookLending lending) {
        Book book = bookRepository.findByIdForUpdate(lending.getBook().getId())
                .orElseThrow(() -> new IllegalArgumentException("Book was not found."));
        book.setAvailableCopies(Math.min(book.getTotalCopies(), book.getAvailableCopies() + 1));
        bookRepository.save(book);
    }

    private void reactivateMemberWhenEligible(LibraryMember member) {
        if (!lendingRepository.existsByLibraryMemberIdAndStatus(member.getId(), "OVERDUE")) {
            member.setStatus("ACTIVE");
            memberRepository.save(member);
        }
    }

    @Override
    public List<BookLending> getAll() {
        refreshOverdues();
        return lendingRepository.findAll();
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 5 0 * * *")
    public void refreshOverdues() {
        for (BookLending lending : lendingRepository.findByStatusAndDueDateBefore("ISSUED", LocalDate.now())) {
            lending.setStatus("OVERDUE");
            lending.getLibraryMember().setStatus("SUSPENDED");
            memberRepository.save(lending.getLibraryMember());
            lendingRepository.save(lending);
        }
    }
}
