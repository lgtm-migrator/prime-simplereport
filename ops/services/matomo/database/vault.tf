# Creates random password for the database
resource "azurerm_key_vault_secret" "mariadb_username" {
  key_vault_id = var.global_vault_id
  name         = "simple-report-${var.env}-matomo-db-username"
  value        = var.administrator_login
}

//TODO: Change this to use a TF-generated password, like db-password-no-phi. See #3673 for the additional work.
data "azurerm_key_vault_secret" "mariadb_password" {
  name         = "simple-report-${var.env_level}-matomo-db-password"
  key_vault_id = var.global_vault_id
}
