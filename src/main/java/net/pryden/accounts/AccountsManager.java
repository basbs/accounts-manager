package net.pryden.accounts;

final class AccountsManager {
  public static void main(String[] args) throws Exception {
    Config config = new Storage(System.getProperty("user.home")).readConfig();
    System.out.printf("Got config:\n%s\n", config);
  }
}
