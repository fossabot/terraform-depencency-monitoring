# -- Provider
variable "region" {
  default = "dummy"
}

variable "assume_role" {
  default = "dummy"
}

variable "allowed_accounts" {
  default = "dummy"
}

# -- KMS
variable "kms_enable_key_rotation" {
  default = true
}

# -- Misc
variable "git_username" {}

variable "git_password" {}

# -- Lambda
variable "lambda_default_runtime" {
  default = "java8"
}

variable "lambda_default_memory_size" {
  default = "256"
}

variable "lambda_default_timeout" {
  default = "180"
}

# -- Lambda - gather_terraform_modules specific
variable "gather_terraform_modules_parameters_inclusions" {
  type = "list"
}

variable "gather_terraform_modules_parameters_exclusions" {
  type = "list"
}

# -- CloudWatch - gather_terraform_modules specific
variable "gather_terraform_modules_schedule_expression" {}

# -- Lambda - gather github repos specific
variable "gather_github_repos_parameters_inclusions" {
  type = "list"
}

variable "gather_github_repos_parameters_exclusions" {
  type = "list"
}

# -- CloudWatch - gather github repos specific
variable "gather_github_repos_schedule_expression" {}

# -- CloudWatch - worker dispatcher specific
variable "worker_dispatcher_schedule_expression" {}

# -- Lambda - worker specific
variable "worker_reserved_concurrent_executions" {
  default = "50"
}
variable "worker_timeout" {
  default = "300"
}

variable "worker_memory_size" {
  default = "512"
}
