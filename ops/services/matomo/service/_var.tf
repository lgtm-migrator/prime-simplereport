variable "name" {
  type        = string
  description = "App Service Name"
}

variable "env" {
  type        = string
  description = "Target Environment"
}

variable "resource_group_name" {
  type        = string
  description = "Resource Group Name"
}

variable "resource_group_location" {
  type        = string
  description = "App Service Location"
}

variable "app_service_plan_id" {
  type        = string
  description = "App Service Plan ID"
}

variable "webapp_subnet_id" {
  type        = string
  description = "Webapp Subnet ID"
}

variable "mariadb_server_name" {
  type        = string
  description = "MariaDB Server name"
}

variable "mariadb_server_fqdn" {
  type        = string
  description = "MariaDB FQDN"
}

variable "mariadb_url" {
  type        = string
  description = "MariaDB Connection URL"
}

variable "mariadb_admin_username" {
  type        = string
  description = "MariaDB admin username"
}

variable "mariadb_admin_password" {
  type        = string
  description = "MariaDB admin password"
  sensitive   = true
}

variable "mariadb_matomo_db_name" {
  type = string
  description = "MariaDB database name for Matomo to use"
}

variable "ai_instrumentation_key" {
  type        = string
  description = "Application Insights Instrumentation Key"
  sensitive   = true
}

variable "lb_subnet_id" {}

# Secret Access
variable "key_vault_id" {}
variable "tenant_id" {}
