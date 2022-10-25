module "matomo_service" {
  source = "../services/matomo/service"
  name   = "${local.project}-${local.name}-${local.env}-metabase"
  env    = local.env

  mariadb_admin_username    = data.azurerm_key_vault_secret.mariadb_matomo_user.value
  mariadb_admin_password    = data.azurerm_key_vault_secret.mariadb_matomo_password.value

  resource_group_location = data.azurerm_resource_group.rg.location
  resource_group_name     = data.azurerm_resource_group.rg.name

  app_service_plan_id    = module.simple_report_api.app_service_plan_id
  webapp_subnet_id       = data.terraform_remote_state.persistent_dev3.outputs.subnet_webapp_id
  ai_instrumentation_key = data.terraform_remote_state.persistent_dev3.outputs.app_insights_instrumentation_key
  key_vault_id           = data.azurerm_key_vault.sr_global.id
  tenant_id              = data.azurerm_client_config.current.tenant_id

  mariadb_server_name = data.terraform_remote_state.persistent_dev3.outputs.matomo_server_name
  mariadb_url         = "@Microsoft.KeyVault(SecretUri=${data.azurerm_key_vault_secret.metabase_db_uri.id})"
  mariadb_server_fqdn = data.terraform_remote_state.persistent_dev3.outputs.matomo_server_fqdn
  mariadb_matomo_db_name = data.terraform_remote_state.persistent_dev3.outputs.matomo_db_name

  lb_subnet_id = data.terraform_remote_state.persistent_dev3.outputs.subnet_lbs_id
}