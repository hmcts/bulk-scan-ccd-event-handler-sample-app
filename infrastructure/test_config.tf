
data "azurerm_key_vault_secret" "source_test_s2s_secret" {
  key_vault_id = "${data.azurerm_key_vault.s2s_key_vault.id}"
  name         = "microservicekey-bulk-scan-sample-app-tests"
}

resource "azurerm_key_vault_secret" "test_sample_app_s2s_secret" {
  key_vault_id = "${data.azurerm_key_vault.key_vault.id}"
  name         = "test-sample-app-s2s-secret"
  value        = "${data.azurerm_key_vault_secret.source_test_s2s_secret.value}"
}
