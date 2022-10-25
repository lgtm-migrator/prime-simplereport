locals {
  app_setting_defaults = {
    "MATOMO_DATABASE_HOST"                            = var.mariadb_url
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
    linux_fx_version = "DOCKER|matomo:v4.12.1"
    ftps_state       = "Disabled"

    ip_restriction {
      virtual_network_subnet_id = var.lb_subnet_id
      action                    = "Allow"
    }
  }

  app_settings = merge(local.app_setting_defaults, {
    "MATOMO_DATABASE_USERNAME" = var.mariadb_admin_username,
    "MATOMO_DATABASE_PASSWORD" = var.mariadb_admin_password,
    "MATOMO_DATABASE_DBNAME" = var.mariadb_matomo_db_name
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
