resource "azurerm_mysql_flexible_server" "matomo" {
  name                = "simple-report-${var.env}-matomo-db"
  location            = var.rg_location
  resource_group_name = var.rg_name
  sku_name            = "GP_Standard_D2ds_v4"
  version             = "8.0.21"

  administrator_login    = var.administrator_login
  administrator_password = data.azurerm_key_vault_secret.mysql_password.value

  storage {
    size_gb = 128
    auto_grow_enabled = true
  }

  backup_retention_days = 7

  delegated_subnet_id = azurerm_subnet.matomo.id
  private_dns_zone_id = azurerm_private_dns_zone.matomo.id

  tags = var.tags
}

resource "azurerm_mysql_flexible_database" "matomo" {
  charset   = "utf8"
  collation = "utf8_unicode_ci"
  name      = var.db_table
  server_name = azurerm_mysql_flexible_server.matomo.name
  resource_group_name = var.rg_name
}

resource "azurerm_mysql_flexible_server_configuration" "example" {
  name                = "require_secure_transport"
  resource_group_name = var.rg_name
  server_name         = azurerm_mysql_flexible_server.matomo.name
  value               = "OFF"
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