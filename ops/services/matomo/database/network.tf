resource "azurerm_subnet" "matomo" {
  name                 = "simple-report-${var.env}-matomo-subnet"
  resource_group_name  = var.rg_name
  virtual_network_name = var.vnet_name
  address_prefixes     = [cidrsubnet(var.network_address, 8, 103)] # X.X.103.0/24
  service_endpoints    = ["Microsoft.Sql"]
}

resource "azurerm_mariadb_virtual_network_rule" "matomo" {
  name                = "simple-report-${var.env}-matomo-vnet-rule"
  resource_group_name = var.rg_name
  server_name         = azurerm_mariadb_server.matomo.name
  subnet_id           = azurerm_subnet.matomo.id
}

resource "azurerm_mariadb_firewall_rule" "example" {
  name                = "simple-report-${var.env}-matomo-firewall-rule"
  resource_group_name = var.rg_name
  server_name         = azurerm_mariadb_server.matomo.name

  // Enables "Allow access to Azure services"
  start_ip_address    = "0.0.0.0"
  end_ip_address      = "0.0.0.0"
}