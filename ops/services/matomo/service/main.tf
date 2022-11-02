locals {
  app_setting_defaults = {
    "MATOMO_DATABASE_HOST"                            = var.mysql_url
    "WEBSITE_VNET_ROUTE_ALL"                          = 1
    "WEBSITE_DNS_SERVER"                              = "168.63.129.16"
    "APPINSIGHTS_INSTRUMENTATIONKEY"                  = var.ai_instrumentation_key
  }
}

resource "azurerm_app_service" "matomo" {
  name                = var.name
  resource_group_name = var.resource_group_name
  location            = var.resource_group_location
  app_service_plan_id = var.app_service_plan_id
  https_only          = true

  site_config {
    always_on        = true
    linux_fx_version = "DOCKER|bitnami/matomo:4.12.3"
    ftps_state       = "Disabled"

    ip_restriction {
      virtual_network_subnet_id = var.lb_subnet_id
      action                    = "Allow"
    }
  }

  app_settings = merge(local.app_setting_defaults, {
    "MATOMO_DATABASE_USER" = var.mysql_admin_username,
    "MATOMO_DATABASE_PASSWORD" = var.mysql_admin_password,
    "MATOMO_DATABASE_NAME" = var.mysql_matomo_db_name,
    "MATOMO_URL" = "${var.env == "prod" ? "" : "${var.env}."}simplereport.gov/matomo/",
    "MATOMO_ENABLE_ASSUME_SECURE_PROTOCOL" = "yes"
    "MATOMO_DATABASE_PORT_NUMBER" = 3306
  })

  identity {
    type = "SystemAssigned"
  }

  logs {
    http_logs {
      file_system {
        retention_in_days = 7
        retention_in_mb   = 30
      }
    }
  }
}

resource "azurerm_key_vault_access_policy" "app_secret_access" {
  key_vault_id = var.key_vault_id
  object_id    = azurerm_app_service.matomo.identity[0].principal_id
  tenant_id    = var.tenant_id

  key_permissions = [
    "get",
    "list",
  ]
  secret_permissions = [
    "get",
    "list",
  ]
  depends_on = [azurerm_app_service.matomo]
}

resource "azurerm_app_service_virtual_network_swift_connection" "metabase_vnet_integration" {
  app_service_id = azurerm_app_service.matomo.id
  subnet_id      = var.webapp_subnet_id
}

/*resource "azurerm_private_endpoint" "matomo" {
  name                = "${var.env}-matomo-endpoint"
  location            = var.resource_group_location
  resource_group_name = var.resource_group_name
  subnet_id           = var.mysql_subnet_id

  private_service_connection {
    name                           = "${var.env}-matomo-privateserviceconnection"
    private_connection_resource_id = var.mysql_server_id
    subresource_names              = [ "mysqlServer" ]
    is_manual_connection           = false
  }
}*/
