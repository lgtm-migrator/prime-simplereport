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

variable "mysql_server_name" {
  type        = string
  description = "MySQL Server name"
}

variable "mysql_server_fqdn" {
  type        = string
  description = "MySQL FQDN"
}

variable "mysql_url" {
  type        = string
  description = "MySQL Connection URL"
}

variable "mysql_admin_username" {
  type        = string
  description = "MySQL admin username"
}

variable "mysql_admin_password" {
  type        = string
  description = "MySQL admin password"
  sensitive   = true
}

variable "mysql_matomo_db_name" {
  type = string
  description = "MySQL database name for Matomo to use"
}

variable "mysql_subnet_id" {

}

variable "mysql_server_id" {

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
