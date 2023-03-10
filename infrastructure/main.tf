provider "azurerm" {
  features {}
}

locals {
  is_preview          = var.env == "preview" || var.env == "spreview"
  local_env           = local.is_preview ? "aat" : var.env
  s2s_rg              = "rpe-service-auth-provider-${local.local_env}"
  s2s_url             = "http://${local.s2s_rg}.service.core-compute-${local.local_env}.internal"

  vaultName           = "bulk-scan-${var.env}"
}

data "azurerm_key_vault" "bulk_scan_key_vault" {
  name                = local.vaultName
  resource_group_name = local.vaultName
}

data "azurerm_key_vault" "s2s_key_vault" {
  name                = "s2s-${var.env}"
  resource_group_name = local.s2s_rg
}

data "azurerm_key_vault_secret" "s2s_secret" {
  key_vault_id = data.azurerm_key_vault.s2s_key_vault.id
  name         = "microservicekey-bulk-scan-sample-app-tests"
}

resource "azurerm_key_vault_secret" "sample_app_s2s_secret" {
  key_vault_id = data.azurerm_key_vault.bulk_scan_key_vault.id
  name         = "sample-app-s2s-secret"
  value        = data.azurerm_key_vault_secret.s2s_secret.value
}
