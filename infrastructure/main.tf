provider "azurerm" {
  version = "=1.42.0"
}

locals {
  is_preview          = var.env == "preview" || var.env == "spreview"
  local_env           = local.is_preview ? "aat" : var.env
  s2s_rg              = "rpe-service-auth-provider-${local.local_env}"
  s2s_url             = "http://${local.s2s_rg}.service.core-compute-${local.local_env}.internal"

  vaultName           = "bulk-scan-${var.env}"
}

module "bulk-scan-ccd-event-handler-sample-app" {
  source              = "git@github.com:hmcts/cnp-module-webapp?ref=master"
  product             = "${var.product}-${var.component}"
  location            = var.location_app
  env                 = var.env
  ilbIp               = var.ilbIp
  subscription        = var.subscription
  capacity            = var.capacity
  common_tags         = var.common_tags
  enable_ase          = var.enable_ase

  app_settings = {
    S2S_URL                 = local.s2s_url
  }
}
