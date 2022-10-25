resource "azurerm_mariadb_server" "matomo" {
  name                = "simple-report-${var.env}-matomo-db"
  location            = var.rg_location
  resource_group_name = var.rg_name
  sku_name            = "GP_Gen5_4"
  version             = "10.3"

  administrator_login    = var.administrator_login
  administrator_login_password = data.azurerm_key_vault_secret.mariadb_password.value

  storage_mb                   = 5120
  backup_retention_days        = 7
  geo_redundant_backup_enabled = false
  auto_grow_enabled = true

  public_network_access_enabled = false
  ssl_enforcement_enabled = true

  tags = var.tags
}

resource "azurerm_mariadb_database" "matomo" {
  charset   = "utf8"
  collation = "utf8_general_ci"
  name      = var.db_table
  server_name = azurerm_mariadb_server.matomo.name
  resource_group_name = var.rg_name
}

/*
resource "azurerm_postgresql_flexible_server_configuration" "log_autovacuum_min_duration" {
  name      = "log_autovacuum_min_duration"
  server_id = azurerm_postgresql_flexible_server.db.id
  value     = 250
}

resource "azurerm_postgresql_flexible_server_configuration" "pg_qs_query_capture_mode" {
  name      = "pg_qs.query_capture_mode"
  server_id = azurerm_postgresql_flexible_server.db.id
  value     = "TOP"
}

resource "azurerm_postgresql_flexible_server_configuration" "pgms_wait_sampling_query_capture_mode" {
  name      = "pgms_wait_sampling.query_capture_mode"
  server_id = azurerm_postgresql_flexible_server.db.id
  value     = "ALL"
}
*/