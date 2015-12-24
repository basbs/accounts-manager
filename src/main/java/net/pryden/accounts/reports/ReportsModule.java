package net.pryden.accounts.reports;

import com.google.common.collect.ImmutableSet;
import dagger.Module;
import dagger.Provides;

/**
 * Module that provides bindings for {@link Report}s.
 */
@Module
public final class ReportsModule {
  @Provides
  ImmutableSet<Report> provideReports(
      AccountsReportForm accountsReportForm,
      AccountsSheetForm accountsSheetForm,
      BranchTransferForm branchTransferForm,
      CheckbookEntriesText checkbookEntriesText,
      ReconciliationText reconciliationText) {
    return ImmutableSet.of(
        accountsReportForm,
        accountsSheetForm,
        branchTransferForm,
        checkbookEntriesText,
        reconciliationText);
  }
}
