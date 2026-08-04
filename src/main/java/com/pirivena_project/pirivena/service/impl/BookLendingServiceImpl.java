package com.pirivena_project.pirivena.service.impl;
import com.pirivena_project.pirivena.dto.ReturnBookRequest;
import com.pirivena_project.pirivena.modal.*;
import com.pirivena_project.pirivena.repository.*;
import com.pirivena_project.pirivena.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal; import java.time.*; import java.util.*;
import java.time.temporal.ChronoUnit;
@Service @RequiredArgsConstructor
public class BookLendingServiceImpl implements BookLendingService {
 private static final int MAX_ACTIVE_LOANS=3; private static final BigDecimal DAILY_FINE=new BigDecimal("10.00");
 private final BookLendingRepository lendingRepository; private final BookRepository bookRepository; private final LibraryMemberRepository memberRepository; private final FundingPoolRepository poolRepository; private final IncomeCategoryRepository incomeCategoryRepository; private final TransactionIncomeService incomeService;
 @Transactional
 public BookLending issue(BookLending lending) {
  refreshOverdues();
  if (lending.getBook() == null || lending.getBook().getId() == null
      || lending.getLibraryMember() == null || lending.getLibraryMember().getId() == null) {
   throw new IllegalArgumentException("Book and library member are required.");
  }
  LibraryMember member = memberRepository.findById(lending.getLibraryMember().getId())
      .orElseThrow(() -> new IllegalArgumentException("Library member was not found."));
  if (!"ACTIVE".equals(member.getStatus())) {
   throw new IllegalStateException("This library member is suspended and cannot borrow books.");
  }
  if (lendingRepository.existsByLibraryMemberIdAndStatus(member.getId(), "OVERDUE")) {
   member.setStatus("SUSPENDED");
   memberRepository.save(member);
   throw new IllegalStateException("Member has an overdue book and has been suspended.");
  }
  if (lendingRepository.countByLibraryMemberIdAndStatusIn(
      member.getId(), List.of("ISSUED", "OVERDUE")) >= MAX_ACTIVE_LOANS) {
   throw new IllegalStateException(
       "A library member may borrow a maximum of three active books.");
  }
  Book book = bookRepository.findByIdForUpdate(lending.getBook().getId())
      .orElseThrow(() -> new IllegalArgumentException("Book was not found."));
  if (book.getAvailableCopies() <= 0) {
   throw new IllegalStateException("This title has no available copies.");
  }

  LocalDate issue = lending.getIssueDate() == null
      ? LocalDate.now() : lending.getIssueDate();
  LocalDate due = lending.getDueDate() == null
      ? issue.plusDays(7) : lending.getDueDate();
  if (!due.isAfter(issue)) {
   throw new IllegalArgumentException("Due date must be after issue date.");
  }
  if (due.isAfter(issue.plusDays(14))) {
   throw new IllegalArgumentException(
       "Due date cannot be more than two weeks after issue date.");
  }

  book.setAvailableCopies(book.getAvailableCopies() - 1);
  bookRepository.save(book);
  lending.setBook(book);
  lending.setLibraryMember(member);
  lending.setIssueDate(issue);
  lending.setDueDate(due);
  lending.setStatus("ISSUED");
  lending.setFineAmount(BigDecimal.ZERO);
  return lendingRepository.save(lending);
 }
 @Transactional public BookLending returnBook(Integer id, ReturnBookRequest request){ BookLending lending=lendingRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Lending record was not found.")); if("RETURNED".equals(lending.getStatus())) throw new IllegalStateException("This book has already been returned."); LocalDate returned=request.getReturnDate()==null?LocalDate.now():request.getReturnDate(); if(returned.isBefore(lending.getIssueDate())) throw new IllegalArgumentException("Return date cannot be before issue date."); long lateDays=Math.max(0,ChronoUnit.DAYS.between(lending.getDueDate(),returned)); BigDecimal fine=DAILY_FINE.multiply(BigDecimal.valueOf(lateDays)); if(fine.signum()>0){ if(request.getFundingPoolId()==null||request.getIncomeCategoryId()==null) throw new IllegalArgumentException("Funding pool and income category are required to record an overdue fine."); FundingPool pool=poolRepository.findById(request.getFundingPoolId()).orElseThrow(()->new IllegalArgumentException("Funding pool was not found.")); IncomeCategory category=incomeCategoryRepository.findById(request.getIncomeCategoryId()).orElseThrow(()->new IllegalArgumentException("Income category was not found.")); TransactionIncome income=new TransactionIncome(); income.setAmount(fine); income.setDateReceived(returned); income.setDescription("Library overdue fine for '"+lending.getBook().getTitle()+"'"); income.setFundingPool(pool); income.setIncomeCategory(category); lending.setFineIncome(incomeService.saveIncome(income)); }
 Book book=bookRepository.findByIdForUpdate(lending.getBook().getId()).orElseThrow(); book.setAvailableCopies(Math.min(book.getTotalCopies(),book.getAvailableCopies()+1)); bookRepository.save(book); lending.setReturnDate(returned); lending.setFineAmount(fine); lending.setStatus("RETURNED"); BookLending returnedLending=lendingRepository.saveAndFlush(lending); LibraryMember member=lending.getLibraryMember(); if(!lendingRepository.existsByLibraryMemberIdAndStatus(member.getId(),"OVERDUE")) { member.setStatus("ACTIVE"); memberRepository.save(member); } return returnedLending; }
 public List<BookLending> getAll(){refreshOverdues();return lendingRepository.findAll();}
 @Transactional @Scheduled(cron="0 5 0 * * *") public void refreshOverdues(){ for(BookLending l:lendingRepository.findByStatusAndDueDateBefore("ISSUED",LocalDate.now())) { l.setStatus("OVERDUE"); l.getLibraryMember().setStatus("SUSPENDED"); memberRepository.save(l.getLibraryMember()); lendingRepository.save(l); } }
}
