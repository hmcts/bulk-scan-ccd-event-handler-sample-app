variable "product" {}

variable "component" {}

variable "location_app" {
  default = "UK South"
}

variable "env" {}

variable "ilbIp" {}

variable "subscription" {}

variable "capacity" {
  default = "1"
}

variable "common_tags" {
  type = map(string)
}

variable "test_s2s_name" {
  default = "bulk_scan_sample_app_tests"
}

variable "enable_ase" {
  default = false
}

variable "deployment_namespace" {
  default = ""
}
