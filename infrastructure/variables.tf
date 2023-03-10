variable "product" {}

variable "component" {}

variable "location_app" {
  default = "UK South"
}

variable "env" {}

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
