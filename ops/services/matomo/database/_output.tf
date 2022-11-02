output "server_name" {
  value = azurerm_mysql_flexible_server.matomo.name
}

output "server_id" {
  value = azurerm_mysql_flexible_server.matomo.id
}

output "server_fqdn" {
  value = azurerm_mysql_flexible_server.matomo.fqdn
}

output "matomo_db_name" {
  value = azurerm_mysql_flexible_database.matomo.name
}

output "subnet_id" {
  value = azurerm_subnet.matomo.id
}
