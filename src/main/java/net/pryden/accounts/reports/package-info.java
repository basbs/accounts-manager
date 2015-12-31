/**
 * Reports that can be generated. Each possible file that can be written to the accounts month
 * folder (other than the {@code accounts.yaml} file which is the canonical data store) is
 * represented by a class in this package that implements the
 * {@link net.pryden.accounts.reports.Report} interface. The {@code generate-forms} command injects
 * all these reports and invokes them to generate auxiliary files.
 */
@ParametersAreNonnullByDefault
package net.pryden.accounts.reports;

import javax.annotation.ParametersAreNonnullByDefault;
