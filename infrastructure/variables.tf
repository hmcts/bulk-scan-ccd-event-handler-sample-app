variable "product" {
  type    = "string"
}

variable "component" {
  type = "string"
}

variable "location_app" {
  type    = "string"
  default = "UK South"
}

variable "env" {
  type = "string"
}

variable "ilbIp" {}

variable "subscription" {}

variable "common_tags" {
  type = "map"
}

variable "deployment_namespace" {
  default = ""
}
