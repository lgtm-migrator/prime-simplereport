output "server_name" {
  value = azurerm_mariadb_server.matomo.name
}

output "server_id" {
  value = azurerm_mariadb_server.matomo.id
}

output "server_fqdn" {
  value = azurerm_mariadb_server.matomo.fqdn
}

output "matomo_db_name" {
  value = azurerm_mariadb_database.matomo.name
}
