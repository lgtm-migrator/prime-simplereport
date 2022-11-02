resource "azurerm_subnet" "matomo" {
  name                 = "simple-report-${var.env}-matomo-subnet"
  resource_group_name  = var.rg_name
  virtual_network_name = var.vnet_name
  address_prefixes     = [cidrsubnet(var.network_address, 8, 103)] # X.X.103.0/24
  service_endpoints    = ["Microsoft.Sql"]

  enforce_private_link_endpoint_network_policies = true

  delegation {
    name = "${var.env}-db"

    service_delegation {
      name    = "Microsoft.DBforMySQL/flexibleServers"
      actions = ["Microsoft.Network/virtualNetworks/subnets/join/action"]
    }
  }
}

# The name of the private DNS zone MUST be environment-specific to support multiple envs within the same resource group.
resource "azurerm_private_dns_zone" "matomo" {
  name                = "privatelink.${var.env == var.env_level ? "" : "${var.env}."}mysql.database.azure.com"
  resource_group_name = var.rg_name
}

# DNS/VNet linkage for Flexible DB functionality
# TODO: Import the existing links for each standing environment.
resource "azurerm_private_dns_zone_virtual_network_link" "vnet_link" {
  name                  = "${var.env}-vnet-dns-link"
  resource_group_name   = var.rg_name
  private_dns_zone_name = azurerm_private_dns_zone.matomo.name
  virtual_network_id    = var.vnet_id
}