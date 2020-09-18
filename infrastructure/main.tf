provider "azurerm" {
  version = "=1.42.0"
}

data "azurerm_key_vault" "bulk_scan_key_vault" {
  name                = "bulk-scan-${var.env}"
  resource_group_name = "bulk-scan-${var.env}"
}

data "azurerm_key_vault" "s2s_key_vault" {
  name                = "s2s-${var.env}"
  resource_group_name = "rpe-service-auth-provider-${var.env}"
}

data "azurerm_key_vault_secret" "s2s_secret" {
  key_vault_id = "${data.azurerm_key_vault.s2s_key_vault.id}"
  name         = "microservicekey-bulk-scan-sample-app-tests"
}

resource "azurerm_key_vault_secret" "sample_app_s2s_secret" {
  key_vault_id = "${data.azurerm_key_vault.bulk_scan_key_vault.id}"
  name         = "sample-app-s2s-secret"
  value        = "${data.azurerm_key_vault_secret.s2s_secret.value}"
}
