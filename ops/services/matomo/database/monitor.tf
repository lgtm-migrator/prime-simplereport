resource "azurerm_monitor_diagnostic_setting" "mysql" {
  name                       = "simple-report-${var.env}-matomo-db-diag"
  target_resource_id         = azurerm_mysql_flexible_server.matomo.id
  log_analytics_workspace_id = var.log_workspace_id

  log {
    category = "MySqlAuditLogs"
    enabled  = true

    retention_policy {
      enabled = false
    }
  }

  metric {
    category = "AllMetrics"
    enabled  = true

    retention_policy {
      enabled = false
    }
  }
}

