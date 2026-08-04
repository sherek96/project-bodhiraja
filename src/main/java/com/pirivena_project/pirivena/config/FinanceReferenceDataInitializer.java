package com.pirivena_project.pirivena.config;

import com.pirivena_project.pirivena.modal.FundingPool;
import com.pirivena_project.pirivena.modal.IncomeCategory;
import com.pirivena_project.pirivena.repository.FundingPoolRepository;
import com.pirivena_project.pirivena.repository.IncomeCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class FinanceReferenceDataInitializer implements ApplicationRunner {

    public static final String LIBRARY_FUND = "Library Fund";
    public static final String LIBRARY_FEES = "Library Fees";

    private final FundingPoolRepository fundingPoolRepository;
    private final IncomeCategoryRepository incomeCategoryRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        fundingPoolRepository.findByNameIgnoreCase(LIBRARY_FUND).orElseGet(() -> {
            FundingPool pool = new FundingPool();
            pool.setName(LIBRARY_FUND);
            pool.setDescription("Library income and expenses, including overdue fines.");
            pool.setCurrentBalance(BigDecimal.ZERO);
            return fundingPoolRepository.save(pool);
        });

        incomeCategoryRepository.findByNameIgnoreCase(LIBRARY_FEES).orElseGet(() -> {
            IncomeCategory category = new IncomeCategory();
            category.setName(LIBRARY_FEES);
            return incomeCategoryRepository.save(category);
        });
    }
}
